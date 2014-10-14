package edu.umich.engin.cm.onecoolthing.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.ViewUtil.SimplePagerAdapter;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by jawad on 14/10/14.
 */
public class FragmentVerticalPager extends Fragment {
    VerticalViewPager mViewPager;

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
        // Create a custom pagerAdapter to handle the different "pages"/fragments
        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(getFragmentManager());
        pagerAdapter.setFragments(context);

        // Set the pageAdapter to the ViewPager
        mViewPager.setAdapter(pagerAdapter);
    }

}
