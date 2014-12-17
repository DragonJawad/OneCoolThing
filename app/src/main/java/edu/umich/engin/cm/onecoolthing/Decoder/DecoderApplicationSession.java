package edu.umich.engin.cm.onecoolthing.Decoder;

/**
 * Created by jawad on 16/12/14.
 */
/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.PIXEL_FORMAT;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Vuforia.UpdateCallbackInterface;


public class DecoderApplicationSession implements UpdateCallbackInterface
{

    private static final String LOGTAG = "MD/Vuforia_Sample_Applications";

    // Reference to the current activity
    private Activity mActivity;
    private DecoderApplicationControl mSessionControl;

    // Flags
    private boolean mStarted = false;
    private boolean mCameraRunning = false;

    // Display size of the device:
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    // The async tasks to initialize the Vuforia SDK:
    private InitVuforiaTask mInitVuforiaTask;
    private LoadTrackerTask mLoadTrackerTask;

    // An object used for synchronizing Vuforia initialization, dataset loading
    // and the Android onDestroy() life cycle event. If the application is
    // destroyed while a data set is still being loaded, then we wait for the
    // loading operation to finish before shutting down Vuforia:
    private Object mShutdownLock = new Object();

    // Vuforia initialization flags:
    private int mVuforiaFlags = 0;

    // Holds the camera configuration to use upon resuming
    private int mCamera = CameraDevice.CAMERA.CAMERA_DEFAULT;

    // Stores the projection matrix to use for rendering purposes
    private Matrix44F mProjectionMatrix;

    // Stores orientation
    private boolean mIsPortrait = false;


    public DecoderApplicationSession(DecoderApplicationControl sessionControl)
    {
        mSessionControl = sessionControl;
    }


    // Initializes Vuforia and sets up preferences.
    public void initAR(Activity activity, int screenOrientation)
    {
        DecoderApplicationException vuforiaException = null;
        mActivity = activity;

        if ((screenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR)
                && (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO))
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;

        Log.d(LOGTAG, "Got to Checkpoint A");

        // Apply screen orientation
        mActivity.setRequestedOrientation(screenOrientation);

        updateActivityOrientation();

        // Query display dimensions:
        storeScreenDimensions();

        Log.d(LOGTAG, "Got to Checkpoint B");

        // As long as this window is visible to the user, keep the device's
        // screen turned on and bright:
        mActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mVuforiaFlags = Vuforia.GL_20;

        Log.d(LOGTAG, "Got to Checkpoint C");

        // Initialize Vuforia SDK asynchronously to avoid blocking the
        // main (UI) thread.
        //
        // NOTE: This task instance must be created and invoked on the
        // UI thread and it can be executed only once!
        if (mInitVuforiaTask != null)
        {
            String logMessage = "Cannot initialize SDK twice";
            vuforiaException = new DecoderApplicationException(
                    DecoderApplicationException.VUFORIA_ALREADY_INITIALIZATED,
                    logMessage);
            Log.e(LOGTAG, logMessage);
        }

        Log.d(LOGTAG, "Got to Checkpoint D");

        if (vuforiaException == null)
        {
            try
            {
                mInitVuforiaTask = new InitVuforiaTask();
                mInitVuforiaTask.execute();
            } catch (Exception e)
            {
                String logMessage = "Initializing Vuforia SDK failed";
                vuforiaException = new DecoderApplicationException(
                        DecoderApplicationException.INITIALIZATION_FAILURE,
                        logMessage);
                Log.e(LOGTAG, logMessage);
            }
        }

        Log.d(LOGTAG, "Got to Checkpoint E");

        if (vuforiaException != null)
            mSessionControl.onInitARDone(vuforiaException);

        Log.d(LOGTAG, "Got to Checkpoint F");
    }


    // Starts Vuforia, initialize and starts the camera and start the trackers
    public void startAR(int camera) throws DecoderApplicationException
    {
        Log.d(LOGTAG, "startAr()");

        String error;
        if(mCameraRunning)
        {
            error = "Camera already running, unable to open again";
            Log.e(LOGTAG, error);
            throw new DecoderApplicationException(
                    DecoderApplicationException.CAMERA_INITIALIZATION_FAILURE, error);
        }

        mCamera = camera;
        if (!CameraDevice.getInstance().init(camera))
        {
            error = "Unable to open camera device: " + camera;
            Log.e(LOGTAG, error);
            throw new DecoderApplicationException(
                    DecoderApplicationException.CAMERA_INITIALIZATION_FAILURE, error);
        }

        configureVideoBackground();

        if (!CameraDevice.getInstance().selectVideoMode(
                CameraDevice.MODE.MODE_DEFAULT))
        {
            error = "Unable to set video mode";
            Log.e(LOGTAG, error);
            throw new DecoderApplicationException(
                    DecoderApplicationException.CAMERA_INITIALIZATION_FAILURE, error);
        }

        if (!CameraDevice.getInstance().start())
        {
            error = "Unable to start camera device: " + camera;
            Log.e(LOGTAG, error);
            throw new DecoderApplicationException(
                    DecoderApplicationException.CAMERA_INITIALIZATION_FAILURE, error);
        }

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

        setProjectionMatrix();

        mSessionControl.doStartTrackers();

        mCameraRunning = true;

        try
        {
            setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
        } catch (DecoderApplicationException exceptionTriggerAuto)
        {
            setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
        }
    }


    // Stops any ongoing initialization, stops Vuforia
    public void stopAR() throws DecoderApplicationException
    {
        Log.d(LOGTAG, "stopAR()");

        // Cancel potentially running tasks
        if (mInitVuforiaTask != null
                && mInitVuforiaTask.getStatus() != InitVuforiaTask.Status.FINISHED)
        {
            mInitVuforiaTask.cancel(true);
            mInitVuforiaTask = null;
        }

        if (mLoadTrackerTask != null
                && mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED)
        {
            mLoadTrackerTask.cancel(true);
            mLoadTrackerTask = null;
        }

        mInitVuforiaTask = null;
        mLoadTrackerTask = null;

        mStarted = false;

        stopCamera();

        // Ensure that all asynchronous operations to initialize Vuforia
        // and loading the tracker datasets do not overlap:
        synchronized (mShutdownLock)
        {

            boolean unloadTrackersResult;
            boolean deinitTrackersResult;

            // Destroy the tracking data set:
            unloadTrackersResult = mSessionControl.doUnloadTrackersData();

            // Deinitialize the trackers:
            deinitTrackersResult = mSessionControl.doDeinitTrackers();

            // Deinitialize Vuforia SDK:
            Vuforia.deinit();

            if (!unloadTrackersResult)
                throw new DecoderApplicationException(
                        DecoderApplicationException.UNLOADING_TRACKERS_FAILURE,
                        "Failed to unload trackers\' data");

            if (!deinitTrackersResult)
                throw new DecoderApplicationException(
                        DecoderApplicationException.TRACKERS_DEINITIALIZATION_FAILURE,
                        "Failed to deinitialize trackers");

        }
    }

    // Resumes Vuforia, restarts the trackers and the camera
    public void resumeAR() throws DecoderApplicationException
    {
        Log.d(LOGTAG, "resumeAR()");

        // Vuforia-specific resume operation
        Vuforia.onResume();

        if (mStarted)
        {
            startAR(mCamera);
        }
    }


    // Pauses Vuforia and stops the camera
    public void pauseAR() throws DecoderApplicationException
    {
        Log.d(LOGTAG, "pauseAr()");

        if (mStarted)
        {
            stopCamera();
        }

        Vuforia.onPause();
    }


    // Gets the projection matrix to be used for rendering
    public Matrix44F getProjectionMatrix()
    {
        return mProjectionMatrix;
    }


    // Callback called every cycle
    @Override
    public void QCAR_onUpdate(State s)
    {
        mSessionControl.onQCARUpdate(s);
    }


    // Manages the configuration changes
    public void onConfigurationChanged()
    {
        Log.d(LOGTAG, "onConfigurationChanged()");

        updateActivityOrientation();

        storeScreenDimensions();

        if (isARRunning())
        {
            // configure video background
            configureVideoBackground();

            // Update projection matrix:
            setProjectionMatrix();
        }

    }


    // Methods to be called to handle lifecycle
    public void onResume()
    {
        Log.d(LOGTAG, "onResume()");
        Vuforia.onResume();
    }


    public void onPause()
    {
        Log.d(LOGTAG, "onPause");
        Vuforia.onPause();
    }


    public void onSurfaceChanged(int width, int height)
    {
        Vuforia.onSurfaceChanged(width, height);
    }


    public void onSurfaceCreated()
    {
        Vuforia.onSurfaceCreated();
    }

    // An async task to initialize Vuforia asynchronously.
    private class InitVuforiaTask extends AsyncTask<Void, Integer, Boolean>
    {
        // Initialize with invalid value:
        private int mProgressValue = -1;


        protected Boolean doInBackground(Void... params)
        {
            Log.d(LOGTAG, "doInBackground() initVuforiaTask");

            // Prevent the onDestroy() method to overlap with initialization:
            synchronized (mShutdownLock)
            {
                Log.d(LOGTAG, "Pt-A doInBackground() initVuforiaTask");

                Vuforia.setInitParameters(mActivity, mVuforiaFlags);

                Log.d(LOGTAG, "Pt-B doInBackground() initVuforiaTask");

                do
                {
                    Log.d(LOGTAG, "Pt-C doInBackground() initVuforiaTask: " + mProgressValue);
                    // Vuforia.init() blocks until an initialization step is
                    // complete, then it proceeds to the next step and reports
                    // progress in percents (0 ... 100%).
                    // If Vuforia.init() returns -1, it indicates an error.
                    // Initialization is done when progress has reached 100%.
                    mProgressValue = Vuforia.init();

                    Log.d(LOGTAG, "Pt-D doInBackground() initVuforiaTask: " + mProgressValue);

                    // Publish the progress value:
                    publishProgress(mProgressValue);

                    Log.d(LOGTAG, "Pt-E doInBackground() initVuforiaTask: " + mProgressValue);

                    // We check whether the task has been canceled in the
                    // meantime (by calling AsyncTask.cancel(true)).
                    // and bail out if it has, thus stopping this thread.
                    // This is necessary as the AsyncTask will run to completion
                    // regardless of the status of the component that
                    // started is.
                } while (!isCancelled() && mProgressValue >= 0
                        && mProgressValue < 100);

                Log.d(LOGTAG, "Pt-F doInBackground() initVuforiaTask: " + mProgressValue);

                return (mProgressValue > 0);
            }
        }


        protected void onProgressUpdate(Integer... values)
        {
            // Do something with the progress value "values[0]", e.g. update
            // splash screen, progress bar, etc.

            Log.d(LOGTAG, "ProgressUpdate: "+values);
        }


        protected void onPostExecute(Boolean result)
        {
            // Done initializing Vuforia, proceed to next application
            // initialization status:

            DecoderApplicationException vuforiaException = null;

            if (result)
            {
                Log.d(LOGTAG, "InitVuforiaTask.onPostExecute: Vuforia "
                        + "initialization successful");

                boolean initTrackersResult;
                initTrackersResult = mSessionControl.doInitTrackers();

                if (initTrackersResult)
                {
                    try
                    {
                        mLoadTrackerTask = new LoadTrackerTask();
                        mLoadTrackerTask.execute();
                    } catch (Exception e)
                    {
                        String logMessage = "Loading tracking data set failed";
                        vuforiaException = new DecoderApplicationException(
                                DecoderApplicationException.LOADING_TRACKERS_FAILURE,
                                logMessage);
                        Log.e(LOGTAG, logMessage);
                        mSessionControl.onInitARDone(vuforiaException);
                    }

                } else
                {
                    vuforiaException = new DecoderApplicationException(
                            DecoderApplicationException.TRACKERS_INITIALIZATION_FAILURE,
                            "Failed to initialize trackers");
                    mSessionControl.onInitARDone(vuforiaException);
                }
            } else
            {
                String logMessage;

                // NOTE: Check if initialization failed because the device is
                // not supported. At this point the user should be informed
                // with a message.
                if (mProgressValue == Vuforia.INIT_DEVICE_NOT_SUPPORTED)
                {
                    logMessage = "Failed to initialize Vuforia because this "
                            + "device is not supported.";
                } else
                {
                    logMessage = "Failed to initialize Vuforia.";
                }

                // Log error:
                Log.e(LOGTAG, "InitVuforiaTask.onPostExecute: " + logMessage
                        + " Exiting.");

                // Send Vuforia Exception to the application and call initDone
                // to stop initialization process
                vuforiaException = new DecoderApplicationException(
                        DecoderApplicationException.INITIALIZATION_FAILURE,
                        logMessage);
                mSessionControl.onInitARDone(vuforiaException);
            }
        }
    }

    // An async task to load the tracker data asynchronously.
    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(Void... params)
        {
            Log.d(LOGTAG, "doInBackground() LoadTrackerTask");

            // Prevent the onDestroy() method to overlap:
            synchronized (mShutdownLock)
            {
                // Load the tracker data set:
                return mSessionControl.doLoadTrackersData();
            }
        }


        protected void onPostExecute(Boolean result)
        {

            DecoderApplicationException vuforiaException = null;

            Log.d(LOGTAG, "LoadTrackerTask.onPostExecute: execution "
                    + (result ? "successful" : "failed"));

            if (!result)
            {
                String logMessage = "Failed to load tracker data.";
                // Error loading dataset
                Log.e(LOGTAG, logMessage);
                vuforiaException = new DecoderApplicationException(
                        DecoderApplicationException.LOADING_TRACKERS_FAILURE,
                        logMessage);
            } else
            {
                // Hint to the virtual machine that it would be a good time to
                // run the garbage collector:
                //
                // NOTE: This is only a hint. There is no guarantee that the
                // garbage collector will actually be run.
                System.gc();

                Vuforia.registerCallback(DecoderApplicationSession.this);

                mStarted = true;
            }

            // Done loading the tracker, update application status, send the
            // exception to check errors
            mSessionControl.onInitARDone(vuforiaException);
        }
    }


    // Stores screen dimensions
    private void storeScreenDimensions()
    {
        // Query display dimensions:
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }


    // Stores the orientation depending on the current resources configuration
    private void updateActivityOrientation()
    {
        Configuration config = mActivity.getResources().getConfiguration();

        switch (config.orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                mIsPortrait = true;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                mIsPortrait = false;
                break;
            case Configuration.ORIENTATION_UNDEFINED:
            default:
                break;
        }

        Log.i(LOGTAG, "Activity is in "
                + (mIsPortrait ? "PORTRAIT" : "LANDSCAPE"));
    }


    // Method for setting / updating the projection matrix for AR content
    // rendering
    private void setProjectionMatrix()
    {
        CameraCalibration camCal = CameraDevice.getInstance()
                .getCameraCalibration();
        mProjectionMatrix = Tool.getProjectionGL(camCal, 10.0f, 5000.0f);
    }


    public void stopCamera()
    {
        if(mCameraRunning)
        {
            mSessionControl.doStopTrackers();
            CameraDevice.getInstance().stop();
            CameraDevice.getInstance().deinit();
            mCameraRunning = false;
        }
    }


    // Applies auto focus if supported by the current device
    private boolean setFocusMode(int mode) throws DecoderApplicationException
    {
        boolean result = CameraDevice.getInstance().setFocusMode(mode);

        if (!result)
            throw new DecoderApplicationException(
                    DecoderApplicationException.SET_FOCUS_MODE_FAILURE,
                    "Failed to set focus mode: " + mode);

        return result;
    }


    // Configures the video mode and sets offsets for the camera's image
    private void configureVideoBackground()
    {
        CameraDevice cameraDevice = CameraDevice.getInstance();
        VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);

        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setSynchronous(true);
        config.setPosition(new Vec2I(0, 0));

        int xSize = 0, ySize = 0;
        if (mIsPortrait)
        {
            xSize = (int) (vm.getHeight() * (mScreenHeight / (float) vm
                    .getWidth()));
            ySize = mScreenHeight;

            if (xSize < mScreenWidth)
            {
                xSize = mScreenWidth;
                ySize = (int) (mScreenWidth * (vm.getWidth() / (float) vm
                        .getHeight()));
            }
        } else
        {
            xSize = mScreenWidth;
            ySize = (int) (vm.getHeight() * (mScreenWidth / (float) vm
                    .getWidth()));

            if (ySize < mScreenHeight)
            {
                xSize = (int) (mScreenHeight * (vm.getWidth() / (float) vm
                        .getHeight()));
                ySize = mScreenHeight;
            }
        }

        config.setSize(new Vec2I(xSize, ySize));

        Log.i(LOGTAG, "Configure Video Background : Video (" + vm.getWidth()
                + " , " + vm.getHeight() + "), Screen (" + mScreenWidth + " , "
                + mScreenHeight + "), mSize (" + xSize + " , " + ySize + ")");

        Renderer.getInstance().setVideoBackgroundConfig(config);

    }


    // Returns true if Vuforia is initialized, the trackers started and the
    // tracker data loaded
    private boolean isARRunning()
    {
        return mStarted;
    }

}

