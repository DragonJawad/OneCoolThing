package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentVerticalPager;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.ServiceHandler;

/**
 * Created by jawad on 23/10/14.
 *
 * Reoptimized JSON parser
 * -> Keeps track of oldJSON using SharedPrefs
 * -> Con: Activity must keep a running instance of this, else data is lose
 * -> Con: Only sets up data first time VertPager needs to get set
 *
 * TODO: Implement with no internet connection, using old JSON and maybe cache 5 images?
 */
public class ParseCoolThingsNew {

    private static final String URL = "http://www.engin.umich.edu/college/about/news/news/coolthingindexjson";
    private static final String LOGTAG = "MD/ParseCoolThings";

    // JSON Tags
    private static final String TAG_ID = "ID";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_SUBTITLE = "Sub Title";
    private static final String TAG_BODYTEXT = "Body Text";
    private static final String TAG_IMAGEURL = "iPhone 5 Retina Image";

    // List of coolThings for everyone to use
    ArrayList<CoolThing> listCoolThings = new ArrayList<CoolThing>();

    // Previous JSON string, from the last synch check
    private static final String PREF_FILE = "Preferences";
    private static final String KEY_OLDJSON = "OldJSON";
    String oldJSON;

    // Get old JSON, starts an Async
    private void getOldJSON(Context context) {
        // If the oldJSON is null, then have to go fetch it
        if(oldJSON == null) {
            // Get the SharedPreferences object from the appropriate file
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE,
                    Context.MODE_PRIVATE);
            // Get the previously saved JSON string, if it exists
            oldJSON = sharedPreferences.getString(KEY_OLDJSON, "");

            if(oldJSON == "") Log.d("Parser", "oldJSON was null");
            else Log.d(LOGTAG, "oldJSON was NOT null");
        }

        // If old JSON is still null, then simply get new JSON

        // Otherwise, compare oldJSON to new JSON
    }

    // Save the new JSON string
        // For optimization reasons, oldJSON must already have been changed
    private void saveOldJSON(Context context) {
        // If OldJSON is null, issues afoot
        if(oldJSON == null) Log.e("Parser", "OldJSON was null while trying to save it!");

        // Get the SharedPreferences object from the appropriate file
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE,
                Context.MODE_PRIVATE);
        // Use the editor and write to the preferences file
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_OLDJSON, oldJSON);

        // Commit the edits!
        editor.apply();
    }

    // Function to call to set up a VerticalPager, that sets everything else up
    public void setUpVertPager(Context context, FragmentVerticalPager vertPager) {

    }

    // Finally passes in the coolThings and context to the vertical pager
    private void vertPagerFinalInit(Context context, FragmentVerticalPager vertPager) {
        vertPager.initCoolViewPager(context, listCoolThings);
    }

    // Async: Get new JSON
        // If old JSON != null, compare the two
        // If old JSON == null, simply use this to fill in the data
    /**
     * Async class to get Cool Things
     *
     * TODO: Handle a loading icon
     */
    private class GetCoolThings extends AsyncTask<Void, Void, Void> {
        Context mContext;

        // The VerticalPager to set up
        FragmentVerticalPager verticalPager;

        // Constructor
            // Note: So far, implemented only to set up a VerticalPager
        public GetCoolThings(Context context, FragmentVerticalPager frag) {
            this.mContext = context;
            this.verticalPager = frag;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create service handler class instance to handle HTTP
            ServiceHandler sh = new ServiceHandler();

            // Make a request to url and get response
            String jsonStr = sh.makeServiceCall(URL, ServiceHandler.GET);

         //   Log.d("MD/JSONResponse: ", "> " +

            if(jsonStr == null) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            else {
                // Get the overarching JSON array
                try {
                    // Get the overarching JSON array
                    JSONArray jsonData = new JSONArray(jsonStr);

                    // Loop through each object and create a CoolThing
                    for(int i = 0; i < jsonData.length(); ++i) {
                        // Get the current data object
                        JSONObject jsonObject = jsonData.getJSONObject(i);

                        // Get the id first, for quick checking
                        String id = jsonObject.getString(TAG_ID);

                        // If this is the first index, check if oldJSON is old
                        if(i == 0) {
                            // If oldJSON was null, no need for more checking- reassign it
                            if(oldJSON == null) {
                                oldJSON = jsonStr;
                                // Save the JSON
                                saveOldJSON(mContext);
                            }
                            // Otherwise have to check the first object's ID
                            else {
                            }
                        }

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

            return null;
        }
    }
}
