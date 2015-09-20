package edu.umich.engin.cm.onecoolthing.CoolFeeds;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.CheckNetworkConnection;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.ServiceHandler;

/**
 * Created by jawad on 18/10/14.
 *
 * Handles getting all Cool Thing objects and setting appropriate data
 */
public class ParseCoolThings {
    // JSON Tags
    private static final String TAG_ID = "ID";
    private static final String TAG_INCLUDEINAPP = "Include in App";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_SUBTITLE = "Sub Title";
    private static final String TAG_BODYTEXT = "Body Text";
    private static final String TAG_PALETTECOLOR = "Color Palette";
    private static final String TAG_IMAGEURL = "iPhone 5 Retina Image";
    private static final String TAG_FULLITEMURL = "Full Item URL";
    private static final String TAG_TWITTERTEXT = "Tweet Language";

    /**
     * Returns the jsonObject in CoolThingData format
     * @param jsonObject - Contains the Cool Thing with the appropriate tags in JSON format
     */
    static private CoolThingData JSONToCoolThing(JSONObject jsonObject) throws JSONException {
        CoolThingData coolThing = new CoolThingData();

        // Get the necessary data from the object
        String id = jsonObject.getString(TAG_ID);

        String title = jsonObject.getString(TAG_TITLE);
        String subTitle = jsonObject.getString(TAG_SUBTITLE);
        String body = jsonObject.getString(TAG_BODYTEXT);

        String paletteColor = jsonObject.getString(TAG_PALETTECOLOR);
        String imageURL = jsonObject.getString(TAG_IMAGEURL);

        String fullItemURL = jsonObject.getString(TAG_FULLITEMURL);
        String tweetText = jsonObject.getString(TAG_TWITTERTEXT);

        // Add the data to the Cool Thing object to hold this data
        coolThing.setId(id);

        coolThing.setTitle(title);
        coolThing.setBodyText(body);

        coolThing.setPaletteColor(paletteColor);
        coolThing.setImageURL(imageURL);
        coolThing.setSubTitle(subTitle);

        coolThing.setFullItemURL(fullItemURL);
        coolThing.setTweetText(tweetText);

        // Finally, return the fully set up cool thing data object
        return coolThing;
    }

    /**
     * Returns whether or not the Cool Thing represented by the parameter should be in the app
     * @param jsonObject Assumed to be a properly formatted Cool Thing JSON object
     * @return True if the Cool Thing should be kept in the app, false otherwise
     */
    static private boolean shouldIncludeInApp(JSONObject jsonObject) {
        try{
            // Only determined currently by the parameter in the json itself
            return jsonObject.getBoolean(TAG_INCLUDEINAPP);
        }
        catch (Exception e) {
            // Log the error
            Log.e("MD/JSONParser", e.getMessage());
            e.printStackTrace();

            // Return false as the json object isn't apparently valid (most likely)
            return false;
        }

    }

    // Interface any interactables need in order to get notified of parsed Cool Things
    public interface CoolJSONUser {
        public void gotCoolThings(ArrayList<CoolThingData> allCoolThings);
    };


    /**
     * Gets JSON string for Cool Things and gives to jsonUser once done
     * @param context
     * @param coolJsonUser - Necessary to communicate finished json string back
     */
    public void getCoolThings(Context context, CoolJSONUser coolJsonUser, String sourceUrl) {
        // Create a new Async to get the JSON data then notify the jsonUser
        GetCoolJSON jsonAsync = new GetCoolJSON(context, coolJsonUser, sourceUrl);
        jsonAsync.execute();
    }

    /**
     * Async class to get String representation of JSON
     * which then passes it to the jsonUser
     */
    private class GetCoolJSON extends AsyncTask<Void, Void, Void> {
        Context mContext;
        CoolJSONUser coolJsonUser; // Necessary for telling client of finished J
        ArrayList<CoolThingData> mAllCoolThings = new ArrayList<CoolThingData>();
        String sourceUrl;

        public GetCoolJSON(Context context, CoolJSONUser coolJsonUser, String sourceUrl) {
            this.mContext = context;
            this.coolJsonUser = coolJsonUser;
            this.sourceUrl = sourceUrl;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create service handler class instance to handle HTTP
            ServiceHandler sh = new ServiceHandler();

            // Make a request to url and get response
            String jsonStr = sh.makeServiceCall(sourceUrl, ServiceHandler.GET);

            // Change the jsonStr into a mJsonArray- save a simple step
            try {
                // Try to create a JSON array from the url- should work
                JSONArray jsonArray = new JSONArray(jsonStr);

                // Loop through all the JSON objects and try to get all the cool things to include
                for(int i = 0; i < jsonArray.length(); ++i) {
                    // Get the wonderful json object under inspection right now
                    JSONObject curJSON = jsonArray.getJSONObject(i);

                    // Only continue parsing the JSON if it should be included in the app
                    if(shouldIncludeInApp(curJSON)) {
                        // Simply add the parsed CoolThing data object to the array of cool things
                        mAllCoolThings.add(JSONToCoolThing(curJSON));
                    }
                }

            } catch (JSONException e) {
                Log.e("MD/JSONParser", e.getMessage());
                e.printStackTrace();
                cancel(true);
            }
            catch (NullPointerException e) {
                Log.e("MD/JSONParser", "NullPointerExpection found while trying to get jsonArray");
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            // Show dialog to state there were issues accessing the Internet
            CheckNetworkConnection.showNoConnectionDialog(mContext);

            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            // Notify the jsonUser of the JSON data
            coolJsonUser.gotCoolThings(mAllCoolThings);
        }
    }
}
