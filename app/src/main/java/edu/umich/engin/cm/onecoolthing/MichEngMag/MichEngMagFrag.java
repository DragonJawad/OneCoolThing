package edu.umich.engin.cm.onecoolthing.MichEngMag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.umich.engin.cm.onecoolthing.Core.AnalyticsHelper;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 20/11/14.
 */
public class MichEngMagFrag extends android.support.v4.app.Fragment {
    ListView mListView;
    MichEngMagListAdapter mAdapter;
    MichEngMagListAdapter.MagazineViewer magazineViewer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_michengmag, container, false);

        // Cache the listView to set it up later
        mListView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Send data that the MichEngMag has now been opened
        ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.MICHENG);

        // Now start the initial setup process
        initSetup();
    }

    public void setMagazineViewer(MichEngMagListAdapter.MagazineViewer magazineViewer) {
        this.magazineViewer = magazineViewer;

        // If the adapter has already been set, then give it the magazineViewer now
        if(mAdapter != null) mAdapter.setMagazineViewer(magazineViewer);
    }

    private void initSetup() {
        // Set up the adapter for the listView
        mAdapter = new MichEngMagListAdapter(getActivity());

        // If the magazineViewer is not null, then set it now
        if(magazineViewer != null) mAdapter.setMagazineViewer(magazineViewer);

        // Set the adapter as the listView's adapter
        mListView.setAdapter(mAdapter);
    }
}
