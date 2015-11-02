package edu.umich.engin.cm.onecoolthing.CoolFeeds;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.CoolImageLoader;
import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.Util.BitmapReceiver;

/**
 * Created by jawad on 19/09/15.
 */
public class CoolThingFrag extends android.support.v4.app.Fragment implements BitmapReceiver {
    private static final String LOGTAG = "MD/CoolThingFrag";

    // View elements
    private ImageView mBackgroundView;
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
    private Bitmap mBackgroundImage;

    // True if data has been assigned at least once
    // Used only in onCreateView to update the counter (so if data is set before views created)
    private boolean mHasDataBeenAssigned = false;

    CoolImageLoader mImageLoader = new CoolImageLoader();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cool_things, container, false);

        // Get the spinner and set it visible
        mSpinner = (ImageView) view.findViewById(R.id.loadingSpinner);

        // Get the view elements
        mBackgroundView = (ImageView) view.findViewById(R.id.background);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mCurrentPosView = (TextView) view.findViewById(R.id.currentPosition);
        mTotalCountView = (TextView) view.findViewById(R.id.totalCount);

        // If data has already been assigned, then simply set up the counter display now
        if(mHasDataBeenAssigned) {
            mCurrentPosView.setText("" + mCurrentPosition);
            mTotalCountView.setText("" + mTotalCoolThings);
        }

        // If the image has already been loaded, then display it
        if(mBackgroundImage != null) {
            DisplayAllData(true);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ShowSpinner();
    }

    public void LoadNewCoolThing(CoolThingData newData, int currentPosition, int totalCount) {
        // Cancel any ongoing previous image loading tasks as appropriate
        if(mImageLoader.CancelTaskIfAny()) {
            Log.d(LOGTAG, "Pos: " + mCurrentPosition + " | NewPos: " + currentPosition +
                    " | Ongoing task was cancelled");
        }

        // Reset the views, if views have already been created
        if(mBackgroundView != null)
            ResetViews();

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
        mImageLoader.GetImage(this, mImageURL);
    }

    private void ResetViews() {
        // Show the loading spinner once again
        ShowSpinner();

        // If the views haven't been set up and cached yet, do nothing else
        if(mBackgroundView == null || mTitleView == null) {
            return;
        }

        // Clear out the background image
        mBackgroundView.setImageResource(android.R.color.transparent);
        mBackgroundImage = null;

        // Clear out any and all text
        mTitleView.setText("");
    }

    private void DisplayAllData(boolean displayImage) {
        // Stop the spinner
        HideSpinner();

        // If the necessary views haven't been set up - for some reason - then stop now
        if(mBackgroundView == null || mTitleView == null) {
            return;
        }

        if(displayImage) {
            // Set the background image only if the views have already been created
            mBackgroundView.setImageBitmap(mBackgroundImage);
        }

        // Set the title text
        mTitleView.setText(mTitleText);
    }

    private void ShowSpinner() {
        if(mSpinner == null) return;

        mSpinner.setVisibility(View.VISIBLE);
        // Set the spinner to infinitely spin
        if(getActivity() != null) {
            mSpinner.startAnimation(
                    AnimationUtils.loadAnimation(getActivity(), R.anim.loadinganim_spin)
            );
        }
    }
    private void HideSpinner() {
        if(mSpinner == null) return;

        mSpinner.clearAnimation();
        mSpinner.setVisibility(View.GONE);
    }

    /**
     * Called via this fragment's CoolImageLoader once the image is successfully received
     * @param results - Background image for this Cool Thing
     */
    @Override
    public void GotImage(Bitmap results) {
        mBackgroundImage = results;

        // Display all the data if the views have already been created
        if(mBackgroundView != null)
            DisplayAllData(true);
    }

    /**
     * Called vai this fragment's CoolImageLoader if it fails to receive the image.
     * Currently doesn't do anything significant at all
     */
    @Override
    public void FailedImageRetrieval() {
        // For now, simply log the issue
        Log.d(LOGTAG, "Pos: " + mCurrentPosition + " | Received event that failed to retrieve image");

        // Display all the information except the background image
        DisplayAllData(false);
    }
}