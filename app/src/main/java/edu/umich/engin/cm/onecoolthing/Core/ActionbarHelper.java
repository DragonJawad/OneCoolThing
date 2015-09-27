package edu.umich.engin.cm.onecoolthing.Core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 27/09/15.
 */
public class ActionbarHelper {
    // Reference to the Activity. TODO: Use an interface instead, simplify the communication
    ActivityMain mActivityRef;

    // Actionbar views
    View mViewActionBarTransparent;
    View mViewActionBarSolidBg;
    View mViewActionBarBackOnly;
    TextView mActionTransBgTitle;
    TextView mActionSolidBgTitle;

    // Enums for ActionBar setting configurations
    public enum ActionBarType {
        TRANSPARENT,// ActionBar that has a transparent background
        SOLIDBG,    // ActionBar that has a solid white bg
        BACKONLY    // For ActionBar that only contains the back button [+solid white bg]
    }

    public ActionbarHelper(ActivityMain activity, LayoutInflater inflater, ViewGroup actionbarContainer) {
        // Cache the activity reference for later use, both for Context ref and for calling methods
        mActivityRef = activity;

        // Inflate the simple ActionBar view
        mViewActionBarTransparent = inflater.inflate(R.layout.actionbar_withtransbg, null);

        // Set the imageButton from the simple view to toggle the slidingMenu
        mViewActionBarTransparent.findViewById(R.id.navButton)
                .setOnClickListener(mActivityRef);

        // Inflate the ActionBar with the title view and solid white background
        mViewActionBarSolidBg = inflater.inflate(R.layout.actionbar_withsolidbg, null);

        // Set the imageButton from the view with title to toggle the slidingMenu
        mViewActionBarSolidBg.findViewById(R.id.navButton)
                .setOnClickListener(mActivityRef);

        // Get the title textView from the transparent bg view so the title can be set later
        mActionTransBgTitle = (TextView) mViewActionBarTransparent.findViewById(R.id.textTitle);

        // Get the title textView from the solid bg view so the title can be set later
        mActionSolidBgTitle = (TextView) mViewActionBarSolidBg.findViewById(R.id.textTitle);

        // Inflate the ActionBar which only contains the back button
        mViewActionBarBackOnly = inflater.inflate(R.layout.actionbar_withbackbutton, null);

        // Make the back button on this ActionBar actually register as a back click
        mViewActionBarBackOnly.findViewById(R.id.back_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Simply act as if the actual back button was pressed
                    mActivityRef.onBackPressed();
                }
            });

        // Add the actionBars to the respective container
        actionbarContainer.addView(mViewActionBarTransparent);
        actionbarContainer.addView(mViewActionBarSolidBg);
        actionbarContainer.addView(mViewActionBarBackOnly);

        // Only show the transparent ActionBar, by default
        mViewActionBarSolidBg.setVisibility(View.INVISIBLE);
        mViewActionBarBackOnly.setVisibility(View.INVISIBLE);
    }

    public void toggleActionBars(ActionBarType mode) {
        // Hide all ActionBars by default, first
        mViewActionBarTransparent.setVisibility(View.INVISIBLE);
        mViewActionBarSolidBg.setVisibility(View.INVISIBLE);
        mViewActionBarBackOnly.setVisibility(View.INVISIBLE);

        // Show the correct actionBar
        if(mode == ActionBarType.TRANSPARENT)
            mViewActionBarTransparent.setVisibility(View.VISIBLE);
        else if(mode == ActionBarType.SOLIDBG)
            mViewActionBarSolidBg.setVisibility(View.VISIBLE);
        else if(mode == ActionBarType.BACKONLY)
            mViewActionBarBackOnly.setVisibility(View.VISIBLE);
    }

    public void setSolidActionbarTitle(CharSequence newTitle) {
        mActionSolidBgTitle.setText(newTitle);
    }

    public void setTransActionbarTitle(CharSequence newTitle) {
        mActionTransBgTitle.setText(newTitle);
    }
}
