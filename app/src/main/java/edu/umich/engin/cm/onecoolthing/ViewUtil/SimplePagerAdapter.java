package edu.umich.engin.cm.onecoolthing.ViewUtil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v13.app.FragmentPagerAdapter;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentBase;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 14/10/14.
 *
 * A simple fill-in adapter for a view pager
 */
public class SimplePagerAdapter extends FragmentPagerAdapter {
    // Three simple fragments
    FragmentBase fragA;
    FragmentBase fragB;
    FragmentBase fragC;

    public SimplePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(Context c){

        // Set up the simple base fragments
        fragA = new FragmentBase();
        fragB = new FragmentBase();
        fragC = new FragmentBase();

        Resources res = c.getResources();

        fragA.changeText("This is Fragment A!");
        fragB.changeText("This is Fragment B!");
        fragC.changeText("This is Fragment C!");

        fragA.changeBG(res.getColor(R.color.dev_blue));
        fragB.changeBG(res.getColor(R.color.dev_green));
        fragC.changeBG(res.getColor(R.color.dev_orange));

    }

    @Override
    public Fragment getItem(int position) {
        // TODO: Make this more efficient, use a list or such, also comment more
        Fragment frag = null;
        if(position == 0){
            frag = fragA;
        }
        else if(position == 1){
            frag = fragB;
        }
        else if(position == 2){
            frag = fragC;
        }

        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
}