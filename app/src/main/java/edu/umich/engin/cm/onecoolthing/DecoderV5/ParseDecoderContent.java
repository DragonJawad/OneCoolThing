package edu.umich.engin.cm.onecoolthing.DecoderV5;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ServiceHandler;
import edu.umich.engin.cm.onecoolthing.Util.StorageUtils;

/**
 * Created by jawad on 28/09/15.
 */
public class ParseDecoderContent {
    private static final String LOGTAG = "MD/ParseDecoderContent";

    private static final String URL_BASE = "http://dme.engin.umich.edu/solarCarModels/";
    private static final String URL_MAIN = "index.json";

    private static final String TAG_NAME = "name";
    private static final String TAG_TEXTNAME = "textname";
    private static final String TAG_TEXTURE = "texture";

    DecoderParserUser mUser = null;
    GetDecoderContent mCurrentTask = null;

    public interface DecoderParserUser {
        public void gotDecoderContent();
        public void parseDecoderContentFromWebFailed();
    }

    public void getDecoderContent(Context context, DecoderParserUser callbackUser) {
        if(mCurrentTask != null) {
            throw new RuntimeException("Task already in progress!");
        }

        this.mUser = callbackUser;

        this.mCurrentTask = new GetDecoderContent(context);
        mCurrentTask.execute();
    }

    // Get all the decoder items locally stored
        // Note: Assumes files have actually been stored, and stored correctly
    static public ArrayList<DecoderCarMetadata> getStoredMetadata(Context context) {
        ArrayList<DecoderCarMetadata> results = new ArrayList<DecoderCarMetadata>();

        try {
            // First, get the index json from the stored file that should be there
            String jsonStr = StorageUtils.getStringFromFile(
                    StorageUtils.getAppDataFolder(context),
                    URL_MAIN
            );

            // Then get the JSON object that holds all the data as more objects
            JSONArray baseArray = new JSONArray(jsonStr);
            JSONObject allContentJSON = baseArray.getJSONObject(0); // Only one object

            // Then loop through each subobject in the json and add their data in
            Iterator<String> keyIterator = allContentJSON.keys();
            while(keyIterator.hasNext()) {
                // Get the current JSON obj of data
                String key = keyIterator.next();
                JSONObject curJSON = allContentJSON.getJSONObject(key);

                // Create the new metadata object and fill it up
                DecoderCarMetadata curMetadata = new DecoderCarMetadata();
                curMetadata.name = curJSON.getString(TAG_NAME);
                curMetadata.filepath_texture = curJSON.getString(TAG_TEXTURE);
                curMetadata.filepath_vertex = curJSON.getString(TAG_TEXTNAME);

                // Finally, add the metadata object to the entire list of metadata objects
                results.add(curMetadata);
            }

        } catch (Exception e) {
            Log.e(LOGTAG, "getStoredMetadata() failed");
            Log.i(LOGTAG, e.getMessage());

            return null;
        }

        return results;
    }

    public void cancelAnyTasks() {
        if(mCurrentTask != null) {
            mCurrentTask.cancel(true);
        }
    }

    public boolean doesDecoderDataExist(Context context) {
        // Check if the index file with all the data was ever stored
        File storageDir = StorageUtils.getAppDataFolder(context);
        return StorageUtils.checkIfFileExists(storageDir, URL_MAIN);
    }

    private void asyncSuccessful(ArrayList<DecoderCarMetadata> allDecoderMetadata) {
        // Throw away the current task
        mCurrentTask = null;

        Log.i(LOGTAG, "asyncSuccessful(): Got data for " + allDecoderMetadata.size() + " items");

        // Notify the user of the received data
        if(mUser != null)
            mUser.gotDecoderContent();
    }

    private void asyncFailed() {
        // Throw away the current task
        mCurrentTask = null;

        // Notify the user that the task failed
        if(mUser != null)
            mUser.parseDecoderContentFromWebFailed();
    }

    private class GetDecoderContent extends AsyncTask<Void, Void, Void> {
        Context mContext;
        ArrayList<DecoderCarMetadata> mAllDecoderItems;

        public GetDecoderContent(Context context) {
            this.mContext = context;

            this.mAllDecoderItems = new ArrayList<DecoderCarMetadata>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Clear cache/extra files- never know if saved only some files in a previous try
            StorageUtils.deleteDirectory(StorageUtils.getAppDataFolder(mContext));

            // Create service handler class instance to handle HTTP
            ServiceHandler sh = new ServiceHandler();

            // Make a request to url and get response
            String jsonStr = sh.makeServiceCall(URL_BASE + URL_MAIN, ServiceHandler.GET);

            try {
                // First try to get all the car model files from the json string
                JSONArray baseArray = new JSONArray(jsonStr);
                JSONObject allContentJSON = baseArray.getJSONObject(0); // Only one object

                // Get all the keys from the content JSON
                Iterator<String> keyIterator = allContentJSON.keys();

                // Get the location to store all the files at
                File storageDirectory = StorageUtils.getAppDataFolder(mContext);

                // Parse the index file for each car file
                while(keyIterator.hasNext()) {
                    // Get the current JSON obj of data
                    String key = keyIterator.next();
                    JSONObject curJSON = allContentJSON.getJSONObject(key);

                    // Create a DecoderCarMetadata object to hold the necessary info
                    DecoderCarMetadata curMetadata = new DecoderCarMetadata();
                    // Stuff all the relevant info into the metadata system
                    curMetadata.name = curJSON.getString(TAG_NAME);
                    curMetadata.filepath_texture = curJSON.getString(TAG_TEXTURE);
                    curMetadata.filepath_vertex = curJSON.getString(TAG_TEXTNAME);
                    // Save the reference to the DecoderCarMetadata for later usage
                    mAllDecoderItems.add(curMetadata);

                    // Download and store the texture file straight away if it doesn't exist already
                    if(!StorageUtils.checkIfFileExists(storageDirectory, curMetadata.filepath_texture)) {
                        StorageUtils.storeFileFromWeb(
                                storageDirectory,
                                curMetadata.filepath_texture,
                                URL_BASE + curMetadata.filepath_texture
                        );
                    }

                    // Download and store the vertex text file straight away, if it doesn't exist already
                    if(!StorageUtils.checkIfFileExists(storageDirectory, curMetadata.filepath_vertex)) {
                        StorageUtils.storeFileFromWeb(
                                storageDirectory,
                                curMetadata.filepath_vertex,
                                URL_BASE + curMetadata.filepath_vertex
                        );
                    }
                }

                // Finally, store the index file for later usage
                StorageUtils.storeFileFromString(
                        storageDirectory,
                        URL_MAIN,
                        jsonStr
                );
            }
            catch(Exception e) {
                Log.e("MD/JSONParser", e.getMessage());
                e.printStackTrace();

                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            // Clear any files tried to download thus far, if any
                // TODO: Should probs store files in a subdirectory of the main app data folder
                //          and probs shouldn't be done on the main thread
            StorageUtils.deleteDirectory(StorageUtils.getAppDataFolder(mContext));

            asyncFailed();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Return all the metadata for all the files
            asyncSuccessful(mAllDecoderItems);
        }
    }
}
