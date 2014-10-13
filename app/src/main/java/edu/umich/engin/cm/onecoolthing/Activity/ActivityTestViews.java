package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentBase;
import edu.umich.engin.cm.onecoolthing.R;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by jawad on 13/10/14.
 *
 * Used to test different views
 */
public class ActivityTestViews extends Activity {
//    VerticalViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_views);

        // Set up the view pager
        initViewPager();
    }

    private void initViewPager() {
        VerticalViewPager mViewPager = (VerticalViewPager) findViewById(R.id.pager);

        // Create a custom pagerAdapter to handle the different "pages"/fragments
        MyAdapter pagerAdapter = new MyAdapter(getFragmentManager());
        pagerAdapter.setFragments(this);

        // Set the pageAdapter to the ViewPager
        mViewPager.setAdapter(pagerAdapter);
    }

    class MyAdapter extends FragmentPagerAdapter {

        // Three simple fragments
        FragmentBase fragA;
        FragmentBase fragB;
        FragmentBase fragC;

        public MyAdapter(FragmentManager fm) {
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
}
