package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ImageLoaderNoCache;

/**
 * Created by jawad on 20/10/14.
 *
 * Displays FragmentCoolThings in PagerAdapter
 */
public class CoolThingsPagerAdapter extends FragmentPagerAdapter implements ParseCoolThings.CoolJSONUser,
        ImageLoaderNoCache.LoaderManager{
    private static final String TAG = "MD/CoolThingsPagerAdapter";

    // List of fragments to display
    ArrayList<CoolThingFrag> mListOfFragCoolThings;

    // Array of all coolThings
    ArrayList<CoolThingData> mListOfCoolThings;

    // Counts how many CoolThings necessary to skip from current index to get next CoolThing
    int skipCounter = 0;

    // Simply save a reference to the frag that calls this pager, to give it the url of its bg
    OneCoolFeedFrag mFragCaller;

    public CoolThingsPagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    // Intializes the adapter, MUST get
    public void initAdapter(Context context, OneCoolFeedFrag frag) {
        // Cache the mFragCaller to give it its background url later
        this.mFragCaller = frag;

        // Initialize teh array lists
        mListOfFragCoolThings = new ArrayList<CoolThingFrag>();
        mListOfCoolThings = new ArrayList<CoolThingData>();

        // Create and display first fragment, with loading animation
        CoolThingFrag firstFrag = new CoolThingFrag();
        mListOfFragCoolThings.add(firstFrag);

        // Call an Async in the parser to get the JSON and call on this object once its done
        ParseCoolThings parser = new ParseCoolThings();
        parser.getCoolThings(context, this);
    }

    // Be notified once the JSON data is retrieved
    @Override
    public void gotCoolThings(ArrayList<CoolThingData> allCoolThings) {
        // Save a reference to all the cool things locally
        mListOfCoolThings = allCoolThings;

        // Grab the first cool thing data so it can be displayed
        CoolThingData firstCoolThingData = mListOfCoolThings.get(0);

        // Give the data to the first fragment that's been waiting
        CoolThingFrag frag = mListOfFragCoolThings.get(0);
        frag.setData(firstCoolThingData.getImageURL(), firstCoolThingData.getTitle(), 1,
            mListOfCoolThings.size(), this);

        // Notify the frag to use this coolThing's url for its background
        mFragCaller.setBackground(firstCoolThingData.getImageURL());

/*        // Create a new cool thing, for easy data retrieval now and later
        CoolThingData coolThing = new CoolThingData("N/A", "N/A", "N/A");
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
        CoolThingFrag frag = mListOfFragCoolThings.get(0);
        frag.setData(coolThing.getImageURL(), coolThing.getTitle(), 1, mListOfCoolThings.size(), this);

        // Notify the frag to use this coolThing's url for its background
        mFragCaller.setBackground(coolThing.getImageURL());*/
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
        if(mListOfFragCoolThings.size() >= mListOfCoolThings.size()) return;

        // Create a placeholder fragment and add it to the list of fragments
        CoolThingFrag frag = new CoolThingFrag();
        mListOfFragCoolThings.add(frag);

        // Notify adapter that the data set has been changed
        notifyDataSetChanged();
    }

    // Set up the placeholder, loading fragment
    private void setUpFrag(int index) {
        // Get the frag to set up
        CoolThingFrag frag = mListOfFragCoolThings.get(index);

        // Get the cool thing that has all the data
        CoolThingData coolThing = mListOfCoolThings.get(index);

        // Apply the data to the frag
        frag.setData(coolThing.getImageURL(), coolThing.getTitle(), index+1,
            mListOfCoolThings.size(), this);
    }

    // Return the title of a CoolThing at the given position
    public String getTitle(int i) {
        // If the length of the list of cool things is allowable, then try to get the title
        if(mListOfCoolThings.size()-1 >= i)
            return mListOfCoolThings.get(i).getTitle();
        else
            return null;
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

    // Return the fullItemURL of a CoolThing at the given position
    public String getFullItemURL(int i) {
        return mListOfCoolThings.get(i).getFullItemURL();
    }

    // Returns the tweetText of a CoolThng at the given position
    public String getTweetText(int i) { return mListOfCoolThings.get(i).getTweetText(); }

    @Override
    public android.support.v4.app.Fragment getItem(int i) {
        // Get the fragment
        CoolThingFrag frag = mListOfFragCoolThings.get(i);

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
