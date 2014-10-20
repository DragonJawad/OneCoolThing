package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

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
        listOfFragCoolThings = new ArrayList<FragmentCoolThing>(pageLimit);

        // Fill in the fragments' titles, for now
        for(int i = 0; i < pageLimit; ++i) {
            FragmentCoolThing thisFrag = listOfFragCoolThings.get(i);
            CoolThing thisThing = listOfCoolThings.get(i);
            thisFrag.setTitleText( thisThing.getTitle() );
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
