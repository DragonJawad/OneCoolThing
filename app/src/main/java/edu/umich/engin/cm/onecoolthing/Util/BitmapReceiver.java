package edu.umich.engin.cm.onecoolthing.Util;

import android.graphics.Bitmap;

/**
 * Created by jawad on 19/09/15.
 */
public interface BitmapReceiver {
    public void GotImage(Bitmap results);
    public void FailedImageRetrieval();
}
