package edu.umich.engin.cm.onecoolthing.NetworkUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jawad on 08/11/14.
 *
 * Simplified ImageLoader class which loads an image with the
 *      specific parameters, but does not cache the image at all,
 *      as unnecessary for this app
 */
public class ImageLoaderNoCache {
    // Handler to display images in UI thread
    Handler mHandler = new Handler();

    ExecutorService mExecutorService;

    // If not -1, then determines what scale to use the images at
    int mGivenScale = -1;

    // Manager(s) to be notified once data is loaded
    LoaderManager mManagers[];

    // Specialized manager to be notified of params to be passed back
    SpecializedManager mSpecialManager;

    // Interface so that user can be notified once data has been finally applied
    public interface LoaderManager {
        // Notifies the manager that the data was loaded
        public void notifyDataLoaded();

        // Give the retrieved bitMap to the manager(s)
            // Note: notifyDataLoaded() will be called first, if used
        public void notifyRetrievedBitmap(Bitmap bitmap);
    }

   // Interface for a special manager who needs params back with bitmap
    public interface SpecializedManager {
       // Gives back the bitmap as well as stored params
       public void notifyRetrievedBitmap(Bitmap bitmap, int paramA, int paramB);
   }

    // Default constructor
    public ImageLoaderNoCache() {
        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        mExecutorService = Executors.newFixedThreadPool(5);
    }

    // Constructor if using all-seeing managers
    public ImageLoaderNoCache(LoaderManager managers[]) {
        this();

        this.mManagers = managers;
    }

    // Display an image from a url on a specified imageView
    public void DisplayImage(String url, ImageView imageView) {
        // Create the necessary task
        LoadIntoImageViewTask task = new LoadIntoImageViewTask(url, imageView);

        // Put the task into the que
        mExecutorService.submit(new BitmapLoader(task));
    }

    // Simply notifies the manager(s) of the image, once retrieved
    public void GetImage(String url, LoaderManager managers[]) {
        // Create a simple notifier task
        NotifyRetrievedTask task = new NotifyRetrievedTask(url, managers);

        // Put the task into the que
        mExecutorService.submit(new BitmapLoader(task));
    }

    // Notifies the specializedManager once the bitmap is received, and returns paramA and paramB at same time
    public void GetImage(String url, SpecializedManager specializedManager, int paramA, int paramB) {
        // Create a specialized notifier task
        NotifySpecialReceivedTask task = new NotifySpecialReceivedTask(url, specializedManager, paramA, paramB);

        // Put the task into the que
        mExecutorService.submit(new BitmapLoader(task));
    }

    // Abstract class, to load images
    private abstract class LoadImageTask {
        // Gets the Url necessary to get this image
        public abstract String GetUrl();

        // Loads an image finally onto thea target
        public abstract void LoadImage(Bitmap bitmap);
    }

    // Represents a task to load an image into an imageView
    private class LoadIntoImageViewTask extends LoadImageTask {
        // The target url to get an image from
        String mUrl;

        // The target imageView to load the image into
        ImageView mTargetImageView;

        // Default constructor
        LoadIntoImageViewTask(String url, ImageView imageView) {
            this.mUrl = url;
            this.mTargetImageView = imageView;
        }

        @Override
        public String GetUrl() {
            return mUrl;
        }

        @Override
        public void LoadImage(Bitmap bitmap) {
            // Set the bitmap onto the imageView
            mTargetImageView.setImageBitmap(bitmap);
        }
    }

    // Simply notifies the manager(s) of the retrieved image
    private class NotifyRetrievedTask extends LoadImageTask {
        String mUrl;
        LoaderManager mManagers[];

        // Default constructor
        NotifyRetrievedTask(String url, LoaderManager managers[]) {
            this.mUrl = url;
            this.mManagers = managers;
        }

        @Override
        public String GetUrl() {
            return mUrl;
        }

        @Override
        public void LoadImage(Bitmap bitmap) {
            // Notify the managers of the bitmap
            for(LoaderManager manager : mManagers) {
                manager.notifyRetrievedBitmap(bitmap);
            }
        }
    }

    // Simply notifies the manager(s) of the retrieved image
    private class NotifySpecialReceivedTask extends LoadImageTask {
        String mUrl;
        SpecializedManager mManager;

        // Special params to return back to the maanger
        int paramA;
        int paramB;

        // Default constructor
        NotifySpecialReceivedTask(String url, SpecializedManager manager, int paramA, int paramB) {
            this.mUrl = url;
            this.mManager = manager;
            this.paramA = paramA;
            this.paramB = paramB;
        }

        @Override
        public String GetUrl() {
            return mUrl;
        }

        @Override
        public void LoadImage(Bitmap bitmap) {
            // Notify the manager of the bitmap abd params at once
            mManager.notifyRetrievedBitmap(bitmap, paramA, paramB);
        }
    }

    class BitmapLoader implements Runnable {
        // The respective task to load
        LoadImageTask mTask;

        // Default constructor
        BitmapLoader(LoadImageTask task) {
            // Cache the task to reference later
            this.mTask = task;
        }

        @Override
        public void run() {
            Bitmap bitmap = null;

            // Get the BitMap from the url
            try {
                URL url = new URL(mTask.GetUrl());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Let the main thread finally load the image, as other threads can't modify
                // other threads' views
            mHandler.post(new BitmapDisplayer(bitmap, mTask));
        }
    }

    class BitmapDisplayer implements Runnable {
        // The respective task to load
        LoadImageTask mTask;

        // The Bitmap to finally load into the task
        Bitmap mBitmap;

        // Default constructor
        BitmapDisplayer(Bitmap bitmap, LoadImageTask task) {
            this.mBitmap = bitmap;
            this.mTask = task;
        }

        @Override
        public void run() {
            // Finally load the Bitmap as the task requires
            mTask.LoadImage(mBitmap);

            // If there are any managers, notify them as appropriate
            if(mManagers != null) {
                for(LoaderManager manager : mManagers) {
                    // Notify the manager
                    manager.notifyDataLoaded();
                }
            }
        }
    }
}
