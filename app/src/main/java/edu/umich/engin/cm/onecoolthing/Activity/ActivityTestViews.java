package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 13/10/14.
 *
 * Used to test different things first
 */
public class ActivityTestViews extends Activity {
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spinload);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
    }

    public void load(View view){
        spinner.setVisibility(View.VISIBLE);
    }
}
