package edu.umich.engin.cm.onecoolthing.MichEngMag;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ServiceHandler;

/**
 * Created by jawad on 18/11/14.
 *
 * Handles parsing the JSON for the Michigan Engineer Magazine
 */
public class ParseMichEngMag {
    private static final String TAG = "MD/ParseMichEngMag";

    // Exact URL to get the JSON from
    private static final String URL = "http://engcomm.engin.umich.edu/magjson.php";

    // JSON Tags
    private static final String TAG_STORIES = "Stories";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_SHORTTITLE = "Short Title";
    private static final String TAG_LEVEL = "Level";
    private static final String TAG_URL = "URL";
    private static final String TAG_IMAGEURL = "Magazine Image";

    // Interface for anyone who wishes to actually receive the data
    public interface MagParserSubscriber {
        public void gotMagazine(ArrayList<MichEngMagItem> magazineList);
    }

    // Gets the magazine from the webs and gives it to the subscriber
    public void getData(Context context, MagParserSubscriber subscriber) {
        // Create and run an Async to do all the work
        GetMagazine task = new GetMagazine(context, subscriber);
        task.execute();
    }

    /**
     * Async class to get String representation of JSON
     * which then passes it to the jsonUser
     */
    private class GetMagazine extends AsyncTask<Void, Void, Void> {
        Context mContext;

        // Subscriber to be notified once data has been accumulated
        MagParserSubscriber mSubscriber;

        // Holds all the magazine items
        ArrayList<MichEngMagItem> magazineList;

        public GetMagazine(Context context, MagParserSubscriber subscriber) {
            this.mContext = context;
            this.mSubscriber = subscriber;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create service handler class instance to handle HTTP
            ServiceHandler sh = new ServiceHandler();

            // Make a request to url and get response
            String jsonStr = sh.makeServiceCall(URL, ServiceHandler.GET);

            // Change the jsonStr into a mJsonArray- save a simple step
            try {
                // Formatted a bit odd, get the single overarching jsonObject first
                    // Technically contains data of the edition of the magazine, but no need yet
                JSONObject jsonObject = new JSONObject(jsonStr);

                // Then get the subArray of all the magazine items
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_STORIES);
                int sizeOfArray = jsonArray.length();

                // Create a new list of the jsonArray size
                magazineList = new ArrayList<MichEngMagItem>(sizeOfArray);

                // Loop through the entire array and convert to magazine items
                for(int i = 0; i < sizeOfArray; ++i) {
                    // Create the object at this point
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    // First, get the data for readability
                    String title = jsonObj.getString(TAG_TITLE);
                    String shortTitle = jsonObj.getString(TAG_SHORTTITLE);
                    String url = jsonObj.getString(TAG_URL);
                    String imageUrl = jsonObj.getString(TAG_IMAGEURL);
                    int level = jsonObj.getInt(TAG_LEVEL);

                    // Create a new magazine item and fill it with the data
                    MichEngMagItem magazineItem = new MichEngMagItem();
                    magazineItem.setTitle(title);
                    magazineItem.setShortTitle(shortTitle);
                    magazineItem.setUrl(url);
                    magazineItem.setImageUrl(imageUrl);
                    magazineItem.setLevel(level);

                    // Add the magazine item to the magazine
                    magazineList.add(magazineItem);
                }
            } catch (JSONException e) {
                Toast.makeText(mContext, "Failed to get data from internet", Toast.LENGTH_LONG)
                        .show();
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Notify the subscriber of the data
            mSubscriber.gotMagazine(magazineList);
        }
    }
}
