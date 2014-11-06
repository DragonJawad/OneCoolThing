package edu.umich.engin.cm.onecoolthing.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.umich.engin.cm.onecoolthing.CoolThings.CoolThingsPagerAdapter;
import edu.umich.engin.cm.onecoolthing.R;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by jawad on 14/10/14.
 */
public class FragmentOneCoolFeed extends Fragment implements ViewPager.OnPageChangeListener {
    private final String TAG = "MD/OneCoolFeed"; // Tag for logging from this class

    VerticalViewPager mViewPager; // The viewPager shown via this fragment
    VertPagerCommunicator communicator; // Allows feedback to the activity

    // The adapter that controls the pager
    CoolThingsPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_verticalpager, container, false);

       // Get the viewpager from the layout but init later
       mViewPager = (VerticalViewPager) view.findViewById(R.id.pager);

       Log.d(TAG, "Running onCreate");

       return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "Running onStart");

        // Initialize the viewpager with the activity's context
        initCoolViewPager(getActivity());
    }

    public void initCoolViewPager(Context context) {
        Log.d(TAG, "Initializing CoolViewpager");

        // Set up the pagerAdapter to handle the different "pages"/fragments
        pagerAdapter = new CoolThingsPagerAdapter(getFragmentManager());
        pagerAdapter.initAdapter(context);

        // Set the pageAdapter to the ViewPager
        mViewPager.setAdapter(pagerAdapter);

        // Make this fragment listen to the adapter's changes
        mViewPager.setOnPageChangeListener(this);
    }

    private void printStats() {

    }

    // Sets the communicator so the fragment can notify the activity of changes in pager
    public void setCommunicator(VertPagerCommunicator comm) {
        this.communicator = comm;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    // When a new page is selected, notify the activity so it can change another view to show data
    @Override
    public void onPageSelected(int i) {
        // If no communicator set, do nothing
        if(communicator == null) return;

        // Get the data from the page to pass on
        String subTitle = pagerAdapter.getSubTitle(i);
        String body = pagerAdapter.getBodyText(i);

        // Send the information to the activity
        communicator.changeRightSlide(subTitle, body);

        /*
        // When a new page is selected, take notice!
        int color = pagerAdapterTest.getFragColor(i);

        // Notify the activity of changes
        communicator.changeRightSlide(color);
        */
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    // TODO: Change this to work better with final design, including names and actual usage
    // Currently, simply tells the ActivityTestCenter to change the right slider's color
    //      to the color that the center fragment is using
    public interface VertPagerCommunicator{
      public void changeRightSlide(int color);
      public void changeRightSlide(String subTitle, String body);
    };
}
