package edu.umich.engin.cm.onecoolthing.CoolFeeds;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.CoolThings.CoolThingData;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.CoolImageLoader;
import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.Util.BitmapReceiver;

/**
 * Created by jawad on 19/09/15.
 */
public class CoolThingFrag extends android.support.v4.app.Fragment implements BitmapReceiver {
    private static final String LOGTAG = "MD/CoolThingFrag";

    // View elements
    private ImageView mBackground;
    private TextView mTitleView;
    private TextView mCurrentPosView;
    private TextView mTotalCountView;
    // Spinner to display until cool thing loads
    private ImageView mSpinner;

    // Holds the data to display for this cool thing
    private String mTitleText;
    private String mImageURL;
    private int mCurrentPosition;
    private int mTotalCoolThings;

    // True if data has been assigned at least once
        // Used only in onCreateView to update the counter (so if data is set before views created)
    private boolean mHasDataBeenAssigned = false;

    CoolImageLoader mImageLoader = new CoolImageLoader();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cool_things, container, false);

        // Get the spinner and set it visible
        mSpinner = (ImageView) view.findViewById(R.id.loadingSpinner);
        mSpinner.setVisibility(View.VISIBLE);

        // Get the view elements
        mBackground = (ImageView) view.findViewById(R.id.background);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mCurrentPosView = (TextView) view.findViewById(R.id.currentPosition);
        mTotalCountView = (TextView) view.findViewById(R.id.totalCount);

        // If data has already been assigned, then simply set up the counter display now
        if(mHasDataBeenAssigned) {
            mCurrentPosView.setText("" + mCurrentPosition);
            mTotalCountView.setText("" + mTotalCoolThings);
        }

        return view;
    }

    public void LoadNewCoolThing(CoolThingData newData, int currentPosition, int totalCount) {
        // Simply store the basic data
        mTitleText = newData.getTitle();
        mImageURL = newData.getImageURL();
        mCurrentPosition = currentPosition;
        mTotalCoolThings = totalCount;

        // If the views have already been created, then display the counter values
        if(mCurrentPosView != null && mTotalCountView != null) {
            mCurrentPosView.setText("" + mCurrentPosition);
            mTotalCountView.setText("" + mTotalCoolThings);
        }

        // Remember that basic data has been assigned at least once
            // Used only in onCreateView, maybe can be more efficient?
        mHasDataBeenAssigned = true;

        // Finally try loading the image
        mImageLoader.GetImage(mImageURL, this);
    }

    private void ResetViews() {
        // Clear out the background image
        mBackground.setImageResource(android.R.color.transparent);

        // Clear out any and all text
        mTitleView.setText("");

        // Show the loading spinner once again
        mSpinner.setVisibility(View.VISIBLE);
    }

    private void DisplayAllData(Bitmap backgroundImage) {
        // Set the background image
        mBackground.setImageBitmap(backgroundImage);

        // Set the title text
        mTitleView.setText(mTitleText);

        // Finally, stop the spinner
        mSpinner.setVisibility(View.GONE);;
    }

    /**
     * Called via this fragment's CoolImageLoader once the image is successfully received
     * @param results - Background image for this Cool Thing
     */
    @Override
    public void GotImage(Bitmap results) {
        DisplayAllData(results);
    }

    /**
     * Called vai this fragment's CoolImageLoader if it fails to receive the image.
     * Currently doesn't do anything significant at all
     */
    @Override
    public void FailedImageRetrieval() {
        // For now, simply log the issue
        Log.d(LOGTAG, "Received event that failed to retrieve image. Not doing anything though~");
    }
}
