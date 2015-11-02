package edu.umich.engin.cm.onecoolthing.NetworkUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import edu.umich.engin.cm.onecoolthing.Util.BitmapReceiver;

/**
 * Created by jawad on 19/09/15.
 *
 * Further simplified ImageLoader for loading images exclusively for CoolThingFrags
 */
public class CoolImageLoader {
    private static String LOGTAG = "MD/CoolImageLoader";

    private GetBitmap mCurrentTask;

    public void GetImage(BitmapReceiver callback, String imageUrl) {
        // If a task already exists, then there's a fairly big issue somewhere!
        if(mCurrentTask != null) {
            throw new RuntimeException("Task already in progress! New url: " + imageUrl);
        }

        mCurrentTask = new GetBitmap(callback, imageUrl);
        mCurrentTask.execute();
    }

    /**
     * Cancels any running async task
     * @return True if there was an ongoing task that was cancelled
     */
    public boolean CancelTaskIfAny() {

        // If there's a current task, cancel it
        if(mCurrentTask != null) {
//            Log.d(LOGTAG, "Canceling task");

            // Get rid of the task's callback, as no need for it anymore
            mCurrentTask.callback = null;

            // Cancel the task itself
            mCurrentTask.cancel(true);

            // Then throw away the task
            mCurrentTask = null;

            return true;
        }
        else {
//            Log.d(LOGTAG, "No task found to cancel");
            return false;
        }
    }

    /**
     * Called whenever the async task is done (even if it is cancelled)
     */
    private void asyncDone() {
        // Simply make sure not to have a reference to the async anymore
        this.mCurrentTask = null;
    }

    private class GetBitmap extends AsyncTask<Void, Void, Void> {
        public BitmapReceiver callback;
        private String mImageUrl;
        private Bitmap mResults;

        public GetBitmap(BitmapReceiver callback, String imageUrl) {
            this.callback = callback;
            this.mImageUrl = imageUrl;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // If there is no URL - possibly due to improper JSON - just save some work and do nothing
            if(mImageUrl == "") {
                mResults = null;
                return null;
            }

            try {
                // Simply get the BitMap from the url
                URL url = new URL(mImageUrl);
//                mResults = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                Bitmap urlResults = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                int width = urlResults.getWidth();
                int height = urlResults.getHeight();
                float scale = 0.5f;
                // CREATE A MATRIX FOR THE MANIPULATION
                Matrix matrix = new Matrix();
                // RESIZE THE BIT MAP
                matrix.postScale(scale, scale);

                // "RECREATE" THE NEW BITMAP
                mResults = Bitmap.createBitmap(urlResults, 0, 0, width, height, matrix, false);
            } catch (Exception e) {
                Log.e(LOGTAG, "Error with getting image | Url: " + mImageUrl + " | Error: " + e.toString());

                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            // If the callback is still valid, let the callback know that the async failed
            if(callback != null) {
                callback.FailedImageRetrieval();
            }

            // Report that the async is done
            asyncDone();

            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            // If the callback is still valid, give the results to it
            callback.GotImage(mResults);

            // Report that the async is done
            asyncDone();

        }
    }
}
