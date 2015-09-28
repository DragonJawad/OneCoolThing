package edu.umich.engin.cm.onecoolthing.Core;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.CoolFeeds.MichiganCoolFeed;
import edu.umich.engin.cm.onecoolthing.CoolFeeds.OneCoolFeed;
import edu.umich.engin.cm.onecoolthing.DecoderV5.DecoderIntroFrag;
import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.StandaloneFragments.AboutFragment;
import edu.umich.engin.cm.onecoolthing.StandaloneFragments.SendCoolFragment;
import edu.umich.engin.cm.onecoolthing.StandaloneFragments.SettingsFragment;
import edu.umich.engin.cm.onecoolthing.StandaloneFragments.WebFeedFragment;
import edu.umich.engin.cm.onecoolthing.Util.BackStackSettings;
import edu.umich.engin.cm.onecoolthing.Util.Constants;
import edu.umich.engin.cm.onecoolthing.Util.IntentStarter;
import edu.umich.engin.cm.onecoolthing.Util.ObservableScrollView;
import edu.umich.engin.cm.onecoolthing.Util.ScrollViewListener;
import edu.umich.engin.cm.onecoolthing.Util.ShakeListener;
import edu.umich.engin.cm.onecoolthing.Util.VertPagerCommunicator;

/**
 * Created by jawad on 12/10/14.
 */
public class ActivityMain extends FragmentActivity implements VertPagerCommunicator,
    View.OnClickListener, SettingsFragment.TutorialEnforcer, ShakeDetector.Listener {
    // Log tag for this class
    private final String TAG = "MD/ActivityTestCenter";

    // Enums for Activity setting configurations
    public enum SettingsType {
        ONECOOLFEED, // Settings for the One Cool Feed
        MICHENGMAG,  // Settings for the MichEngMag feed
        WEBVIEW,     // Settings for the WebView fragments, like the Tumblr feeds
        ABOUT,       // Settings for the About page
        DECODER      // Settings for the Decoder intro fragment
    }

    // The view that holds all the fragments
    FrameLayout mFragContainer;

    // Tags for all fragments
    private String[] mFragTags;
    // URLs for all feeds- same indexes as mCurrentFragmentIndex and null == no webview
    private String[] mFragUrls;

    // Sliding Menu test objects
    private SlidingMenu mSlidingMenuLeft;
    private SlidingMenu mSlidingMenuRight;

    // ListView of the left slidingMenu
    ListView mListNav;
    NavAdapter mNavAdapter;

    // Determines whether or not the right sliding menu is enabled
    private boolean mRightMenuEnabled = false;

    // The right sliding menu's repeatedly accessed views
    LinearLayout mRightMenuLinearLayout;
    TextView mRightMenuTitleTextView;
    TextView mRightMenuBodyTextView;
    TextView mRightMenuTapMoreTextView;
    ObservableScrollView mRightMenuScrollView;
    ImageView mRightMenuScrollArrow;

    // Views for the share icons within the right sliding menu
    ImageView mShareMainImage;
    boolean isShareIconShowing = false; // States if the share icons are showing are not

    // Object that encapsulates all the necessary actionbar work
    ActionbarHelper mActionbarHelper;

    // Keeps track if seen the tutorial or not already
    boolean seenTutorialAlready = false; // False if not seen tutorial yet

    // Caches the currently seen title of the One Cool Feed
    String mCurrentCoolThingTitle = null;

    /* Current center fragment index, for reference
     * Below is the master list - If the nav is changed, change the below and any
     *      code that uses the below system, ie
     *          strings.xml -> nav_items array
     *          this -> changeFrag()
     * TODO/NOTE: In hindsight, this was stupid. Need to redo this as enums
     * 0 - One Cool Feed [main One Cool Thing feed]
     * 1 - Michigan Engineer Mag
     * 2 - LabLog
     * 3 - Visual Adventures
     * 4 - I <3 A2
     * 5 - Some Cool Apps
     * 6 - MichEpedia
     * 7 - Decoder
     * 8 - About
     * 9 - Send Us Cool Things
     * 10 - Settings
     */
    int mCurrentFragmentIndex = -1;

    // List of backstack settings to restore
    ArrayList<BackStackSettings> mBackStackList;

    // Time since last time user shook the phone
    private long mLastShakeTime;
    // Timeout until subsequent shakes will be registered
    private static final long SHAKE_TIMOEUT = 2000; // 2 seconds, in milliseconds
    // Listener to be called when a shake event occurs
    private ShakeListener mShakeListener; // Only changed in changeFrag

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the actionBar in its evil entirety
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.hide();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }

        View homeIcon = findViewById(android.R.id.home);
        // Hides the View (and so the icon)
        if (homeIcon != null)
            ((View) homeIcon.getParent()).setVisibility(View.GONE);

        overridePendingTransition(0, 0);

        setContentView(R.layout.activity_main);

        // Find and cache the fragContainer
        mFragContainer = (FrameLayout) findViewById(R.id.fragContainer);

        // Initialize the custom actionBar views and set the first actionBar up
        initCustomActionBar();

        // Initialize the sliding menus
        initBothSlidingMenus();

        // Get all the fragments' tags - or names - from the nav list in resources
        mFragTags = getResources().getStringArray(R.array.nav_items);
        // Get all fragments' urls, if they exist
        mFragUrls = getResources().getStringArray(R.array.nav_items_urls);

        // Initialize the backStack listening system
        initBackStackSystem();

        // Register the shake system
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        // Safety check- there shouldn't have been a last shake time registered yet
        mLastShakeTime = 0;

        // If the bundle is null, then using the One Cool Feed by default
        if(savedInstanceState == null) {
            changeFrag(0, false);
        }
        else {
            // Otherwise, get the "current" page index to use
            int newFragIndex = savedInstanceState.getInt(Constants.KEY_STATE_CURINDEX, 0);

            changeFrag(newFragIndex, false);
        }
    }

    // Save any data before the Activity is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current fragment's index, to pinpoint which page the Activity should restart into
        outState.putInt(Constants.KEY_STATE_CURINDEX, mCurrentFragmentIndex);
    }

    private void initCustomActionBar() {
        // Initialize the ActionbarHelper, which will encapsulate all the necessary work
        mActionbarHelper = new ActionbarHelper(
                this,
                getLayoutInflater(),
                (RelativeLayout) findViewById(R.id.actionbar_container)
        );
    }

    private void initBackStackSystem() {
        // Initialize the list of backStack settings tracking
        mBackStackList = new ArrayList<BackStackSettings>();
    }

    //region -->Tutorial Code<--
    private void showTutorialIfNecessary() {
        // Get from sharedPreferences whether or not the tutorial has been seen yet or not
        seenTutorialAlready = getSeenTutorial();

        // If haven't seen the tutorial yet, display it
        if(!seenTutorialAlready) displayTutorial();
    }

    private void displayTutorial() {
        // Inflate the view for the tutorial
        View tutorialView = getLayoutInflater().inflate(R.layout.overlay_tutorial, null);

        // Create the dialog and set the inflated view as its contentView
        final Dialog tutorialDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        tutorialDialog.setContentView(tutorialView);

        // Add an onClickListener to the specified region to close the dialog
        tutorialDialog.findViewById(R.id.tap_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save that the tutorial has been seen
                setSeenTutorial(true);

                // Close the tutorial
                tutorialDialog.dismiss();
            }
        });

        // Now show the tutorial finally
        tutorialDialog.show();
    }

    // Sets sharedPreferences boolean of whether or not tutorial has been seen yet
    public void setSeenTutorial(boolean isTutorialSeen) {
        // Get the shared preferences' editor
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set the saved bool to whatever isTutorialSeen is
        editor.putBoolean(Constants.KEY_SEENTUTORIAL, isTutorialSeen);

        // Commit the changes
        editor.commit();
    }

    public boolean getSeenTutorial() {
        // Get and return the value from the preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(Constants.KEY_SEENTUTORIAL, false);
    }


    @Override
    public void showTutorialAgain() {
        // First, set that the tutorial has not to be seen, ie needs to be checked as seen again
        setSeenTutorial(false);

        // Then, make sure the One Cool Feed frag is in place
        changeFrag(0);

        // Finally, actually show the tutorial
        displayTutorial();
    }
    //endregion

    // Set up the right and left sliding menus
    private void initBothSlidingMenus() {
        // Get the LayoutInflater to inflate the views for the left sliding menu
        LayoutInflater inflater = getLayoutInflater();

        // Initialize the left sliding menu
        mSlidingMenuLeft = new SlidingMenu(this);
        mSlidingMenuLeft.setMode(SlidingMenu.LEFT); // Define the orientation to the left

        // Inflate a view for the left sliding menu
        View viewLeftMenu = inflater.inflate(R.layout.slidingmenu_left, null);

        // Set up the listView within the left sliding menu
        mListNav = (ListView)viewLeftMenu.findViewById(R.id.list);
        mNavAdapter = new NavAdapter(this);
        mListNav.setAdapter(mNavAdapter);
        // Set a click listener for the listView
        mListNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 // Change out the current fragment displayed in the center
                changeFrag(position);

                // Toggle/close the left sliding menu
                toggleLeftSlidingMenu();
            }
        });

        mSlidingMenuLeft.setMenu(viewLeftMenu); // Assign the layout/content

        // Initialize the right sliding menu
        mSlidingMenuRight = new SlidingMenu(this);
        mSlidingMenuRight.setMode(SlidingMenu.RIGHT); // Define the orientation to the right

        // Initialize and set the right sliding menu's content
        View rightMenuView = inflater.inflate(R.layout.slidingmenu_right,null);
        mSlidingMenuRight.setMenu(rightMenuView);

        // Get the repeatedly accessed right sliding menu's views now
        mRightMenuLinearLayout = (LinearLayout) rightMenuView.findViewById(R.id.container_right_sliding);
        mRightMenuTitleTextView = (TextView) rightMenuView.findViewById(R.id.subTitle);
        mRightMenuBodyTextView = (TextView) rightMenuView.findViewById(R.id.bodyText);
        mRightMenuTapMoreTextView = (TextView) rightMenuView.findViewById(R.id.tapForMoreText);
        mRightMenuScrollView = (ObservableScrollView) rightMenuView.findViewById(R.id.scroll_description);
        mRightMenuScrollArrow = (ImageView) rightMenuView.findViewById(R.id.scroll_arrow);

        // Get the share icons and the container for the specific share icons
        mShareMainImage = (ImageView) rightMenuView.findViewById(R.id.share_text_icon);
        final ImageView shareFacebook = (ImageView) rightMenuView.findViewById(R.id.share_icon_facebook);
        final ImageView shareTwitter = (ImageView) rightMenuView.findViewById(R.id.share_icon_twitter);
        final ImageView shareGeneral = (ImageView) rightMenuView.findViewById(R.id.share_icon_email);
        final View shareSpecificContainer = (View) rightMenuView.findViewById(R.id.container_small_share_icons);

        // Create the reusable animations for the share icons
        final Animation shareSlideIn = AnimationUtils.loadAnimation(this, R.anim.shareicons_container_slidein);
        final Animation shareSlideOut = AnimationUtils.loadAnimation(this, R.anim.shareicons_container_slideout);

        // Initially animate the share icons out of view
        shareSpecificContainer.startAnimation(shareSlideOut);
        isShareIconShowing = false; // Make sure the system is stating that the icons are not showing

        // Set a click listener on the "Share This Cool Thing" button/icon to show the share buttons
        mShareMainImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;

                        // Overlay is black with transparency of 0x66 (40%)
                        view.getDrawable().setColorFilter(0x66000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();

                        // Either animate the share icons in or out
                        if(isShareIconShowing) {
                            // If the icons are showing, slide em out of here
                            shareSpecificContainer.startAnimation(shareSlideOut);

                            // State that the share icons are now not showing anymore
                            isShareIconShowing = false;
                        }
                        else {
                            // Otherwise, the icons are not showing so show em now
                            shareSpecificContainer.startAnimation(shareSlideIn);

                            // State that the share icons are now not showing anymore
                            isShareIconShowing = true;
                        }


                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;

                        // Clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();

                        break;
                    }
                }

                return true;
            }
        });

        // Set a single listener for the specific share icons
        View.OnTouchListener shareSpecificTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;

                        // Overlay is black with transparency of 0x66 (40%)
                        view.getDrawable().setColorFilter(0x66000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();

                        OneCoolFeed oneCoolFeedFrag = (OneCoolFeed) getSupportFragmentManager().findFragmentByTag(mFragTags[0]);

                        // React depending on which button was tapped
                        if(v == shareFacebook) {
                            // Unfortunately, can only share the url to FB, Get that data first from the feed
                            String shareUrl = oneCoolFeedFrag.getShareUrl();

                            // Then, use the Util to share it to Facebook
                            IntentStarter.shareToFacebook(ActivityMain.this, shareUrl);
                        }
                        else if(v == shareTwitter) {
                            // Get the text to share to Twitter
                            String tweetText = oneCoolFeedFrag.getTweetText();
                            String shareUrl = oneCoolFeedFrag.getShareUrl();

                            // Then, use the Util to share to Twitter
                            IntentStarter.shareToTwitter(ActivityMain.this, tweetText, shareUrl);
                        }
                        else if(v == shareGeneral) {
                            // Get the text for sharing
                            String shareSubject = oneCoolFeedFrag.getSubjectForSharing();
                            String shareUrl = oneCoolFeedFrag.getShareUrl();

                            // Then, use the Util to share generally
                            IntentStarter.shareToGeneral(ActivityMain.this, shareSubject, shareUrl);
                        }
                        else {
                            Log.e(TAG, "From share touch listener: Couldn't find matching view!");
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;

                        // Clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();

                        break;
                    }
                }

                return true;
            }
        };
        shareFacebook.setOnTouchListener(shareSpecificTouchListener);
        shareTwitter.setOnTouchListener(shareSpecificTouchListener);
        shareGeneral.setOnTouchListener(shareSpecificTouchListener);

        // Set a click listener on the textView to hide the flashing arrow animation
        mRightMenuBodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the flashing scroll arrow
                mRightMenuScrollArrow.setVisibility(View.INVISIBLE);
            }
        });

        // Take notice when a scrollView scroll event occurs, and if it occurs, hide the indicator
        mRightMenuScrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged() {
                // Hide the flashing scroll arrow
                mRightMenuScrollArrow.setVisibility(View.INVISIBLE);
            }
        });

        // Initially hide the TapMore textView
        mRightMenuTapMoreTextView.setVisibility(View.GONE);

        // Set the flashing arrow animation/indicator to be hidden by default
        mRightMenuScrollArrow.setVisibility(View.INVISIBLE);

        // Set listeners for the left and right sliding menus [so both aren't open at once]
        mSlidingMenuLeft.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                // Disable the right sliding menu period, as both shouldn't be open at once
                mSlidingMenuRight.setSlidingEnabled(false);
            }
        });

        mSlidingMenuLeft.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                // Enable the right sliding menu, if it's enabled
                if(mRightMenuEnabled) {
                    mSlidingMenuRight.setSlidingEnabled(true);
                }
            }
        });

        mSlidingMenuRight.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                // Disable the left sliding menu
                mSlidingMenuLeft.setSlidingEnabled(false);
            }
        });

        // TODO: Not sure, onOpen doesn't activate as expected. Consider changing all to onOpened/Closed
        mSlidingMenuRight.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                // If possible, send data of the current Cool Thing being viewed
                if(mCurrentCoolThingTitle != null) {
                    // Send some tracker data
                    ((AnalyticsHelper) getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.DETAILVIEW, mCurrentCoolThingTitle);
                }
            }
        });

        mSlidingMenuRight.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                // Enable the left sliding menu
                mSlidingMenuLeft.setSlidingEnabled(true);
            }
        });

        // Set up the rest of the shared sliding menu properties
        setUpSlidingMenu(mSlidingMenuLeft);
        setUpSlidingMenu(mSlidingMenuRight);
    }

    /** Sets up a slidingMenu according to pre-defined specifics
     * @param slidingMenu Must be an initialized SlidingMenu object with
     *                      orientation, shadow drawable, and menu already defined
     */
    private void setUpSlidingMenu(SlidingMenu slidingMenu) {
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width_main);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
    }

    public void changeFragIfNecessary(int index) {
        if(index == mCurrentFragmentIndex)
            return;
        else
            changeFrag(index, true);
    }

    /**
     * Changes out the center fragment as necessary and save the previous settings as well
     * @param index - Index of fragment page to change to
     */
    private void changeFrag(int index) {
        changeFrag(index, true);
    }

    /**
     * Changes out the center fragment and may or may not save the previous settings
     * @param index - Index of fragment page to change to
     * @param savePreviousSettings - If true, will save the previous settings to restore to later
     */
    private void changeFrag(int index, boolean savePreviousSettings) {
        // First check if user clicked on the current fragment again
        if (index == mCurrentFragmentIndex) return;

        // If there was a shake listener before, it's now obselete as the current frag is changing
        if(mShakeListener != null) mShakeListener = null;

        // Begin the fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create the BackStack instance that will keep track of the changes
        BackStackSettings backStackSettings = new BackStackSettings();
        backStackSettings.setPreviousFragPosition(mCurrentFragmentIndex);

        // First, clear out the main container, if necessary
        if(mCurrentFragmentIndex < 0) {
            // In case just restarted the Activity and just changing the frag, clean up everything
            mFragContainer.removeAllViews();
        }
        else {
            // Save the previous settings based off of the index
            if(mCurrentFragmentIndex == 0)
                backStackSettings.setPreviousSettings(SettingsType.ONECOOLFEED);
            else if(mCurrentFragmentIndex == 1) {
                backStackSettings.setPreviousSettings(SettingsType.MICHENGMAG);
            }
            else if(mCurrentFragmentIndex == 7)
                backStackSettings.setPreviousSettings(SettingsType.DECODER);
            else if(mCurrentFragmentIndex == 8)
                backStackSettings.setPreviousSettings(SettingsType.ABOUT);
            else
                backStackSettings.setPreviousSettings(SettingsType.WEBVIEW);
        }

        // Clean up any other necessary variables
        mCurrentCoolThingTitle = null;

        // Then add in the chosen fragment and set the appropriate settings
        if (index == 0) {
            // Apply the settings for the OneCoolFeed
            changeSettingsMode(SettingsType.ONECOOLFEED);

            // Actually initialize the fragment
            OneCoolFeed frag = new OneCoolFeed();

            // Let the frag communicate with this activity
            frag.setCommunicator(this);

            // Add in the fragment to the place specified in the layout file
            fragmentTransaction.replace(R.id.fragContainer, frag, mFragTags[0]);

            // Set the index of the mCurrentFragmentIndex to 0, to show that the OneCoolFeed was added
            mCurrentFragmentIndex = 0;

            // Register this fragment to handle shake events
            mShakeListener = frag;

            // Show the tutorial if necessary
            showTutorialIfNecessary();
        }
        else if(index == 1) {
            // Apply the settings for the Mich Eng Feed
            changeSettingsMode(SettingsType.MICHENGMAG);

            // Actually initialize the fragment
            MichiganCoolFeed frag = new MichiganCoolFeed();

            // Let the frag communicate with this activity
            frag.setCommunicator(this);

            // Add in the fragment to the place specified in the layout file
            fragmentTransaction.replace(R.id.fragContainer, frag, mFragTags[0]);

            // Set the index of the mCurrentFragmentIndex to 0, to show that the OneCoolFeed was added
            mCurrentFragmentIndex = 0;

            // Register this fragment to handle shake events
            mShakeListener = frag;

            // Show the tutorial if necessary
            showTutorialIfNecessary();
        }
        // Otherwise, if this index has an URL, open up a feed
        else if(!mFragUrls[index].equals("")) {
            // Get the url and title separately, for ease of typing/reading
            String this_url = mFragUrls[index];
            String this_title = mFragTags[index];

            // Set the actionBar's title text (on the one that will be used)
            mActionbarHelper.setSolidActionbarTitle(this_title);

            // Set settings for this web view
            changeSettingsMode(SettingsType.WEBVIEW);

            // Create a new TumblrFeed fragment, with its title and url
            WebFeedFragment frag = WebFeedFragment.newInstance(this_url, this_title);

            // Add the webview to the center view
            fragmentTransaction.replace(R.id.fragContainer, frag, mFragTags[index]);
        }
        else if(index == 7) {
            // Get the title/tag separately, for ease of typing/reading
            String this_title = mFragTags[index];
            // Put the title on the actionBar that will be used
            mActionbarHelper.setSolidActionbarTitle(this_title);

            // Set settings for this view- same as a Webview
            changeSettingsMode(SettingsType.DECODER);

            // Create the DecoderIntro frag to use
            DecoderIntroFrag frag = new DecoderIntroFrag();

            // Add the frag to the center view
            fragmentTransaction.replace(R.id.fragContainer, frag, this_title);
        }
        // If so, then add in the About fragment
        else if(index == 8) {
            // Get the title/tag separately, for ease of typing/reading
            String this_title = mFragTags[index];
            // Put the title on the actionBar that will be used
            mActionbarHelper.setSolidActionbarTitle(this_title);

            // Set settings for this view
            changeSettingsMode(SettingsType.ABOUT);

            // Create a new AboutFragment to use
            AboutFragment frag = new AboutFragment();

            // Add the frag to the center view
            fragmentTransaction.replace(R.id.fragContainer, frag, this_title);
        }
        // If so, then add in the Send Us Cool Things fragment
        else if(index == 9) {
          // Get the title/tag separately, for ease of typing/reading
          String this_title = mFragTags[index];
          // Put the title on the actionBar that will be used
          mActionbarHelper.setTransActionbarTitle(this_title);

          // Set settings for this view- same as About page
          changeSettingsMode(SettingsType.ABOUT);

          // Create a new SendCool fragment to use
          SendCoolFragment frag = new SendCoolFragment();

          // Add the frag to the center view
          fragmentTransaction.replace(R.id.fragContainer, frag, this_title);
        }
        // If so, then add in the Settings fragment
        else if(index == 10) {
            // Get the title/tag separately, for ease of typing/reading
            String this_title = mFragTags[index];
            // Put the title on the actionBar that will be used
            mActionbarHelper.setTransActionbarTitle(this_title);

            // Set settings for this view
            changeSettingsMode(SettingsType.ABOUT);

            // Create a new Settings to use
            SettingsFragment frag = new SettingsFragment();
            // Make sure to set the TutorialEnforcer if user decides to see tutorial again
            frag.setTutorialEnforcer(this);

            // Add the frag to the center view
            fragmentTransaction.replace(R.id.fragContainer, frag, this_title);
        }

        fragmentTransaction.commit();

        // Add the previous changes to the backStack settings tracker list IF
            // previous settings were requested to be saved AND previous index was in bounds
        if(savePreviousSettings && backStackSettings.getPreviousFragPosition() > -1)
            mBackStackList.add(backStackSettings);

        // Change the indicator on the menu
        mNavAdapter.setVisibleIndicator(index);

        // Finally, change the index of the currently used fragment
        mCurrentFragmentIndex = index;
    }

    @Override
    public void onBackPressed() {
        // If either sliding menu is showing, close 'em
        if(mSlidingMenuLeft.isMenuShowing()) toggleLeftSlidingMenu();
        else if(mSlidingMenuRight.isMenuShowing()) mSlidingMenuRight.toggle();

        // If there are no tracked/wanted backstack changes, then simply let Android handle the press
        else if(mBackStackList.size() == 0) {
            super.onBackPressed();
        }

        // Otherwise, handle the back button as user expects and switch back to the previous fragment
        else {
            // Pop off a BackStackSettings item from the list
            BackStackSettings backStackSettings = mBackStackList.get(mBackStackList.size()-1 );
            mBackStackList.remove(mBackStackList.size()-1);

            // Restore the settings
                // TODO: Either remove saving the backstack settings type or use it somehow
            changeFrag(backStackSettings.getPreviousFragPosition(), false);
        }
    }

    // Change the right slide to match the current CoolThing
    public void changeRightSlide(String title, String subTitle, String body, String paletteColor, final String fullItemURL) {
        // Get and cache the current Cool Thing's title
        mCurrentCoolThingTitle = title;

        // Change the body and subtitle texts
        mRightMenuTitleTextView.setText(subTitle);
        mRightMenuBodyTextView.setText(body);

        // Set the right slider's background and UI text color based on the pre-determined colors
        Resources res = getResources();
        int textColor;
        int idSliderBackground;

        if(paletteColor.equals("Red")) {
            textColor = res.getColor(R.color.ui_if_red);
            idSliderBackground = R.drawable.slidingright_red;
        }
        else if(paletteColor.equals("Yellow")) {
            textColor = res.getColor(R.color.ui_if_yellow);
            idSliderBackground = R.drawable.slidingright_yellow;
        }
        else if(paletteColor.equals("Green")) {
            textColor = res.getColor(R.color.ui_if_green);
            idSliderBackground = R.drawable.slidingright_greenlighter;
        }
        else if(paletteColor.equals("Orange")) {
            textColor = res.getColor(R.color.ui_if_orange);
            idSliderBackground = R.drawable.slidingright_orangedarker;
        }
        else if(paletteColor.equals("Teal")) {
            textColor = res.getColor(R.color.ui_if_teal);
            idSliderBackground = R.drawable.slidingright_aqua;
        }
        else if(paletteColor.equals("Blue")) {
            textColor = res.getColor(R.color.ui_if_blue);
            idSliderBackground = R.drawable.slidingright_bluedarker;
        }
        else if(paletteColor.equals("Purple")) {
            textColor = res.getColor(R.color.ui_if_purple);
            idSliderBackground = R.drawable.slidingright_purple;
        }
        else {
            textColor = res.getColor(R.color.ui_if_other);
            idSliderBackground = R.drawable.slidingright_greydarker;
        }

        // Now, finally set the colors
        mRightMenuTitleTextView.setTextColor(textColor);
        mRightMenuBodyTextView.setTextColor(textColor);
        mRightMenuTapMoreTextView.setTextColor(textColor);
        mRightMenuLinearLayout.setBackgroundResource(idSliderBackground);

        // Reset the ScrollView ie description to the top
        mRightMenuScrollView.scrollTo(0, 0);

        // TODO: On first run, arrow does not show. Double check this works and get it to update properly
        // Check if the scrollView is scrollable, and if so, set the indicator to visible
        View child = mRightMenuScrollView.getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            boolean isScrollable = mRightMenuScrollView.getHeight() < childHeight +
                    mRightMenuScrollView.getPaddingTop() +
                    mRightMenuScrollView.getPaddingBottom();

            if(isScrollable) {
                //Log.d(TAG, "Decided it was scrollable!");
                mRightMenuScrollArrow.setVisibility(View.VISIBLE);
            }
            else {
                //Log.d(TAG, "Decided it was not scrollable");
                mRightMenuScrollArrow.setVisibility(View.INVISIBLE);
            }
        }
       else {
            //Log.d(TAG, "No child found! ='<");
            // If can't even find the child, play it safe and hide the arrow
            mRightMenuScrollArrow.setVisibility(View.INVISIBLE);
        }

        // If there IS a full item URL, then include the tap more link
        if(fullItemURL == null) {
            // Hide the tap for more, if not necessary
            mRightMenuTapMoreTextView.setVisibility(View.GONE);
        }
        else {
            // Otherwise, make the tap for more textview visible then set its link
            mRightMenuTapMoreTextView.setVisibility(View.VISIBLE);

            // Create a new listener to open up the link
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start an intent to open up the URL in a browser
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(fullItemURL));
                    startActivity(intent);
                }
            };

            // Set the listener on the tap for more link
            mRightMenuTapMoreTextView.setOnClickListener(listener);
        }
    }

    /**
     * Changes the settings according to the passed in mode
     * @param mode - A specific SettingsType mode that states which group of settings to use
     */
    private void changeSettingsMode(SettingsType mode) {
        // First, enable/disable the left sliding menu as necessary
            // Only disable the left slider if using the MEMDetailed view
//        enableLeftSlider(!(mode == SettingsType.MEMDETAILED));
        enableLeftSlider(true);

        boolean usingCoolFeed = mode == SettingsType.ONECOOLFEED || mode == SettingsType.MICHENGMAG;

        // Decide on whether or not to enable the right slider
            // Only the CoolFeeds use the right slider
        enableRightSlider(usingCoolFeed);

        // Then toggle the landscape mode as necessary
        if(usingCoolFeed)
            toggleLandscapeMode(false);
        else
            toggleLandscapeMode(true); // Everything else uses landscape mode

        // Finally, change the ActionBar as necessary
        if(mode == SettingsType.ONECOOLFEED) {
            toggleActionBars(ActionbarHelper.ActionBarType.ONECOOLFEED);
        }
        else if(mode == SettingsType.MICHENGMAG) {
            toggleActionBars(ActionbarHelper.ActionBarType.MEMCOOLFEEED);
        }
        else if(mode == SettingsType.WEBVIEW || mode == SettingsType.DECODER) {
            // If so, show the ActionBar with a solid white bg
            toggleActionBars(ActionbarHelper.ActionBarType.SOLIDBG);
        }
        else if(mode == SettingsType.ABOUT) {
            // If so, then show the particular transparent ActionBar
            toggleActionBars(ActionbarHelper.ActionBarType.TRANSPARENT);
        }
        /*else if(mode == SettingsType.MEMDETAILED) {
            // Simply show the backOnly ActionBar
            toggleActionBars(ActionbarHelper.ActionBarType.BACKONLY);
        }*/
    }

    /**
     * Enables/disables the right sliding menu entirely
     * @param enable - True if now allowing the right slider to be used
     */
    private void enableRightSlider(boolean enable) {
        // Enable/disable the right sliding menu, for other controls
        mRightMenuEnabled = enable;

        // Enable the sliding menu, if the left sliding menu is NOT open
        if(enable && !mSlidingMenuLeft.isMenuShowing()) {
            mSlidingMenuRight.setSlidingEnabled(true);
        }
        // Otherwise, if disabling the right sliding menu, then simply disable it
        else if(!enable) {
            mSlidingMenuRight.setSlidingEnabled(false);
        }
    }

    /**
     * Enables/disables the left sliding menu entirely
     * @param enable - True if now allowing the left slider to be used
     */
    private void enableLeftSlider(boolean enable) {
        // If disabling the left sliding menu and it's open, close it
        if(!enable && mSlidingMenuLeft.isMenuShowing()) {
            toggleLeftSlidingMenu();
        }

        // Enable/disable the left sliding menu as necessary
        mSlidingMenuLeft.setSlidingEnabled(enable);
    }

    /**
     * Enables/disables landscape orientation entirely
     * @param enable - True if now allowing landscape mode now, false if disabling landscape mode
     */
    private void toggleLandscapeMode(boolean enable) {
        // If disabling, simply lock orientation to portrait mode
        if(!enable) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // Otherwise, let the user define the orientation to use
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    /**
     * Switches the correct ActionBar into view
     * @param mode - Defines which ActionBar should be visible
     */
    // TODO: Method deprecated, simply call the ActionbarHelper instead of this method
    private void toggleActionBars(ActionbarHelper.ActionBarType mode) {
        // Simply let the ActionbarHelper switch modes
        mActionbarHelper.toggleActionBars(mode);
    }

    /**
     * Toggle the left sliding menu. Was used for testing functions to occur at same time
     */
    private void toggleLeftSlidingMenu() {
        // If not showing the left sliding menu, show it
        if(!mSlidingMenuLeft.isMenuShowing()) {
            // Simply toggle it to show it
            mSlidingMenuLeft.toggle();
        }
        // If showing, then hide it
        else {
            mSlidingMenuLeft.toggle();
        }
    }

    // Override the menu key press so sliding menu can be open and closed by it
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU && !mSlidingMenuRight.isMenuShowing()) {
            toggleLeftSlidingMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Overrides the Up/Home button, so drawer icon toggles left sliding menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!mSlidingMenuRight.isMenuShowing())
                    toggleLeftSlidingMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // To remove the options menu - and make the custom actionBar take the full space -
            // always return false
        return false;
    }

    @Override
    public void onClick(View v) {
        // The only onClickListener that uses the Activity simply toggles the left slidingMenu

        // If the rightSlidingMenu is showing, close it
        if(mSlidingMenuRight.isMenuShowing()) mSlidingMenuRight.toggle();

        // Toggle the slidingMenu
        toggleLeftSlidingMenu();
    }

    //region -->Shake Code<--
    @Override
    public void hearShake() {
        // If the current displayed item doesn't do anything for shakes, then do nothing
        if(mShakeListener == null) {
            return;
        }

        // If there was a previous shake and it wasn't too soon, then handle the shake
        if(mLastShakeTime == 0 || (System.currentTimeMillis() - mLastShakeTime) > SHAKE_TIMOEUT) {
            // Register the current time as the last time when a shake occurred
            mLastShakeTime = System.currentTimeMillis();

            // If the user hasn't seen the shake dialog yet, show it
            if(!getIfSeenShakeDialog()) {
                // Show the first time shake dialog and let it handle it all
                showShakeDialog();
            }
            // Otherwise, if shake is allowed, let the shake listener handle the shake as necessary
            else if(isShakeEnabled()) {
                mShakeListener.onShake();
            }
        }
    }

    private boolean getIfSeenShakeDialog() {
        // Get and return the value from the preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(Constants.KEY_SEENSHAKEDIALOG, false);
    }

    // Sets sharedPreferences boolean of whether or not tutorial has been seen yet
    private void setSeenShakeDialog(boolean isDialogSeen) {
        // Get the shared preferences' editor
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set the saved bool to whatever the argument is
        editor.putBoolean(Constants.KEY_SEENSHAKEDIALOG, isDialogSeen);

        // Commit the changes
        editor.apply();
    }

    private void showShakeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.shake_dialog_message)
                .setTitle(R.string.shake_dialog_title);
        builder.setPositiveButton(R.string.shake_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Remember that the user has seen the dialog and wants shake enabled in the future
                setSeenShakeDialog(true);
                setShakeEnabled(true);

                // Let the shake listener handle the shake as necessary
                mShakeListener.onShake();
            }
        });
        builder.setNegativeButton(R.string.shake_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Remember that the user has seen the dialog
                setSeenShakeDialog(true);

                // Remember that the user wants to disable shake for random
                setShakeEnabled(false);
            }
        });

        AlertDialog shakeDialog = builder.create();
        shakeDialog.show();
    }

    private boolean isShakeEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(Constants.KEY_ENABLESHAKE, true);
    }

    private void setShakeEnabled(boolean isShakeEnabled) {
        // Get the shared preferences' editor
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set the saved bool to whatever the argument is
        editor.putBoolean(Constants.KEY_ENABLESHAKE, isShakeEnabled);

        // Commit the changes
        editor.apply();
    }
    //endregion

    // Adapter for the left sliding menu's nav listView
    class NavAdapter extends BaseAdapter {
        Context mContext;
        String[] navText;

        // Holds all the indicators, in order
        ArrayList<View> mIndicatorViewList;

        // Indicates the current visible indicator position
        int mCurrentVisiblePos = -1;

        // Initialize the adapter
        public NavAdapter(Context context) {
            this.mContext = context;

            // Init the indicator list
            initIndicatorList();

            // Initialize the String array for the navigation
            navText = getResources().getStringArray(R.array.nav_items);
        }

        // Initializes the indicator view list, as necessary
        private void initIndicatorList() {
          // TODO: Change this so doesn't manually need to be set in order to increase/decrease
          //          size of menu
          final int SIZE = 11;

            // Actually initialize the indicator list
            mIndicatorViewList = new ArrayList<View>(SIZE);

            // Fill the list with dummy data
            for(int i = 0; i < SIZE; ++i) {
                // Add a new dummy view into the indicator list
                mIndicatorViewList.add(null);
            }
        }

        // Sets the indicator position to the new position
        public void setVisibleIndicator(int newPos) {
            // If the input is the same as the old position, nothing necessary to be done
            if(newPos == mCurrentVisiblePos) return;

            // If the mCurrentVisiblePos is still -1, then return- nothing has been set yet
            if(mCurrentVisiblePos == -1) return;
            // TODO: Have a better fix for this crashing bug that only occurs when app first starts up!

            // First, make the old position invisible, if valid
            if(mCurrentVisiblePos > -1) {
                mIndicatorViewList.get(mCurrentVisiblePos).setVisibility(View.INVISIBLE);
            }

            // Then, set the new pos as visible
            mIndicatorViewList.get(newPos).setVisibility(View.VISIBLE);

            // Cache the newPos as the current position
            mCurrentVisiblePos = newPos;
        }

        @Override
        public int getCount() {
            return navText.length;
            //return navText.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = null;
            ViewHolder holder = null;

            if(convertView == null) {
                // Then have to inflate this row for the first time
                LayoutInflater inflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_drawer_item, parent, false);

                // Create a ViewHolder to save all the different parts of the row
                holder = new ViewHolder();
                holder.navTitle = (TextView) row.findViewById(R.id.name);
                holder.indicator = row.findViewById(R.id.arrow_indicator);

                // Make the row reuse the ViewHolder
                row.setTag(holder);
            }
            // Else, use the recycled view and holder
            else {
                row = convertView;
                holder = (ViewHolder) row.getTag();
            }

            // Set the text for this item
            holder.navTitle.setText(navText[position]);

            // Set the view's indicator as visible if this is the current one
            if(position == mCurrentFragmentIndex || (position == 0 && mCurrentFragmentIndex < 0) ){
                holder.indicator.setVisibility(View.VISIBLE);

                // Cache that this position is visible
                mCurrentVisiblePos = position;
            }
            else {
                // Otherwise, just hide it
                holder.indicator.setVisibility(View.INVISIBLE);
            }

            // Caches the indicator in its own special list, for enabling/disabling later
            mIndicatorViewList.set(position, holder.indicator);

            return row;
        }

        class ViewHolder {
            TextView navTitle;
            View indicator;
        }
    }
}
