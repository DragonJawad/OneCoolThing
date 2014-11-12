package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentBase;
import edu.umich.engin.cm.onecoolthing.Fragments.FragmentOneCoolFeed;
import edu.umich.engin.cm.onecoolthing.Fragments.FragmentTumblrFeed;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 12/10/14.
 */
public class ActivityTestCenter extends Activity implements FragmentOneCoolFeed.VertPagerCommunicator {
    // Log tag for this class
    private final String TAG = "MD/ActivityTestCenter";

    // The single One Cool Feed fragment to use
    private FragmentOneCoolFeed mFragOneCoolFeed;

    // Tags for all fragments
    private String[] mFragTags;
    // URLs for all feeds- same indexes as currentFragmentIndex and null == no webview
    private String[] mFragUrls;

    // Sliding Menu test objects
    private SlidingMenu mSlidingMenuLeft;
    private SlidingMenu mSlidingMenuRight;

    // Determines whether or not the right sliding menu is enabled
    private boolean mRightMenuEnabled = false;

    // The right sliding menu's repeatedly accessed views
    LinearLayout mRightMenuLinearLayout;
    TextView mRightMenuTitleTextView;
    TextView mRightMenuBodyTextView;
    ScrollView mRightMenuScrollView;

    // Actionbar Views
    View mViewActionBarSimple;
    View mViewActionBarWithTitle;
    // Actionbar itself
    ActionBar mActionBar;

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
    int currentFragmentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize the custom actionBar views and set the first actionBar up
        initActionBar();

        // Initialize the sliding menus
        initBothSlidingMenus();

        // Get all the fragments' tags - or names - from the nav list in resources
        mFragTags = getResources().getStringArray(R.array.nav_items);
        // Get all fragments' urls, if they exist
        mFragUrls = getResources().getStringArray(R.array.nav_items_urls);

        // Initialize the one cool feed [which also adds it to the center as well]
        initOneCoolFeedFrag();
    }

    private void initActionBar() {
        // Get the LayoutInflater to inflate the views
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the simple ActionBar view
        mViewActionBarSimple = inflater.inflate(R.layout.actionbar_simple, null);

        // Initialize and set up the ActionBar
        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);

        // Set the actionBar layout initially to the simple one
        mActionBar.setCustomView(mViewActionBarSimple);
    }

    private void initOneCoolFeedFrag() {
        // Actually initialize the fragment
        mFragOneCoolFeed = new FragmentOneCoolFeed();

        // Let the frag communicate with this activity
        mFragOneCoolFeed.setCommunicator(this);

        // Add in the fragment to the place specified in the layout file
        getFragmentManager().beginTransaction()
                .add(R.id.container, mFragOneCoolFeed, mFragTags[0])
                .commit();

        // Set the particular activity settings for the center view
        toggleCoolThingSettings(true);

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
        ListView listNav = (ListView)viewLeftMenu.findViewById(R.id.list);
        NavAdapter adapter = new NavAdapter(this);
        listNav.setAdapter(adapter);
        // Set a click listener for the listView
        listNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Change out the current fragment displayed in the center
                changeFrag(position);

                // Toggle/close the left sliding menu
                mSlidingMenuLeft.toggle();
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
        mRightMenuScrollView = (ScrollView) rightMenuView.findViewById(R.id.scroll_description);

        // Set listeners for the left and right sliding menus [so both aren't open at once]
        // TODO: Make them call a function that takes in a boolean.
        // Function would possibly close other if open, check if already disabled, etc
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
        //    Log.d(TAG, "Showing center fragment");

            // Simply show the OneCoolFeed and set up the settings using its function
            fragmentTransaction.show(mFragOneCoolFeed);
            fragmentTransaction.commit();

            // Apply the settings for the OneCoolFeed
            toggleCoolThingSettings(true);
        }
        // Otherwise, if this index has an URL, open up a feed
        else if(!mFragUrls[index].equals("")) {
        //    Log.d(TAG, "Opening up a webview");

            // Get the url and title separately, for ease of typing/reading
            String this_url = mFragUrls[index];
            String this_title = mFragTags[index];

            // Create a new TumblrFeed fragment, with its title and url
            FragmentTumblrFeed frag = FragmentTumblrFeed.newInstance(this_url, this_title);

            // Add the url to the center view
            fragmentTransaction.add(R.id.container, frag, mFragTags[index]);
            fragmentTransaction.commit();

            // Set settings for this view
            // Currently: Undo the settings for the One Cool Feed
            toggleCoolThingSettings(false);
        }
        else {
            //    Log.d(TAG, "Created general fragment " + index);

            // Otherwise, add a fill-in frag
            FragmentBase frag = new FragmentBase();
            frag.changeBG(R.color.dev_blue);

            fragmentTransaction.add(R.id.container, frag, mFragTags[index]);
            fragmentTransaction.commit();

            // Set settings for this view
            // Currently: Undo the settings for the One Cool Feed
            toggleCoolThingSettings(false);
        }

        // Finally, change the index of the currently used fragment
        currentFragmentIndex = index;
    }

    // Change the right slide to match the current CoolThing
    public void changeRightSlide(String subTitle, String body, String paletteColor) {
        // Change the body and subtitle texts
        mRightMenuTitleTextView.setText(subTitle);
        mRightMenuBodyTextView.setText(body);

        // TODO: ...Find some nice midpoint between strings values and readability in code here
            // Ie, so that not hardcoding exact strings but can still easily tell what color here
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
        mRightMenuLinearLayout.setBackgroundResource(idSliderBackground);

        // Reset the ScrollView ie description to the top
        mRightMenuScrollView.scrollTo(0, 0);
    }

    /**
     * Toggle the settings necessary for the CoolThing views
     * @param enable - True if now enabling the CoolThing center view. Vice versa if removing view
     */
    private void toggleCoolThingSettings(boolean enable) {
        // Toggle the right slider (in terms of enabling or disabling it)
        enableRightSlider(enable);

        // Toggle the landscape mode - if enabling the CoolThing view, disable the landscape mode
        toggleLandscapeMode(!enable);
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

    // Override the menu key press so sliding menu can be open and closed by it
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU && !mSlidingMenuRight.isMenuShowing()) {
            this.mSlidingMenuLeft.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Overrides the Up/Home button, so drawer icon toggles left sliding menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.mSlidingMenuLeft.toggle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Adapter for the left sliding menu's listView
        // TODO: Make this cleaner, more efficient, and more towards the final design
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
            return navText.length;
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

            if(convertView == null) {
                // Then have to inflate this row for the first time
                LayoutInflater inflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_drawer_item, parent, false);
            }
            // Else, use the recycled view
            else row = convertView;

            // Change the text of the item
            TextView navTitle = (TextView) row.findViewById(R.id.name);
            // Set the text
            navTitle.setText(navText[position]);

            return row;
        }
    }
}
