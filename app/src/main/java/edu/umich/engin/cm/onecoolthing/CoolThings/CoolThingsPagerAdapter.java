package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentCoolThing;
import edu.umich.engin.cm.onecoolthing.Fragments.FragmentOneCoolFeed;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.ImageLoaderNoCache;

/**
 * Created by jawad on 20/10/14.
 *
 * Displays FragmentCoolThings in PagerAdapter
 */
public class CoolThingsPagerAdapter extends FragmentPagerAdapter implements ParseCoolThings.JSONUser,
        ImageLoaderNoCache.LoaderManager{
    private static final String TAG = "MD/CoolThingsPagerAdapter";

    // List of fragments to display
    ArrayList<FragmentCoolThing> mListOfFragCoolThings;

    // Array of all coolThings
    ArrayList<CoolThing> mListOfCoolThings;

    // Contains all the cool things in raw JSON form
    JSONArray mJsonArray;

    // Counts how many CoolThings necessary to skip from current index to get next CoolThing
    int skipCounter = 0;

    // Simply save a reference to the frag that calls this pager, to give it the url of its bg
    FragmentOneCoolFeed mFragCaller;
        // TODO: Make an interface... again?

    public CoolThingsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // Intializes the adapter, MUST get
    public void initAdapter(Context context, FragmentOneCoolFeed frag) {
        // Cache the mFragCaller to give it its background url later
        this.mFragCaller = frag;

        // Initialize teh array lists
        mListOfFragCoolThings = new ArrayList<FragmentCoolThing>();
        mListOfCoolThings = new ArrayList<CoolThing>();

        // Create and display first fragment, with loading animation
        FragmentCoolThing firstFrag = new FragmentCoolThing();
        mListOfFragCoolThings.add(firstFrag);

        // Call an Async in the parser to get the JSON and call on this object once its done
        ParseCoolThings parser = new ParseCoolThings();
        parser.getJSON(context, this);
    }

    // Be notified once the JSON data is retrieved
    @Override
    public void gotJSON(JSONArray jsonArray) {
        // Cache the JSON for later use
        this.mJsonArray = jsonArray;

        // Create a new cool thing, for easy data retrieval now and later
        CoolThing coolThing = new CoolThing("N/A", "N/A", "N/A");
        try {
            int FIRST_INDEX = 0; // Index of the very first cool thing
            ParseCoolThings.JSONToCoolThing(jsonArray.getJSONObject(FIRST_INDEX),
                    coolThing);

            // While not including this CoolThing, get the next CoolThing to check
            while(!coolThing.isIncludeInApp()) {
                // Increment the skipCounter
                ++skipCounter;

                // Get the next Cool Thing to check
                ParseCoolThings.JSONToCoolThing(jsonArray.getJSONObject(FIRST_INDEX + skipCounter),
                        coolThing);
            }
        } catch (JSONException e) {
            Log.e("MD/PagerAdapter", e.getMessage());
            e.printStackTrace();
        }

        // Add the coolThing to current list of cool things
        mListOfCoolThings.add(coolThing);

        // Give the data to the fragment
        FragmentCoolThing frag = mListOfFragCoolThings.get(0);
        frag.setData(coolThing.getImageURL(), coolThing.getTitle(), this);

        // Notify the frag to use this coolThing's url for its background
        mFragCaller.setBackground(coolThing.getImageURL());
    }

    // Be notified once a fragment's data has finally loaded
    @Override
    public void notifyDataLoaded() {
        // Simply add the next fragment and notify that the data set has been changed
        addNextFragment();
    }

    // No need for this, just here to fulfill implementation of ImageLoaderNoCache LoaderManager interface
    @Override
    public void notifyRetrievedBitmap(Bitmap bitmap) {

    }

    // Add the next placeholder, loading fragment
    private void addNextFragment() {
        // Check!: If added all data, then no need to add another fragment
        if(mListOfFragCoolThings.size() >= mJsonArray.length()) return;

        // Create a placeholder fragment and add it to the list of fragments
        FragmentCoolThing frag = new FragmentCoolThing();
        mListOfFragCoolThings.add(frag);

        // Create a placeholder cool thing and add it to the list of cool things
        CoolThing coolThing = new CoolThing("N/A", "N/A", "N/A");
        mListOfCoolThings.add(coolThing);

        // Notify adapter that the data set has been changed
        notifyDataSetChanged();
    }

    // Set up the placeholder, loading fragment
    private void setUpFrag(int index) {
        // Get the frag to set up
        FragmentCoolThing frag = mListOfFragCoolThings.get(index);

        // Get the cool thing that represents this fragment and fill it with data
        CoolThing coolThing = mListOfCoolThings.get(index);
        try {
            // Get the first CoolThing to check
            ParseCoolThings.JSONToCoolThing(mJsonArray.getJSONObject(index+skipCounter),
                    coolThing);

            // If not using this Cool Thing, get and check the next one
            while(!coolThing.isIncludeInApp()) {
                // Indicate to skip another CoolThing
                ++skipCounter;

                // Get the next CoolThing to check
                ParseCoolThings.JSONToCoolThing(mJsonArray.getJSONObject(index+skipCounter),
                        coolThing);
            }
        } catch (JSONException e) {
            Log.e("MD/PagerAdapter", e.getMessage());
            e.printStackTrace();
        }

        // Apply the data to the frag
        String url = coolThing.getImageURL();
        String titleText = coolThing.getTitle();

        frag.setData(url, titleText, this);
    }

    // Return the subTitle of a CoolThing at the given position
    public String getSubTitle(int i) {
        return mListOfCoolThings.get(i).getSubTitle();
    }

    // Return the body text of a CoolThing at the given position
    public String getBodyText(int i) {
        return mListOfCoolThings.get(i).getBodyText();
    }

    // Return the palette color of a CoolThing at the given position
    public String getPaletteColor(int i) {return mListOfCoolThings.get(i).getPaletteColor(); }

    @Override
    public Fragment getItem(int i) {
        // Get the fragment
        FragmentCoolThing frag = mListOfFragCoolThings.get(i);

        // If the fragment hasn't been set yet and this isn't the special first fragment's case,
            // then set it up
        if(i != 0 && !frag.checkIfSet()) {
            // Set up this fragment
            setUpFrag(i);
        }

        return frag;
    }

    @Override
    public int getCount() {
        return mListOfFragCoolThings.size();
    }
}
