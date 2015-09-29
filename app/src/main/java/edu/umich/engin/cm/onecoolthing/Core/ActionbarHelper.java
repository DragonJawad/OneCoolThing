package edu.umich.engin.cm.onecoolthing.Core;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 27/09/15.
 */
public class ActionbarHelper {
    private static final String LOGTAG = "MD/ActionbarHelper";

    // Reference to the Activity. TODO: Use an interface instead, simplify the communication
    ActivityMain mActivityRef;

    // Internal remembrance of what mode is currently active
    ActionBarType mCurrentMode;

    // Actionbar views
    View mViewCoolFeeds;
    View mViewActionBarTransparent;
    View mViewActionBarSolidBg;
    View mViewActionBarBackOnly;
    TextView mActionTransBgTitle;
    TextView mActionSolidBgTitle;

    // For remembering what's currently active in the CoolFeeds actionbar
    private boolean mIsOCFActive;
    // Remembers if the CoolFeeds ActionBar has been fully set up one time at least
    private boolean mFirstCoolFeedSetupDone = false;
    // The container of the MEM portion in the CoolFeeds actionbar (for moving and animating)
    View mCoolFeedsMEMContainer;
    // Locations to and from where the MEM part of the CoolFeed actionbar should animate around
    private int[] mClosedLoc = null;
    private int[] mOpenLoc = null;

    // Enums for ActionBar setting configurations
    public enum ActionBarType {
        ONECOOLFEED ,   // CoolFeeds actionbar with the OCT part active
        MEMCOOLFEEED,   // CoolFeeds actionbar with the Mich Eng Mag part active
        TRANSPARENT,    // ActionBar that has a transparent background
        SOLIDBG,        // ActionBar that has a solid white bg
        BACKONLY        // For ActionBar that only contains the back button [+solid white bg]
    }

    public ActionbarHelper(ActivityMain activity, LayoutInflater inflater, ViewGroup actionbarContainer) {
        // Cache the activity reference for later use, both for Context ref and for calling methods
        mActivityRef = activity;

        // Inflate the CoolFeeds ActionBar view
        mViewCoolFeeds = inflater.inflate(R.layout.actionbar_coolfeeds, null);

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
        actionbarContainer.addView(mViewCoolFeeds);
        actionbarContainer.addView(mViewActionBarTransparent);
        actionbarContainer.addView(mViewActionBarSolidBg);
        actionbarContainer.addView(mViewActionBarBackOnly);

        // Show the transparent ActionBar by default
        mCurrentMode = ActionBarType.BACKONLY; // Set so toggleActionBars() can reset it without issues
        toggleActionBars(ActionBarType.TRANSPARENT);

        // Initialize the CoolFeed actionbar as necessary
        initCoolFeed();
    }

    public void toggleActionBars(ActionBarType mode) {
        // If this is the same mode as before, nothing to do
        if(mCurrentMode == mode)
            return;

        // TODO: Overly hacky solution. Supposed to only use CoolFeeds actionbar. Properly do when time allows
        if(mode != ActionBarType.ONECOOLFEED && mode != ActionBarType.MEMCOOLFEEED) {
            return;
        }

        // Hide all ActionBars by default, first
        mViewCoolFeeds.setVisibility(View.INVISIBLE);
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
        else if(mode == ActionBarType.ONECOOLFEED) {
            mViewCoolFeeds.setVisibility(View.VISIBLE);

            switchCoolFeed(true, false);
        }
        else if(mode == ActionBarType.MEMCOOLFEEED) {
            mViewCoolFeeds.setVisibility(View.VISIBLE);

            switchCoolFeed(false, false);
        }

        // Now finally cache the new ActionBarType
        mCurrentMode = mode;
    }

    public void setSolidActionbarTitle(CharSequence newTitle) {
        mActionSolidBgTitle.setText(newTitle);
    }

    public void setTransActionbarTitle(CharSequence newTitle) {
        mActionTransBgTitle.setText(newTitle);
    }

    private void initCoolFeed() {
        // First off, make the main menu button act as it should be- the drawer opening button
        mViewCoolFeeds.findViewById(R.id.navButton_main)
                .setOnClickListener(mActivityRef);

        // Cache all the necessary parts of the CoolFeeds to setup the switching, sliding actions
        mCoolFeedsMEMContainer = mViewCoolFeeds.findViewById(R.id.containerMEM);
        ImageButton imageBtnOCF = (ImageButton) mViewCoolFeeds.findViewById(R.id.navButton_OCT);
        ImageButton imageBtnMEM = (ImageButton) mViewCoolFeeds.findViewById(R.id.navButton_MEM);

        // Set the image buttons to switch the current feed, as necessary
        imageBtnOCF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCoolFeed(true, true);
            }
        });
        imageBtnMEM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCoolFeed(false, true);
            }
        });

        // Remember that, by default, One Cool Feed is NOT active
        mIsOCFActive = false;

        // As the layout is usually not drawn at this point, wait until it is to move over the MEM portion
        ViewTreeObserver vto = mCoolFeedsMEMContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Try to switch to the OCF before the user notices anything, if necessary
                if(!mFirstCoolFeedSetupDone) {
                    mFirstCoolFeedSetupDone = true;
                    switchCoolFeed(true, true);
                }
            }
        });
    }

    /**
     * For the mViewCoolFeeds actionbar, switches the "active" part of the actionbar
     * @param useOCF True if One Cool Feed should be the active item, False for MEM
     * @param useAnimation True if the MEM part should slide in/out
     */
    private void switchCoolFeed(boolean useOCF, boolean useAnimation) {
        // First off, try to switch to the correct fragment, if necessary
        if(useOCF) {
            // Switch to the One Cool Thing/Feed fragment, if not already on it
            mActivityRef.changeFragIfNecessary(0);
        }
        else {
            // Switch to the Michigan Engineer Mag, if not already on it
            mActivityRef.changeFragIfNecessary(1);
        }

        // If the current active feed in the actionbar is the requested one, nothing more to do
        if(useOCF == mIsOCFActive)
            return;

        // If the locations haven't been cached yet, get them now
        if(mClosedLoc == null || mClosedLoc[0] == 0) {
            mClosedLoc = new int[2];
            mOpenLoc = new int[2];

            mCoolFeedsMEMContainer.getLocationOnScreen(mOpenLoc);
            mViewCoolFeeds.findViewById(R.id.viewTargetPos)
                    .getLocationOnScreen(mClosedLoc);

            Log.d(LOGTAG, "mOpenLoc: " + mOpenLoc[0] + " " + mOpenLoc[1]);
            Log.d(LOGTAG, "mClosedLoc: " + mClosedLoc[0] + " " + mClosedLoc[1]);

            // If the position is set to 0, then the layout hasn't been set yet- can't do anything
            if(mClosedLoc[0] == 0) {
                return;
            }
        }

        // Duration depends on actually "animating", in terms of what the user can see
        long animDuration = useAnimation ? 400 : 0;

        ObjectAnimator objectAnimator;
        if(useOCF) {
            // If so, then "closing" the MEM portion
            objectAnimator = ObjectAnimator.ofFloat(mCoolFeedsMEMContainer, "translationX", 0f, mClosedLoc[0] - mOpenLoc[0]);
        }
        else {
            // Otherwise, "opening" the MEM portio
            objectAnimator = ObjectAnimator.ofFloat(mCoolFeedsMEMContainer, "translationX", mClosedLoc[0] - mOpenLoc[0], 0f);
        }
        objectAnimator.setDuration(animDuration);
        objectAnimator.start();

        // Finally remember what mode the CoolFeed is now in
        mIsOCFActive = useOCF;
    }
}
