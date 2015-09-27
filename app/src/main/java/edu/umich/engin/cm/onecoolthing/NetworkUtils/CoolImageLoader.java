package edu.umich.engin.cm.onecoolthing.NetworkUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private BitmapReceiver mCallback;

    public void GetImage(String imageUrl, BitmapReceiver callback) {
        // If a task already exists, then there's a fairly big issue somewhere!
        if(mCurrentTask != null) {
            throw new RuntimeException("Task already in progress! New url: " + imageUrl);
        }

        mCallback = callback;
        mCurrentTask = new GetBitmap(imageUrl);
        mCurrentTask.execute();
    }

    public void CancelTaskIfAny() {

        // If there's a current task, cancel it
        if(mCurrentTask != null) {
//            Log.d(LOGTAG, "Canceling task");

            // Cancel the task itself
            mCurrentTask.cancel(true);

            // Then throw away the task
            mCurrentTask = null;
        }
        else {
//            Log.d(LOGTAG, "No task found to cancel");
        }
    }

    private void GotBitmapSuccessfully(Bitmap results) {
        // First off, throw away the already-used async task
        mCurrentTask = null;

        // Then give the results to the callback
        mCallback.GotImage(results);

        // Finally, throw away the callback as it's now (supposedly) useless
        mCallback = null;
    }

    private void FailedImageRetrieval() {
        // Simply notify the callback
        mCallback.FailedImageRetrieval();
    }

    private class GetBitmap extends AsyncTask<Void, Void, Void> {
        private String mImageUrl;
        private Bitmap mResults;

        public GetBitmap(String imageUrl) {
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
                mResults = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                Log.e(LOGTAG, "Error with getting image | Url: " + mImageUrl + " | Error: " + e.toString());

                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            // Let rest of the system know that something went wrong
            FailedImageRetrieval();

            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            GotBitmapSuccessfully(mResults);
        }
    }
}
