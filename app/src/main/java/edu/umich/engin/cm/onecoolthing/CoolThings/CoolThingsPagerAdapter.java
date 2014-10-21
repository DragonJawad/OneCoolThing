package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentCoolThing;

/**
 * Created by jawad on 20/10/14.
 *
 * Displays FragmentCoolThings in a ViewPager
 */
public class CoolThingsPagerAdapter extends FragmentPagerAdapter {
    // List of fragments to display
    ArrayList<FragmentCoolThing> listOfFragCoolThings;

    // Array of all coolThings
    ArrayList<CoolThing> listOfCoolThings;

    // Fragment/page limiter, for debugging purposes
    int pageLimit = 5;

    public CoolThingsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void initAdapter(ArrayList<CoolThing> coolThings) {
        // Save the list of coolThings for later use
        listOfCoolThings = coolThings;

        // Initialize the array of cool thing fragments
        listOfFragCoolThings = new ArrayList<FragmentCoolThing>();

        // Init fragments and with titles, for now
        for(int i = 0; i < pageLimit; ++i) {
            // Initialize fragment
            FragmentCoolThing thisFrag = new FragmentCoolThing();

            // Insert a title into the fragment
            CoolThing thisThing = listOfCoolThings.get(i);
            thisFrag.setTitleText( thisThing.getTitle() );

            // Add the fragment to the array
            listOfFragCoolThings.add(thisFrag);
        }
    }

    // TODO: Set up the background image lazily and efficiently
    @Override
    public Fragment getItem(int i) {
        return listOfFragCoolThings.get(i);
    }

    @Override
    public int getCount() {
        return pageLimit;
    }
}
