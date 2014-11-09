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
    // Manager to be notified once data is loaded
    LoaderManager mManagers[];

    // Handler to display images in UI thread
    Handler mHandler = new Handler();

    ExecutorService mExecutorService;

    // If not -1, then determines what scale to use the images at
    int mGivenScale = -1;

    // Interface so that user can be notified once data has been finally applied
    public interface LoaderManager {
        // Determines whether or not to give the retrieved bitMap to the manager(s) as well
        boolean getBitmap = false;

        // Notifies the fragment's manager that the fragment is done being set
        public void notifyDataLoaded();

        // Determines whether or not to return the retrieved bitMap
        public boolean shouldReturnBitmap();

        // Give the retrieved bitMap to the manager(s)
        public void notifyRetrievedBitmap(Bitmap bitmap);
    };

    // Basic constructor
    ImageLoaderNoCache() {
        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        mExecutorService = Executors.newFixedThreadPool(5);
    }

    // Constructor that specifies manager(s) to notify
    ImageLoaderNoCache(LoaderManager mManagers[]) {
        // Call the simple constructor
        this();

        // Cache the mManagers to call on later
        this.mManagers = mManagers;
    }

    // Display an image from a url on a specified imageView
    void DisplayImage(String url, ImageView imageView) {
        // Put this image process on line
        queBitmap(url, imageView);
    }


    void queBitmap(String url, ImageView imageView) {
        // Create the necessary task
        LoadIntoImageViewTask imageViewTask = new LoadIntoImageViewTask(url, imageView);

        // Put the task into the que
        mExecutorService.submit( new BitmapLoader(imageViewTask) );
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
            this.mBitmap = mBitmap;
            this.mTask = task;
        }

        @Override
        public void run() {
            // Finally load the Bitmap as the task requires
            mTask.LoadImage(mBitmap);

            // If there are any managers, notify them as appropriate
            if(mManagers != null) {
                for(LoaderManager manager : mManagers) {
                    // Notify the manager, by default
                    manager.notifyDataLoaded();

                    // If the manager also wants the bitmap, give it to him/her
                    if( manager.shouldReturnBitmap() ) {
                        manager.notifyRetrievedBitmap(mBitmap);
                    }
                }
            }
        }
    }
}
