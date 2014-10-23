package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.CoolThings.CoolThing;
import edu.umich.engin.cm.onecoolthing.CoolThings.CoolThingsListAdapter;
import edu.umich.engin.cm.onecoolthing.Fragments.FragmentVerticalPager;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.CheckNetworkConnection;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.ServiceHandler;

/**
 * Created by jawad on 18/10/14.
 *
 * Handles getting all Cool Thing objects and setting appropriate data
 * TODO: Save or at least cache some of data - JSON string and/or cool things
 * TODO: Optimize loading contacts- not all need to be saved/used at once
 */
public class ParseCoolThings {
    // Exact URL to get CoolThings from
    private static final String URL = "http://www.engin.umich.edu/college/about/news/news/coolthingindexjson";

    // JSON Tags
    private static final String TAG_ID = "ID";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_SUBTITLE = "Sub Title";
    private static final String TAG_BODYTEXT = "Body Text";
    private static final String TAG_IMAGEURL = "iPhone 5 Retina Image";

    // List of coolThings
    ArrayList<CoolThing> listCoolThings = new ArrayList<CoolThing>();

    public void setListView(Context context, ListView listView, ProgressDialog pDialog) {
        // If the network is available, go ahead and set everything up
        if(CheckNetworkConnection.isConnectionAvailable(context)) {
            // Let the subclass AsyncTask get and set the data
            GetCoolThings getCoolThings = new GetCoolThings(context, listView, pDialog);
            getCoolThings.execute();
        }
        else {
            // Otherwise, give a warning that there is no internet connection
            Toast.makeText(context, "Error: No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setVertPager(Context context, FragmentVerticalPager frag) {
        // If the network is available, go ahead
        if(CheckNetworkConnection.isConnectionAvailable(context)) {
            // Let the subclass AsyncTask get and set the data
            GetCoolThings getCoolThings = new GetCoolThings(context, frag);
            getCoolThings.execute();
        }
        else {
            // TODO: Give prompt to open up internet connection

            // Otherwise, give a warning that there is no internet connection
            Toast.makeText(context, "Error: No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Async class to get Cool Things
     */
    private class GetCoolThings extends AsyncTask<Void, Void, Void> {
        Context mContext;

        FragmentVerticalPager fragmentVerticalPager; // Used to set up a VerticalPager
        ListView targetList; // Used to set up a listView, if necessary
        ProgressDialog pDialog; // Optional pDialog object

        // Simple constructor, for simply getting the contacts
        public GetCoolThings() {}

        // Constructor, for setting up a CoolThing
        public GetCoolThings(Context context, FragmentVerticalPager frag) {
            this.mContext = context;
            this.fragmentVerticalPager = frag;
        }

        // Constructor, to set up a listView
        public GetCoolThings(Context context, ListView listView, ProgressDialog progressDialog) {
            this.mContext = context;
            this.targetList = listView;
            this.pDialog = progressDialog;
        }

        // Sets up the listView
        private void setUpList(boolean useImages) {
            // If there's a listView, set it up
            if(targetList != null) {
                CoolThingsListAdapter listAdapter =
                        new CoolThingsListAdapter(mContext, listCoolThings, useImages);
                targetList.setAdapter(listAdapter);
            }
        }

        // If there is a pDialog, use it
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(pDialog != null) {
                // Showing progress dialog
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create service handler class instance to handle HTTP
            ServiceHandler sh = new ServiceHandler();

            // Make a request to url and get response
            String jsonStr = sh.makeServiceCall(URL, ServiceHandler.GET);

            Log.d("MD/JSONResponse: ", "> " + jsonStr);

            if(jsonStr != null) {
                try {
                    // Get the overarching JSON array
                    JSONArray jsonData = new JSONArray(jsonStr);

                    // Loop through each object and create a CoolThing
                    for(int i = 0; i < jsonData.length(); ++i) {
                        // Get the current data object
                        JSONObject jsonObject = jsonData.getJSONObject(i);

                        // Get the id first, for quick checking
                        String id = jsonObject.getString(TAG_ID);

                        // Get the necessary data from the object
                        String title = jsonObject.getString(TAG_TITLE);
                        String subTitle = jsonObject.getString(TAG_SUBTITLE);
                        String body = jsonObject.getString(TAG_BODYTEXT);
                        String imageURL = jsonObject.getString(TAG_IMAGEURL);

                        // Make a new Cool Thing object to hold this data
                        CoolThing coolThing = new CoolThing(id, title, body);
                        coolThing.setImageURL(imageURL);
                        coolThing.setSubTitle(subTitle);

                        // Add the new cool thing to the list of awesome cool things
                        listCoolThings.add(coolThing);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Dismiss the progress dialog
            if(pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            Log.d("MD/JSONParser", "Finished parsing JSON");

            // Set up the listView, if necessary
            if(targetList != null) {
                setUpList(false);
            }

            // Set up the VerticalPager, if necessary
            if(fragmentVerticalPager != null) {
                fragmentVerticalPager.initCoolViewPager(mContext, listCoolThings);
            }
        }
    }
}
