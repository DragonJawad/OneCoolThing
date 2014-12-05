package edu.umich.engin.cm.onecoolthing.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 13/11/14.
 *
 * Following StackOverflow instructions to use perfectly custom ActionBar
 */
public class ActivityBaseActionbar extends Activity {
    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
    }

    @SuppressLint("InlinedApi")
    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(R.layout.activity_main);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.mainContainer);
        viewGroup.removeAllViews();
    //    viewGroup.addView(getLayoutInflater().inflate(layoutResID, null));
        // you can find action_bar layouts view & add listner
    }
}