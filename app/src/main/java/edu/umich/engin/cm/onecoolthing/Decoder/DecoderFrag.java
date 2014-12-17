package edu.umich.engin.cm.onecoolthing.Decoder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.vuforia.State;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 16/12/14.
 */
public class DecoderFrag extends Fragment implements DecoderApplicationControl {
    private static final String LOG = "MD/DecoderActivity";

    // Decoder session that handles all the gritty work
    DecoderApplicationSession mDecoderSession;

    // Holds all the UI elements
    private RelativeLayout mUIContainer;
    LoadingDialogHandler loadingDialogHandler; // Handles the loading dialog

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decoder, container, false);

        // Cache the necessary views to setup later
        mUIContainer = (RelativeLayout) view.findViewById(R.id.camera_overlay_layout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create a loading animation while the vuforia loads
        startLoadingAnimation();
    }

    // Initializes the loading animation
    private void startLoadingAnimation()
    {
        mUIContainer.setVisibility(View.VISIBLE);
        mUIContainer.setBackgroundColor(Color.BLACK);

        loadingDialogHandler = new LoadingDialogHandler(getActivity());

        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUIContainer
                .findViewById(R.id.loading_indicator);

        // Shows the loading indicator at start
        loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
    }

    @Override
    public boolean doInitTrackers() {
        return false;
    }

    @Override
    public boolean doLoadTrackersData() {
        return false;
    }

    @Override
    public boolean doStartTrackers() {
        return false;
    }

    @Override
    public boolean doStopTrackers() {
        return false;
    }

    @Override
    public boolean doUnloadTrackersData() {
        return false;
    }

    @Override
    public boolean doDeinitTrackers() {
        return false;
    }

    @Override
    public void onInitARDone(DecoderApplicationException e) {

    }

    @Override
    public void onQCARUpdate(State state) {

    }
}
