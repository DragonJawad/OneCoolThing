package edu.umich.engin.cm.onecoolthing.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.ViewUtil.SimplePagerAdapter;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by jawad on 14/10/14.
 */
public class FragmentVerticalPager extends Fragment implements ViewPager.OnPageChangeListener {
    VerticalViewPager mViewPager; // The viewPager shown via this fragment
    SimplePagerAdapter pagerAdapter; // The adapter that controls the pager
    VertPagerCommunicator communicator; // Allows feedback to the activity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_verticalpager, container, false);

        // Get the viewpager from the layout but init later
        mViewPager = (VerticalViewPager) view.findViewById(R.id.pager);

       return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize the viewpager with the activity's context
        initViewPager(getActivity());
    }

    private void initViewPager(Context context) {
        // Set up pagerAdapter to handle the different "pages"/fragments
        pagerAdapter = new SimplePagerAdapter(getFragmentManager());
        pagerAdapter.setFragments(context);

        // Set the pageAdapter to the ViewPager
        mViewPager.setAdapter(pagerAdapter);

        // Make this fragment listen to the adapter's changes
        mViewPager.setOnPageChangeListener(this);
    }

    // Sets the communicator so the fragment can notify the activity of changes in pager
    public void setCommunicator(VertPagerCommunicator comm) {
        this.communicator = comm;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        // If no communicator set, do nothing
        if(communicator == null) return;

        // When a new page is selected, take notice!
        int color = pagerAdapter.getFragColor(i);

        // Notify the activity of changes
        communicator.changeRightSlide(color);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    // TODO: Change this to work better with final design, including names and actual usage
    // Currently, simply tells the ActivityTestCenter to change the right slider's color
    //      to the color that the center fragment is using
    public interface VertPagerCommunicator{
      public void changeRightSlide(int color);
    };
}
