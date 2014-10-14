package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentBase;
import edu.umich.engin.cm.onecoolthing.Fragments.FragmentVerticalPager;
import edu.umich.engin.cm.onecoolthing.R;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by jawad on 13/10/14.
 *
 * Used to test different views
 */
public class ActivityTestViews extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_views);

        // Add in the viewpager fragment
        addViewPager();
    }

    private void addViewPager() {
        FragmentVerticalPager frag = new FragmentVerticalPager();
    //    FragmentBase frag = new FragmentBase();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.container, frag);
        fragmentTransaction.commit();
    }
}
