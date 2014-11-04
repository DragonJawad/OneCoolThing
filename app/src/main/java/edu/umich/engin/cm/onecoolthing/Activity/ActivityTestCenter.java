package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentBase;
import edu.umich.engin.cm.onecoolthing.Fragments.FragmentVerticalPager;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 12/10/14.
 */
public class ActivityTestCenter extends Activity implements FragmentVerticalPager.VertPagerCommunicator {
    // Sliding Menu test objects
    SlidingMenu slidingMenuLeft;
    SlidingMenu slidingMenuRight;

    // The right sliding menu's view, for easy access to change its contents
    View viewRightMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Add in the center view
        addCenterCoolThings();

        // Initialize the sliding menus
        initBothSlidingMenus();
    }

    private void addCenterCoolThings() {
        // Create and setup the center fragment, as necessary
        FragmentVerticalPager frag = new FragmentVerticalPager();

        // Add in the fragment to the place specified in the layout file
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag);
        fragmentTransaction.commit();

        // Let the frag communicate with this activity
        frag.setCommunicator(this);

        // Set the particular activity settings for the center view
        toggleCoolThingSettings(true);
    }

    // Set up the right and left sliding menus
    private void initBothSlidingMenus() {
        // Get the LayoutInflater to inflate the views for the sliding menus
        LayoutInflater inflater = getLayoutInflater();

        // Initialize the left sliding menu
        slidingMenuLeft = new SlidingMenu(this);
        slidingMenuLeft.setMode(SlidingMenu.LEFT); // Define the orientation to the left
        slidingMenuLeft.setShadowDrawable(R.drawable.slidingmenu_shadow_left);

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
                // Highlight the selected item
                view.setSelected(true);

                // Change out the current fragment displayed in the center
                    // TODO: Make this it's own function
                if(position == 0) addCenterCoolThings(); // Add the CoolThings home view
                else {
                    // Otherwise add a fill in frag
                    FragmentBase frag = new FragmentBase();
                    frag.changeBG(R.color.dev_blue);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container, frag);
                    fragmentTransaction.commit();
                }
            }
        });

        slidingMenuLeft.setMenu(viewLeftMenu); // Assign the layout/content

        // Initialize the right sliding menu
        slidingMenuRight = new SlidingMenu(this);
        slidingMenuRight.setMode(SlidingMenu.RIGHT); // Define the orientation to the right
        slidingMenuRight.setShadowDrawable(R.drawable.slidingmenu_shadow_right);

        // Inflate a view for the right sliding menu
        viewRightMenu = inflater.inflate(R.layout.slidingmenu_right,null);
        slidingMenuRight.setMenu(viewRightMenu);

        // Set listeners for the left and right sliding menus [so both aren't open at once]
        // TODO: Make them call a function that takes in a boolean.
        // Function would possibly close other if open, check if already disabled, etc
        slidingMenuLeft.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                // Disable the right sliding menu
                slidingMenuRight.setSlidingEnabled(false);
            }
        });

        slidingMenuLeft.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                // Enable the right sliding menu
                slidingMenuRight.setSlidingEnabled(true);
            }
        });

        slidingMenuRight.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                // Disable the left sliding menu
                slidingMenuLeft.setSlidingEnabled(false);
            }
        });

        slidingMenuRight.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                // Enable the left sliding menu
                slidingMenuLeft.setSlidingEnabled(true);
            }
        });

        // Set up the rest of the shared sliding menu properties
        setUpSlidingMenu(slidingMenuLeft);
        setUpSlidingMenu(slidingMenuRight);
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

    // Testing function: change the right sliding menu's background color
    public void changeRightSlide(int color) {
        // For now, output the passed in color
        Toast.makeText(this, "Passed in color: " + color, Toast.LENGTH_SHORT).show();

        // Change the right slider's container's color
        LinearLayout container = (LinearLayout)viewRightMenu.findViewById(R.id.container_right_sliding);
        container.setBackgroundColor(color);
    }

    // Change the right slide to match the current CoolThing
    public void changeRightSlide(String subTitle, String body) {
        // Get the views to change
            // TODO: Cache these early on
        TextView subTitleView = (TextView) viewRightMenu.findViewById(R.id.subTitle);
        TextView bodyTextView = (TextView) viewRightMenu.findViewById(R.id.bodyText);
        ScrollView scrollDescription = (ScrollView) viewRightMenu.findViewById(R.id.scroll_description);

        // Change the views according to the passed parameters
        subTitleView.setText(subTitle);
        bodyTextView.setText(body);

        // Reset the ScrollView ie description to the top
        scrollDescription.scrollTo(0,0);
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
        if ( keyCode == KeyEvent.KEYCODE_MENU && !slidingMenuRight.isMenuShowing()) {
            this.slidingMenuLeft.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Overrides the Up/Home button, so drawer icon toggles left sliding menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.slidingMenuLeft.toggle();
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
