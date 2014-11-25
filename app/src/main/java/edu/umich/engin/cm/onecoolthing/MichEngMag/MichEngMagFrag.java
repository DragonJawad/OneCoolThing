package edu.umich.engin.cm.onecoolthing.MichEngMag;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 20/11/14.
 */
public class MichEngMagFrag extends Fragment implements ParseMichEngMag.MagSubscriber {
    // Holds all of the individual story items
    ArrayList<MichEngMag> mMagazineList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_michengmag, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Now start the initial setup process
        initSetup();
    }

    private void initSetup() {
        // Use the parser to asynchronously get the data
        ParseMichEngMag parser = new ParseMichEngMag();
        parser.getData(getActivity(), this);
    }

    @Override
    public void gotMagazine(ArrayList<MichEngMag> magazineList) {
        // Cache the data
        this.mMagazineList = magazineList;

        // Display the data
    }
}
