package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG_INCLUDEINAPP = "Include in App";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_SUBTITLE = "Sub Title";
    private static final String TAG_BODYTEXT = "Body Text";
    private static final String TAG_PALETTECOLOR = "Color Palette";
    private static final String TAG_IMAGEURL = "iPhone 5 Retina Image";

    /**
     * Returns the jsonObject in CoolThing format
     * @param jsonObject - Contains the Cool Thing with the appropriate tags in JSON format
     * @param coolThing - CoolThing who's data is to be modified
     */
    static public void JSONToCoolThing(JSONObject jsonObject, CoolThing coolThing) throws JSONException {

        // Get the necessary data from the object
        String id = jsonObject.getString(TAG_ID);
        boolean includeInApp = jsonObject.getBoolean(TAG_INCLUDEINAPP);

        String title = jsonObject.getString(TAG_TITLE);
        String subTitle = jsonObject.getString(TAG_SUBTITLE);
        String body = jsonObject.getString(TAG_BODYTEXT);

        String paletteColor = jsonObject.getString(TAG_PALETTECOLOR);
        String imageURL = jsonObject.getString(TAG_IMAGEURL);

        // Add the data to the Cool Thing object to hold this data
        coolThing.setId(id);
        coolThing.setIncludeInApp(includeInApp);

        coolThing.setTitle(title);
        coolThing.setBodyText(body);

        coolThing.setPaletteColor(paletteColor);
        coolThing.setImageURL(imageURL);
        coolThing.setSubTitle(subTitle);
    }

    // Interface any interactables need in order to get notified of finished JSON array
    public interface JSONUser {
        public void gotJSON(JSONArray jsonArray);
    };


    /**
     * Gets JSON string for Cool Things and gives to jsonUser once done
     * @param context
     * @param jsonUser - Necessary to communicate finished json string back
     */
    public void getJSON(Context context, JSONUser jsonUser) {
        // Create a new Async to get the JSON data then notify the jsonUser
        GetCoolJSON jsonAsync = new GetCoolJSON(context, jsonUser);
        jsonAsync.execute();
    }

    /**
     * Async class to get String representation of JSON
     * which then passes it to the jsonUser
     */
    private class GetCoolJSON extends AsyncTask<Void, Void, Void> {
        Context mContext;
        JSONUser jsonUser; // Necessary for telling client of finished JSON string
        JSONArray jsonArray; // The final product, that contains all of the JSON

        public GetCoolJSON(Context context, JSONUser jsonUser) {
            this.mContext = context;
            this.jsonUser = jsonUser;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create service handler class instance to handle HTTP
            ServiceHandler sh = new ServiceHandler();

            // Make a request to url and get response
            String jsonStr = sh.makeServiceCall(URL, ServiceHandler.GET);

            // Change the jsonStr into a mJsonArray- save a simple step
            try {
                jsonArray = new JSONArray(jsonStr);
            } catch (JSONException e) {
                Toast.makeText(mContext, "Failed to get data from internet", Toast.LENGTH_LONG)
                        .show();
                Log.e("MD/JSONParser", e.getMessage());
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Notify the jsonUser of the JSON data
            jsonUser.gotJSON(jsonArray);
        }
    }
}
