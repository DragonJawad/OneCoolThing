package edu.umich.engin.cm.onecoolthing.Decoder;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;

import java.util.ArrayList;
import java.util.Vector;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 16/12/14.
 */
public class DecoderActivity extends Activity implements DecoderApplicationControl,
            DecoderAppMenuInterface {
    private static final String LOG = "MD/DecoderActivity";

    // Camera status constants
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;

    // Decoder session that handles all the gritty work
    DecoderApplicationSession mDecoderSession;

    // The overarching layout container
    private RelativeLayout mLayoutContainer;
    // Holds all the UI elements
    private RelativeLayout mUIContainer;
    // Handles the loading dialog
    LoadingDialogHandler loadingDialogHandler;
    private View mFlashOptionView;

    // The textures we will use for rendering:
    private Vector<Texture> mTextures;

    // Our OpenGL view:
    private DecoderApplicationGLView mGlView;
    // Our renderer:
    private ImageTargetRenderer mRenderer;

    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = false;
    private boolean mExtendedTracking = false;

    private DecoderAppMenu mAppMenu;

    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private int mStartDatasetsIndex = 0;
    private int mDatasetsNumber = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        // Cache the necessary views to setup later
        mLayoutContainer = (RelativeLayout) findViewById(R.id.container);
        mUIContainer = (RelativeLayout) findViewById(R.id.camera_overlay_layout);

        // Create a loading animation while the vuforia loads
        startLoadingAnimation();

        // Initialize the actual Vuforia session
        initVuforiaSession();
    }

    // Initializes the loading animation
    private void startLoadingAnimation()
    {
        mUIContainer.setVisibility(View.VISIBLE);
        mUIContainer.setBackgroundColor(Color.BLACK);

        loadingDialogHandler = new LoadingDialogHandler(this);

        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUIContainer
                .findViewById(R.id.loading_indicator);

        // Shows the loading indicator at start
        loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
    }

    // Loads in the textures
    // TODO: What are these for again? Are they necessary?
    // We want to load specific textures from the APK, which we will later use
    // for rendering.
    private void loadTextures()
    {
        mTextures = new Vector<Texture>();

        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png",
                getAssets()));
    }

    private void initVuforiaSession() {
        // Load in any necessary textures
        loadTextures();

        // Add data in
        addData();

        // Create the new session and start it up
        mDecoderSession = new DecoderApplicationSession(this);
        mDecoderSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void addData() {
        mDatasetStrings.add("StonesAndChips.xml");
        mDatasetStrings.add("Tarmac.xml");
    }

    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new DecoderApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new ImageTargetRenderer(this, mDecoderSession);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);

    }

    @Override
    public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ImageTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                    LOG,
                    "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOG, "Tracker successfully initialized");
        }
        return result;
    }

    @Override
    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) tManager
                .getTracker(ImageTracker.getClassType());
        if (imageTracker == null)
            return false;

        if (mCurrentDataset == null)
            mCurrentDataset = imageTracker.createDataSet();

        if (mCurrentDataset == null)
            return false;

        if (!mCurrentDataset.load(
                mDatasetStrings.get(mCurrentDatasetSelectionIndex),
                STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;

        if (!imageTracker.activateDataSet(mCurrentDataset))
            return false;

        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++)
        {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if(isExtendedTrackingActive())
            {
                trackable.startExtendedTracking();
            }

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOG, "UserData:Set the following user data "
                    + (String) trackable.getUserData());
        }

        return true;
    }

    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        boolean result = true;

        Tracker imageTracker = TrackerManager.getInstance().getTracker(
                ImageTracker.getClassType());
        if (imageTracker != null)
            imageTracker.start();

        return result;
    }

    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;

        Tracker imageTracker = TrackerManager.getInstance().getTracker(
                ImageTracker.getClassType());
        if (imageTracker != null)
            imageTracker.stop();

        return result;
    }

    @Override
    public boolean doUnloadTrackersData() {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) tManager
                .getTracker(ImageTracker.getClassType());
        if (imageTracker == null)
            return false;

        if (mCurrentDataset != null && mCurrentDataset.isActive())
        {
            if (imageTracker.getActiveDataSet().equals(mCurrentDataset)
                    && !imageTracker.deactivateDataSet(mCurrentDataset))
            {
                result = false;
            } else if (!imageTracker.destroyDataSet(mCurrentDataset))
            {
                result = false;
            }

            mCurrentDataset = null;
        }

        return result;
    }

    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ImageTracker.getClassType());

        return result;
    }

    @Override
    public void onInitARDone(DecoderApplicationException exception) {
        if (exception == null)
        {
            initApplicationAR();

            mRenderer.mIsActive = true;

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            mLayoutContainer.addView(mGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            // Sets the UILayout to be drawn in front of the camera
            mUIContainer.bringToFront();

            // Sets the layout background to transparent
            mUIContainer.setBackgroundColor(Color.TRANSPARENT);

            try
            {
                mDecoderSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
            } catch (DecoderApplicationException e)
            {
                Log.e(LOG, e.getString());
            }

            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

            if (result)
                mContAutofocus = true;
            else
                Log.e(LOG, "Unable to enable continuous autofocus");

            mAppMenu = new DecoderAppMenu(this, this, "Image Targets",
                    mGlView, mUIContainer, null);
            setSampleAppMenuSettings();

        } else
        {
            Log.e(LOG, exception.getString());
            // TODO: Do something if there's an exception! finish()
        }
    }

    // This method sets the menu's settings
    private void setSampleAppMenuSettings()
    {
        DecoderAppMenuGroup group;

        group = mAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);

        group = mAppMenu.addGroup("", true);
        group.addSelectionItem(getString(R.string.menu_extended_tracking),
                CMD_EXTENDED_TRACKING, false);
        group.addSelectionItem(getString(R.string.menu_contAutofocus),
                CMD_AUTOFOCUS, mContAutofocus);
        mFlashOptionView = group.addSelectionItem(
                getString(R.string.menu_flash), CMD_FLASH, false);

        Camera.CameraInfo ci = new Camera.CameraInfo();
        boolean deviceHasFrontCamera = false;
        boolean deviceHasBackCamera = false;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                deviceHasFrontCamera = true;
            else if (ci.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                deviceHasBackCamera = true;
        }

        if (deviceHasBackCamera && deviceHasFrontCamera)
        {
            group = mAppMenu.addGroup(getString(R.string.menu_camera),
                    true);
            group.addRadioItem(getString(R.string.menu_camera_front),
                    CMD_CAMERA_FRONT, false);
            group.addRadioItem(getString(R.string.menu_camera_back),
                    CMD_CAMERA_REAR, true);
        }

        group = mAppMenu
                .addGroup(getString(R.string.menu_datasets), true);
        mStartDatasetsIndex = CMD_DATASET_START_INDEX;
        mDatasetsNumber = mDatasetStrings.size();

        group.addRadioItem("Stones & Chips", mStartDatasetsIndex, true);
        group.addRadioItem("Tarmac", mStartDatasetsIndex + 1, false);

        mAppMenu.attachMenu();
    }

    boolean isExtendedTrackingActive()
    {
        return mExtendedTracking;
    }

    @Override
    public void onQCARUpdate(State state) {
        if (mSwitchDatasetAsap)
        {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ImageTracker it = (ImageTracker) tm.getTracker(ImageTracker
                    .getClassType());
            if (it == null || mCurrentDataset == null
                    || it.getActiveDataSet() == null)
            {
                Log.d(LOG, "Failed to swap datasets");
                return;
            }

            doUnloadTrackersData();
            doLoadTrackersData();
        }
    }

    @Override
    public boolean menuProcess(int command) {
        boolean result = true;

        switch (command)
        {
            case CMD_BACK:
                // TODO: finish();
                break;

            case CMD_FLASH:
                result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);

                if (result)
                {
                    mFlash = !mFlash;
                } else
                {
                    showToast(getString(mFlash ? R.string.menu_flash_error_off
                            : R.string.menu_flash_error_on));
                    Log.e(LOG,
                            getString(mFlash ? R.string.menu_flash_error_off
                                    : R.string.menu_flash_error_on));
                }
                break;

            case CMD_AUTOFOCUS:

                if (mContAutofocus)
                {
                    result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);

                    if (result)
                    {
                        mContAutofocus = false;
                    } else
                    {
                        showToast(getString(R.string.menu_contAutofocus_error_off));
                        Log.e(LOG,
                                getString(R.string.menu_contAutofocus_error_off));
                    }
                } else
                {
                    result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                    if (result)
                    {
                        mContAutofocus = true;
                    } else
                    {
                        showToast(getString(R.string.menu_contAutofocus_error_on));
                        Log.e(LOG,
                                getString(R.string.menu_contAutofocus_error_on));
                    }
                }

                break;

            case CMD_CAMERA_FRONT:
            case CMD_CAMERA_REAR:

                // Turn off the flash
                if (mFlashOptionView != null && mFlash)
                {
                    // OnCheckedChangeListener is called upon changing the checked state
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    {
                        ((Switch) mFlashOptionView).setChecked(false);
                    } else
                    {
                        ((CheckBox) mFlashOptionView).setChecked(false);
                    }
                }

                mDecoderSession.stopCamera();

                try
                {
                    mDecoderSession
                            .startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA.CAMERA_FRONT
                                    : CameraDevice.CAMERA.CAMERA_BACK);
                } catch (DecoderApplicationException e)
                {
                    showToast(e.getString());
                    Log.e(LOG, e.getString());
                    result = false;
                }
                doStartTrackers();
                break;

            case CMD_EXTENDED_TRACKING:
                for (int tIdx = 0; tIdx < mCurrentDataset.getNumTrackables(); tIdx++)
                {
                    Trackable trackable = mCurrentDataset.getTrackable(tIdx);

                    if (!mExtendedTracking)
                    {
                        if (!trackable.startExtendedTracking())
                        {
                            Log.e(LOG,
                                    "Failed to start extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOG,
                                    "Successfully started extended tracking target");
                        }
                    } else
                    {
                        if (!trackable.stopExtendedTracking())
                        {
                            Log.e(LOG,
                                    "Failed to stop extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOG,
                                    "Successfully started extended tracking target");
                        }
                    }
                }

                if (result)
                    mExtendedTracking = !mExtendedTracking;

                break;

            default:
                if (command >= mStartDatasetsIndex
                        && command < mStartDatasetsIndex + mDatasetsNumber)
                {
                    mSwitchDatasetAsap = true;
                    mCurrentDatasetSelectionIndex = command
                            - mStartDatasetsIndex;
                }
                break;
        }

        return result;
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
