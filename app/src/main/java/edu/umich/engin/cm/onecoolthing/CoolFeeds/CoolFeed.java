package edu.umich.engin.cm.onecoolthing.CoolFeeds;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.Core.AnalyticsHelper;
import edu.umich.engin.cm.onecoolthing.Util.VertPagerCommunicator;
import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.Util.ShakeListener;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by jawad on 19/09/15.
 */
public abstract class CoolFeed extends Fragment implements ViewPager.OnPageChangeListener,
        ShakeListener, ParseCoolThings.CoolJSONUser {
    private final String LOGTAG = "MD/OneCoolFeed";

    // The viewPager shown via this fragment
    VerticalViewPager mViewPager;
    VertPagerCommunicator mCommunicator; // Allows feedback to the activity

    // The adapter that controls the pager
    CoolThingsPagerAdapter mPagerAdapter;

    // Caches the current page selected
    int mCurrentPageSelected = 0;

    // States whether the Activity has been created yet or not
    boolean mActivityYetCreated = false;

    public abstract String getBaseUrl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coolfeed, container, false);

        // Get the viewpager from the layout but init later
        mViewPager = (VerticalViewPager) view.findViewById(R.id.pager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivityYetCreated = true;

        // Send data that the OneCoolFeed page has now been opened
        ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.MAINVIEW);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get all the cool things, if necessary
        if(mPagerAdapter == null)
            getAllCoolThings();
    }

    @Override
    public void onResume() {
        super.onResume();

        // If the Activity has been linked, then send data that the OneCoolFeed center view has been returned to
        if(mActivityYetCreated)
            ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.MAINVIEW);
    }

    private void getAllCoolThings() {
        ParseCoolThings parser = new ParseCoolThings();
        parser.getCoolThings(getActivity(), this, getBaseUrl());
    }

    private void initCoolViewPager(Context context, ArrayList<CoolThingData> allCoolThings) {
        // ONLY set the mPagerAdapter if necessary
        // ie, when restarting activity/frag, mPagerAdapter already exists
        if(mPagerAdapter == null) {
            // Set up the mPagerAdapter to handle the different "pages"/fragments
            mPagerAdapter = new CoolThingsPagerAdapter(getChildFragmentManager());
            mPagerAdapter.LoadCoolThings(allCoolThings);

            // Set the pageAdapter to the ViewPager
            mViewPager.setAdapter(mPagerAdapter);

            // Make this fragment listen to the adapter's changes
            mViewPager.setOnPageChangeListener(this);

            // Set the pager to retain pretty much no frags itself in memory
            mViewPager.setOffscreenPageLimit(0);

            // Now the right sliding menu can be set up finally for the first time
            notifyCommunicator(0);
        }
    }

    // Passes the appropriate data for the mCommunicator at the position
    private void notifyCommunicator(int position) {
        // Double check that the mCommunicator has been set
        if(mCommunicator == null) {
            Log.e(LOGTAG, "Communicator was null!");
            return;
        }

        // Get the data from the page to pass on
        String title = mPagerAdapter.getTitle(mCurrentPageSelected);
        String subTitle = mPagerAdapter.getSubTitle(position);
        String body = mPagerAdapter.getBodyText(position);
        String paletteColor = mPagerAdapter.getPaletteColor(position);
        String fullItemURL = mPagerAdapter.getFullItemURL(position);

        // Send the information to the activity
        mCommunicator.changeRightSlide(title, subTitle, body, paletteColor, fullItemURL);
    }

    // Sets the mCommunicator so the fragment can notify the activity of changes in pager
    public void setCommunicator(VertPagerCommunicator comm) {
        this.mCommunicator = comm;
    }

    // Gets the url to share to everybody
    public String getShareUrl() {
        // Simply get and return the current url from the mPagerAdapter
        return mPagerAdapter.getFullItemURL(mCurrentPageSelected);
    }

    // Gets the text for tweeting
    public String getTweetText() {
        return mPagerAdapter.getTweetText(mCurrentPageSelected);
    }

    // Get the subject line - ie short title - for general sharing
    public String getSubjectForSharing() {
        return mPagerAdapter.getSubTitle(mCurrentPageSelected);
    }

    @Override
    public void gotCoolThings(ArrayList<CoolThingData> allCoolThings) {
        initCoolViewPager(getActivity(), allCoolThings);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    // When a new page is selected, notify the activity so it can change another view to show data
    @Override
    public void onPageSelected(int i) {
        // If no mCommunicator set, do nothing
        if(mCommunicator == null) return;

        // Let the function notify the mCommunicator of the data at position i
        notifyCommunicator(i);

        // Cache the current position
        mCurrentPageSelected = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onShake() {
        Log.d(LOGTAG, "onShake() - Not implemented yet!!!");
        Toast.makeText(getActivity(), "Got a shake in the OneCoolFeedFrag!", Toast.LENGTH_SHORT)
                .show();
    }
}
