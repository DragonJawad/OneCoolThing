package edu.umich.engin.cm.onecoolthing.MichEngMag;

import android.graphics.Bitmap;

/**
 * Created by jawad on 05/12/14.
 *
 * Holds the necessary data for opening a detailed MEM item
 */
public class MEMDetailedData {
    Bitmap mTopImageBitmap;
    String mTitle;
    String mCategory;
    String mAuthor;
    String mWebHTMLData;

    // Holds the data for which position this data is for
    int adapterRowPosition;
    int adapterSpot; // 0 - big item, 1 - split left, 2 - split right

    public Bitmap getmTopImageBitmap() {
        return mTopImageBitmap;
    }

    public void setmTopImageBitmap(Bitmap mTopImageBitmap) {
        this.mTopImageBitmap = mTopImageBitmap;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmWebHTMLData() {
        return mWebHTMLData;
    }

    public void setmWebHTMLData(String mWebHTMLData) {
        this.mWebHTMLData = mWebHTMLData;
    }

    public int getAdapterRowPosition() {
        return adapterRowPosition;
    }

    public void setAdapterRowPosition(int adapterRowPosition) {
        this.adapterRowPosition = adapterRowPosition;
    }

    public int getAdapterSpot() {
        return adapterSpot;
    }

    public void setAdapterSpot(int adapterSpot) {
        this.adapterSpot = adapterSpot;
    }
}
