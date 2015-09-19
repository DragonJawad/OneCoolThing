package edu.umich.engin.cm.onecoolthing.CoolFeeds;

import android.graphics.Bitmap;
import android.util.Log;

import edu.umich.engin.cm.onecoolthing.Util.BitmapReceiver;

/**
 * Created by jawad on 19/09/15.
 */
public class CoolThingFrag extends android.support.v4.app.Fragment implements BitmapReceiver {
    private static final String LOGTAG = "MD/CoolThingFrag";

    @Override
    public void GotImage(Bitmap results) {

    }

    @Override
    public void FailedImageRetrieval() {
        // For now, simply log the issue
        Log.d(LOGTAG, "Received event that failed to retrieve image. Not doing anything though~");
    }
}
