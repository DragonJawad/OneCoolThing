package edu.umich.engin.cm.onecoolthing.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.umich.engin.cm.onecoolthing.CoolThings.OneCoolFeedFrag;
import edu.umich.engin.cm.onecoolthing.MichEngMag.MichEngMagParserFrag;
import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.StandaloneFragments.AboutFragment;
import edu.umich.engin.cm.onecoolthing.StandaloneFragments.WebFeedFragment;
import edu.umich.engin.cm.onecoolthing.Util.ObservableScrollView;
import edu.umich.engin.cm.onecoolthing.Util.ScrollViewListener;

/**
 * Created by jawad on 12/10/14.
 */
public class ActivityTestCenter extends Activity implements OneCoolFeedFrag.VertPagerCommunicator,
    View.OnClickListener {
    // Log tag for this class
    private final String TAG = "MD/ActivityTestCenter";

    // Enums for Activity setting configurations
    private static enum SettingsType {
        ONECOOLFEED, // Settings for the One Cool Feed
        WEBVIEW,     // Settings for the WebView fragments, like the Tumblr feeds
        ABOUT        // Settings for the About page
    }

    // The single One Cool Feed fragment to use
    private OneCoolFeedFrag mFragOneCoolFeed;

    // Tags for all fragments
    private String[] mFragTags;
    // URLs for all feeds- same indexes as currentFragmentIndex and null == no webview
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

    // Actionbar Views
    View mViewActionBarTransparent;
    View mViewActionBarSolidBg;
    TextView mActionTransBgTitle;
    TextView mActionSolidBgTitle;

    /* Current center fragment index, for reference
     * Below is the master list - If the nav is changed, change the below and any
     *      code that uses the below system, ie
     *          strings.xml -> nav_items array
     *          this -> changeFrag()
     * 0 - One Cool Feed [main One Cool Thing feed]
     * 1 - LabLog
     * 2 - Visual Adventures
     * 3 - Michigan Engineer Mag
     * 4 - I <3 A2
     * 5 - Some Cool Apps
     * 6 - MichEpedia
     * 7 - Decoder
     * 8 - About
     */
    /* NOTE: Decoder removed for now from app! Search for "DECODE-FIX" to find code where it is specifically circumvented
     */
    int currentFragmentIndex = -1;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the actionBar in its evil entirety
        getActionBar().hide();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        View homeIcon = findViewById(android.R.id.home);
        // Hides the View (and so the icon)
        if (homeIcon != null)
            ((View) homeIcon.getParent()).setVisibility(View.GONE);

        overridePendingTransition(0, 0);

        setContentView(R.layout.activity_test);
        // Remove the rest of the evilness of the ActionBar
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.mainContainer);
    //    viewGroup.removeAllViews();

        // Initialize the custom actionBar views and set the first actionBar up
        initCustomActionBar();

        // Initialize the sliding menus
        initBothSlidingMenus();

        // Get all the fragments' tags - or names - from the nav list in resources
        mFragTags = getResources().getStringArray(R.array.nav_items);
        // Get all fragments' urls, if they exist
        mFragUrls = getResources().getStringArray(R.array.nav_items_urls);

        // Initialize the one cool feed [which also adds it to the center as well]
        initOneCoolFeedFrag();
    }

    private void initCustomActionBar() {
        // Get the LayoutInflater to inflate the views
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the simple ActionBar view
        mViewActionBarTransparent = inflater.inflate(R.layout.actionbar_withtransbg, null);

        // Set the imageButton from the simple view to toggle the slidingMenu
        ((ImageButton) mViewActionBarTransparent.findViewById(R.id.navButton))
                .setOnClickListener(this);

        // Inflate the ActionBar with the title view and solid white background
        mViewActionBarSolidBg = inflater.inflate(R.layout.actionbar_withsolidbg, null);

        // Set the imageButton from the view with title to toggle the slidingMenu
        ((ImageButton) mViewActionBarSolidBg.findViewById(R.id.navButton))
                .setOnClickListener(this);

        // Get the title textView from the transparent bg view so the title can be set later
        mActionTransBgTitle = (TextView) mViewActionBarTransparent.findViewById(R.id.textTitle);

        // Get the title textView from the solid bg view so the title can be set later
        mActionSolidBgTitle = (TextView) mViewActionBarSolidBg.findViewById(R.id.textTitle);

        // Add the actionBars to the respective container
        RelativeLayout viewGroup = (RelativeLayout) findViewById(R.id.actionbar_container);
        viewGroup.addView(mViewActionBarTransparent);
        viewGroup.addView(mViewActionBarSolidBg);

        // Hide the actionBar with the title- the simple actionBar is the one to use at the moment
        mViewActionBarSolidBg.setVisibility(View.INVISIBLE);
    }

    private void initOneCoolFeedFrag() {
        // Actually initialize the fragment
        mFragOneCoolFeed = new OneCoolFeedFrag();

        // Let the frag communicate with this activity
        mFragOneCoolFeed.setCommunicator(this);

        // Add in the fragment to the place specified in the layout file
        getFragmentManager().beginTransaction()
                .add(R.id.fragContainer, mFragOneCoolFeed, mFragTags[0])
                .commit();

        // Set the particular activity settings for the initial, One Cool Feed center view
        changeSettingsMode(SettingsType.ONECOOLFEED);

        // Set the index of the currentFragmentIndex to 0, to show that the OneCoolFeed was added
        currentFragmentIndex = 0;
    }

    // Set up the right and left sliding menus
    private void initBothSlidingMenus() {
        // Get the LayoutInflater to inflate the views for the left sliding menu
        LayoutInflater inflater = getLayoutInflater();

        // Initialize the left sliding menu
        mSlidingMenuLeft = new SlidingMenu(this);
        mSlidingMenuLeft.setMode(SlidingMenu.LEFT); // Define the orientation to the left
        mSlidingMenuLeft.setShadowDrawable(R.drawable.slidingmenu_shadow_left);

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
                // TODO: Skip if currently at this posit
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
        mSlidingMenuRight.setShadowDrawable(R.drawable.slidingmenu_shadow_right);

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
        final ImageView shareEmail = (ImageView) rightMenuView.findViewById(R.id.share_icon_email);
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

                        Toast.makeText(getBaseContext(), "Tapped the image!", Toast.LENGTH_SHORT).show();

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

                        // React depending on which button was tapped
                        if(v == shareFacebook) {
                            Toast.makeText(getBaseContext(), "Tapped the Facebook share button!", Toast.LENGTH_SHORT).show();
                        }
                        else if(v == shareTwitter) {
                            Toast.makeText(getBaseContext(), "Tapped the Twitter share button!", Toast.LENGTH_SHORT).show();
                        }
                        else if(v == shareEmail) {
                            Toast.makeText(getBaseContext(), "Tapped the Email share button!", Toast.LENGTH_SHORT).show();
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
        shareEmail.setOnTouchListener(shareSpecificTouchListener);

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

    /**
     * Changes out the center fragment as necessary
     * @param index - Index chosen by user in navigation menu
     */
    private void changeFrag(int index) {
        // First check if user clicked on the current fragment again
        if (index == currentFragmentIndex) return;

        // DECODE-FIX
        if( (index == 7 && currentFragmentIndex == 8) ||
                (index == 8 && currentFragmentIndex == 7) ) return;

        // Begin the fragment transaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // First, clear out the center container - if not the main frag, then simply remove middle
        if (currentFragmentIndex != 0) {
            // Get the fragment to remove
            Fragment fragment = fragmentManager.findFragmentByTag(mFragTags[currentFragmentIndex]);
            fragmentTransaction.remove(fragment);

            Log.d(TAG, "Removed fragment: " + currentFragmentIndex);
        }
        // Otherwise, if this is the One Cool Feed, simply hide it- don't remove it
        // in order to avoid fixing the lifecycle
        else {
            fragmentTransaction.hide(mFragOneCoolFeed);
            Log.d(TAG, "Hid center fragment");
        }

        // Then add in the chosen fragment and set the appropriate settings
        if (index == 0) {
            // Simply show the OneCoolFeed and set up the settings using its function
            fragmentTransaction.show(mFragOneCoolFeed);
            fragmentTransaction.commit();

            // Apply the settings for the OneCoolFeed
            changeSettingsMode(SettingsType.ONECOOLFEED);
        }
        // If so, then add in the Michigan Engineer Magazine fragment
        else if(index == 3) {
            // Get the title/tag separately, for ease of typing/reading
            String this_title = mFragTags[index];
            // Put the title on the actionBar that will be used
            mActionSolidBgTitle.setText(this_title);

            // Create a new fragment to use
            MichEngMagParserFrag frag = new MichEngMagParserFrag();

            // Add the frag to the center view
            fragmentTransaction.add(R.id.fragContainer, frag, this_title);
            fragmentTransaction.commit();

            // Set settings for this view- same as a Webview
            changeSettingsMode(SettingsType.WEBVIEW);
        }
        // Otherwise, if this index has an URL, open up a feed
        else if(!mFragUrls[index].equals("")) {
            // Get the url and title separately, for ease of typing/reading
            String this_url = mFragUrls[index];
            String this_title = mFragTags[index];

            // Set the actionBar's title text (on the one that will be used)
            mActionSolidBgTitle.setText(this_title);

            // Create a new TumblrFeed fragment, with its title and url
            WebFeedFragment frag = WebFeedFragment.newInstance(this_url, this_title);

            // Add the webview to the center view
            fragmentTransaction.add(R.id.fragContainer, frag, mFragTags[index]);
            fragmentTransaction.commit();

            // Set settings for this web view
            changeSettingsMode(SettingsType.WEBVIEW);
        }
        // If so, then add in the About fragment
        else if(index == 8 || index == 7) { // DECODE-FIX
            // Get the title/tag separately, for ease of typing/reading
            String this_title = mFragTags[index];
            // Put the title on the actionBar that will be used
            mActionTransBgTitle.setText(this_title);

            // Create a new fragment to use
            AboutFragment frag = new AboutFragment();

            // Add the frag to the center view
            fragmentTransaction.add(R.id.fragContainer, frag, this_title);
            fragmentTransaction.commit();

            // Set settings for this view- same as a Webview
            changeSettingsMode(SettingsType.ABOUT);
        }

        // Finally, change the index of the currently used fragment
        currentFragmentIndex = index;
    }

    // Change the right slide to match the current CoolThing
    public void changeRightSlide(String subTitle, String body, String paletteColor, final String fullItemURL) {
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
                // TODO: Think of a more efficient way to do this
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
        // First, decide on whether or not to enable the right slider
        if(mode == SettingsType.ONECOOLFEED) {
            // Enable the right sliding menu ONLY if this is the One Cool Feed
            enableRightSlider(true);
        }
        else {
            // Otherwise, all other modes currently don't use the right sliding menu
            enableRightSlider(false);
        }

        // Then toggle the landscape mode as necessary
            // TODO: Currently, landscape mode is too buggy to use
        toggleLandscapeMode(false); // For now, permanently disable landscape mode

        // Finally, change the ActionBar as necessary
        if(mode == SettingsType.ONECOOLFEED) {
            // If so, then set the transparent ActionBar up without a title
            toggleTransparentActionBar(true);
            mActionTransBgTitle.setText("");
        }
        else if(mode == SettingsType.WEBVIEW) {
            // If so, show the ActionBar with a solid white bg
            toggleTransparentActionBar(false);
        }
        else if(mode == SettingsType.ABOUT) {
            // If so, then set the transparent ActionBar up with its specific title
            toggleTransparentActionBar(true);
            mActionTransBgTitle.setText(mFragTags[8]); // Index for About is currently 8
        }
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
     * Switches the ActionBar out
     * @param enable - True if to show the transparent actionBar, false if using one with solid bg
     */
    private void toggleTransparentActionBar(boolean enable) {
        if(enable) {
            // If the simple, transparent actionbar is NOT visible, then must switch it out
            if(mViewActionBarTransparent.getVisibility() != View.VISIBLE) {
                // Make the title actionbar invisible
                mViewActionBarSolidBg.setVisibility(View.INVISIBLE);

                // Make the simple actionbar visible
                mViewActionBarTransparent.setVisibility(View.VISIBLE);
            }
        }
        else {
            // If the title actionbar is NOT visible, then must switch it out
            if(mViewActionBarSolidBg.getVisibility() != View.VISIBLE) {
                // Make the simple actionbar invisible
                mViewActionBarTransparent.setVisibility(View.INVISIBLE);

                // Make the title actionbar visible
                mViewActionBarSolidBg.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Toggle the left sliding menu
     */
    private void toggleLeftSlidingMenu() {
        // If not showing the left sliding menu, show it
        if(!mSlidingMenuLeft.isMenuShowing()) {
            // Simply toggle it to show it
            mSlidingMenuLeft.toggle();

            // Then, set the highlighted nav item
        //    setHighlightedNavRow();
        }
        // If showing, then hide it
        else {
            mSlidingMenuLeft.toggle();
        }
    }

    private void setHighlightedNavRow() {
        // In case this is the first setup, then check that a mCurrentNav was already set
    //    if(mCurrentNavView != null)
    //        mCurrentNavView.setSelected(true);

        Log.d(TAG, "Set the selected nav row with position " + currentFragmentIndex);

        // TODO: Test
        mListNav.invalidateViews();

        /*
        // If the current fragment index is not yet set, just use the first position
        int selection = currentFragmentIndex;
        if(currentFragmentIndex == -1) selection = 0;

        // Get the selected row and set it as focused and selected
        View view = mListNav.getChildAt(selection);
        view.setSelected(true);

        if(view == null) Log.d(TAG, "View was null!");
        */
    }

    private void setHighlightedNavRow(int position) {
        mListNav.setItemChecked(position, true);
    }

    private void setHighlightedNavRow(View navView) {
        navView.setSelected(true);
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

    // Adapter for the left sliding menu's nav listView
    class NavAdapter extends BaseAdapter {
        Context mContext;
        String[] navText;

        // Initialize the adapter
        public NavAdapter(Context context) {
            this.mContext = context;

            // Initialize the String array for the navigation
            navText = getResources().getStringArray(R.array.nav_items);
        }

        @Override
        public int getCount() {
            // DECODE-FIX
            return navText.length-1;
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
            if(position != 7 && position != 8) // DECODE-FIX
                holder.navTitle.setText(navText[position]);
            else holder.navTitle.setText(navText[8]);

            // Set the view's indicator as visible if this is the current one
            if(position == currentFragmentIndex || (position == 0 && currentFragmentIndex < 0) ){
                holder.indicator.setVisibility(View.VISIBLE);
            }
            else {
                // Otherwise, just hide it
                holder.indicator.setVisibility(View.INVISIBLE);
            }


            return row;
        }

        class ViewHolder {
            TextView navTitle;
            View indicator;
        }
    }
}
