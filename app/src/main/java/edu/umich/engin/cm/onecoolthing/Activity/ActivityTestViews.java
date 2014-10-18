package edu.umich.engin.cm.onecoolthing.Activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ParseCoolThings;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.ServiceHandler;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 13/10/14.
 *
 * Used to test different things first
 */
public class ActivityTestViews extends ListActivity {
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_views);

        // Test out JSON parsing
        testJSON();
    }

    private void testJSON() {
        // Use the parser to set everything up
        ParseCoolThings parser = new ParseCoolThings();
        parser.setListView(this, getListView(), pDialog);
    }
}
