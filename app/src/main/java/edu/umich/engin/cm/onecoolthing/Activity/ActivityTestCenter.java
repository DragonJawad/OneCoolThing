package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 12/10/14.
 */
public class ActivityTestCenter extends Activity {
    // Sliding Menu test objects
    SlidingMenu slidingMenuLeft;
    SlidingMenu slidingMenuRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize the left sliding menu
        slidingMenuLeft = new SlidingMenu(this);
        slidingMenuLeft.setMode(SlidingMenu.LEFT);
        slidingMenuLeft.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenuLeft.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenuLeft.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenuLeft.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenuLeft.setFadeDegree(0.35f);
        slidingMenuLeft.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenuLeft.setMenu(R.layout.slidingmenu_test);

        // Initialize the right sliding menu
        slidingMenuRight = new SlidingMenu(this);
        slidingMenuRight.setMode(SlidingMenu.RIGHT);
        slidingMenuRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenuRight.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenuRight.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenuRight.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenuRight.setFadeDegree(0.35f);
        slidingMenuRight.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenuRight.setMenu(R.layout.slidingmenu_test);

    //    getActionBar().setDisplayHomeAsUpEnabled(true);
    }

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

    // Override the menu key press so sliding menu can be open and closed by it
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            this.slidingMenuLeft.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
}
