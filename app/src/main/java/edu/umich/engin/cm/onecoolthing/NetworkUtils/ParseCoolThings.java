package edu.umich.engin.cm.onecoolthing.NetworkUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.Activity.ActivityTestViews;

/**
 * Created by jawad on 18/10/14.
 *
 * Handles getting all Cool Thing objects and setting appropriate data
 */
public class ParseCoolThings {
    private static final String URL = "http://www.engin.umich.edu/college/about/news/news/coolthingindexjson";

    // JSON Tags
    private static final String TAG_ID = "ID";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_BODYTEXT = "Body Text";

    // List of coolThings
    ArrayList<CoolThing> listCoolThings = new ArrayList<CoolThing>();

    public void setListView(Context context, ListView listView, ProgressDialog pDialog) {
        Log.d("MD/JSON", "Got to Point A");

        // If the network is available, go ahead and set everything up
        if(CheckNetworkConnection.isConnectionAvailable(context)) {
            Log.d("MD/JSON", "Got to Point B");

            // Let the subclass AsyncTask get and set the data
            GetCoolThings getCoolThings = new GetCoolThings(context, listView, pDialog);
            getCoolThings.execute();
        }
        else {
            // Otherwise, give a warning that there is no internet connection
            Toast.makeText(context, "Error: No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Async class to get Cool Things
     */
    private class GetCoolThings extends AsyncTask<Void, Void, Void> {
        Context mContext;

        // Used to set up a listView
        ListView targetList;
        // Optional pDialog object
        ProgressDialog pDialog;

        public GetCoolThings(Context context, ListView listView) {
            this.mContext = context;
            this.targetList = listView;
        }

        public GetCoolThings(Context context, ListView listView, ProgressDialog progressDialog) {
            this.mContext = context;
            this.targetList = listView;
            this.pDialog = progressDialog;
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

                        // Get the necessary data from the object
                        String id = jsonObject.getString(TAG_ID);
                        String title = jsonObject.getString(TAG_ID);
                        String body = jsonObject.getString(TAG_BODYTEXT);

                        // Make a new Cool Thing object to hold this data
                        CoolThing coolThing = new CoolThing(id, title, body);

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

            // Apply the data to the listView
            CoolThingsListAdapter listAdapter =
                    new CoolThingsListAdapter(mContext, listCoolThings);
            targetList.setAdapter(listAdapter);
        }
    }
}
