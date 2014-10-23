package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.umich.engin.cm.onecoolthing.CoolThings.ParseCoolThings;
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
        addCenterView();

       // Initialize the sliding menus
        initBothSlidingMenus();
    }

    private void addCenterView() {
        // Create and setup the center fragment, as necessary
        FragmentVerticalPager frag = new FragmentVerticalPager();

        // Add in the fragment to the place specified in the layout file
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.container, frag);
        fragmentTransaction.commit();

        // Let the frag communicate with this activity
        frag.setCommunicator(this);

        // Tell ParseCoolThings to set up the VerticalPager
        ParseCoolThings parser = new ParseCoolThings();
        parser.setVertPager(this, frag);
    }

    // Set up the right and left sliding menus
    private void initBothSlidingMenus() {
        // Initialize the left sliding menu
        slidingMenuLeft = new SlidingMenu(this);
        slidingMenuLeft.setMode(SlidingMenu.LEFT); // Define the orientation to the left
        slidingMenuLeft.setShadowDrawable(R.drawable.slidingmenu_shadow_left);
        slidingMenuLeft.setMenu(R.layout.slidingmenu_left); // Assign the layout/content

        // Initialize the right sliding menu
        slidingMenuRight = new SlidingMenu(this);
        slidingMenuRight.setMode(SlidingMenu.RIGHT); // Define the orientation to the right
        slidingMenuRight.setShadowDrawable(R.drawable.slidingmenu_shadow_right);

        // Inflate a view for the right sliding menu
        viewRightMenu = getLayoutInflater().inflate(R.layout.slidingmenu_right,null);
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

        // Set up the rest of the sliding menu properties
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

        // Change the views according to the passed parameters
        subTitleView.setText(subTitle);
        bodyTextView.setText(body);
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

    /*
    // Override the back button press so can close sliding menu instead
    @Override
    public void onBackPressed() {
        if ( slidingMenuLeft.isMenuShowing() ) {
            slidingMenuLeft.toggle();
        }
        else if( slidingMenuRight.isMenuShowing() ) {
            slidingMenuRight.toggle();
        }
        else {
            super.onBackPressed();
        }
    }

    // This may be scrapped soon, but toggles sliding menu by home button
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
    */
}
