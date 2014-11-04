package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.Fragments.FragmentCoolThing;

/**
 * Created by jawad on 20/10/14.
 *
 * Displays FragmentCoolThings in PagerAdapter
 */
public class CoolThingsPagerAdapter extends FragmentPagerAdapter implements ParseCoolThings.JSONUser {
    // List of fragments to display
    ArrayList<FragmentCoolThing> listOfFragCoolThings;

    // Array of all coolThings
    ArrayList<CoolThing> listOfCoolThings;

    // Contains all the cool things in raw JSON form
    JSONArray jsonArray;

    public CoolThingsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void initAdapter(Context context) {
        // Initialize teh array lists
        listOfFragCoolThings = new ArrayList<FragmentCoolThing>();
        listOfCoolThings = new ArrayList<CoolThing>();

        // Create and display first fragment, with loading animation
        FragmentCoolThing firstFrag = new FragmentCoolThing();
        listOfFragCoolThings.add(firstFrag);

        // Call an Async in the parser to get the JSON and call on this object once its done
        ParseCoolThings parser = new ParseCoolThings();
        parser.getJSON(context, this);
    }

    // Be notified once the JSON data is retrieved
    @Override
    public void gotJSON(JSONArray jsonArray) {
        // Cache the JSON for later use
        this.jsonArray = jsonArray;

        // Create a new cool thing, for easy data retrieval now and later
        CoolThing coolThing = new CoolThing("N/A", "N/A", "N/A");
        try {
            int FIRST_INDEX = 0; // Index of the very first cool thing
            coolThing = ParseCoolThings.JSONToCoolThing(jsonArray.getJSONObject(FIRST_INDEX));
        } catch (JSONException e) {
            Log.e("PagerAdapter", e.getMessage());
            e.printStackTrace();
        }

        // Add the coolThing to current list of cool things
        listOfCoolThings.add(coolThing);

        // Give the data to the fragment
        FragmentCoolThing frag = listOfFragCoolThings.get(0);
        frag.setImageURLAndText(coolThing.getImageURL(), coolThing.getTitle());

        // Let function add additional placeholder fragment and such
        addNextFragment();
    }

    // Add the next placeholder, loading fragment
    private void addNextFragment() {
        // Check!: If added all data, then no need to add another fragment
        if(listOfFragCoolThings.size() >= jsonArray.length()) return;

        // Create a placeholder fragment and add it to the list of fragments
        FragmentCoolThing frag = new FragmentCoolThing();
        listOfFragCoolThings.add(frag);
        // TODO: Add the first image's bg as a placeholder background

        // Create a placeholder cool thing and add it to the list of cool things
        CoolThing coolThing = new CoolThing("N/A", "N/A", "N/A");
        listOfCoolThings.add(coolThing);

        // Notify adapter that the data set has been changed
        notifyDataSetChanged();
    }

    // Set up the placeholder, loading fragment
    private void setUpFrag(int index, FragmentCoolThing frag) {
        // Get the cool thing that represents this fragment and fill it with data
        CoolThing coolThing = listOfCoolThings.get(index);
        try {
            coolThing = ParseCoolThings.JSONToCoolThing(jsonArray.getJSONObject(index));
        } catch (JSONException e) {
            Log.e("PagerAdapter", e.getMessage());
            e.printStackTrace();
        }

        // Apply the data to the frag
        frag.setImageURLAndText(coolThing.getImageURL(), coolThing.getTitle());
    }

    // Return the subTitle of a CoolThing at the given position
    public String getSubTitle(int i) {
        return listOfCoolThings.get(i).getSubTitle();
    }

    // Return the body text of a CoolThing at the given position
    public String getBodyText(int i) {
        return listOfCoolThings.get(i).getBodyText();
    }

    @Override
    public Fragment getItem(int i) {
        // Get the fragment
        FragmentCoolThing frag = listOfFragCoolThings.get(i);

        // If the fragment hasn't been set yet and this isn't the special first fragment's case,
            // then set it up
    //    if(i != 0 && !frag.checkIfSet()) {
        if(i != 0 && !frag.checkIfSet()) {
            // Set up this fragment
            setUpFrag(i, frag);

            // Add the next placeholder fragment
            addNextFragment();
        }

        return frag;
    }

    @Override
    public int getCount() {
        return listOfFragCoolThings.size();
    }
}
