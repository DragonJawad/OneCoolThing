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

    // URL to get contacts JSON
    private static String url = "http://api.androidhive.info/contacts/";

    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";

    // contacts JSONArray
    JSONArray contacts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

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
