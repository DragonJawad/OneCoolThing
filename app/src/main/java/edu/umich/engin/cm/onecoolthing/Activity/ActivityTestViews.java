package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.MichEngMag.MichEngMag;
import edu.umich.engin.cm.onecoolthing.MichEngMag.ParseMichEngMag;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 13/10/14.
 *
 * Used to test different things first
 */
public class ActivityTestViews extends Activity implements ParseMichEngMag.MagSubscriber {
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spinload);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        // Test parseMichEngMag
        ParseMichEngMag parser = new ParseMichEngMag();
        parser.getData(this, this);
    }

    public void load(View view){
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void gotMagazine(ArrayList<MichEngMag> magazineList) {
        Log.d("MD/ActivityTestViews", "Got teh data!");
    }
}
