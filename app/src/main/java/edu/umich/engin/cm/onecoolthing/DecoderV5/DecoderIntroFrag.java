package edu.umich.engin.cm.onecoolthing.DecoderV5;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import edu.umich.engin.cm.onecoolthing.Core.AnalyticsHelper;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 27/09/15.
 */
public class DecoderIntroFrag extends android.support.v4.app.Fragment
        implements View.OnClickListener, ParseDecoderContent.DecoderParserUser {
    private static final String LOGTAG = "MD/DecoderIntroFrag";

    // The button that, once clicked, starts the actual Decoder
    Button startButton;
    // The indeterminate progress bar used when the user tries to download the Decoder content
    ProgressBar mProgressBar;

    private ParseDecoderContent mContentParser;
    private boolean mGotDecoderData;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decoder_intro, container, false);

        // Get and set up the button to launch the Decoder
        startButton = (Button) view.findViewById(R.id.button_start);

        // Get and hide the indeterminate progress bar
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_horizontal);
        mProgressBar.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Send some data that the Decoder Intro has been opened
        ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.ARVIEW);

        // Initialize the parser and check if the decoder content has already been stored from the internet
        mContentParser = new ParseDecoderContent();
        mGotDecoderData = mContentParser.doesDecoderDataExist(getActivity());

        // Now set up the button
        startButton.setOnClickListener(this);
        // If already have the data, not much to do- display the button with the start decoder text
        if(mGotDecoderData) {
            startButton.setText(getActivity().getResources().getString(R.string.decoder_intro_btn_starttext));
        }
        // Otherwise, let the user know they need to download content first
        else {
            startButton.setText(getActivity().getResources().getString(R.string.decoder_intro_btn_downloadtext));
        }
    }

    @Override
    public void onClick(View view) {
        // If already have all the Decoder data, then the user can start the Decoder for real
        if(mGotDecoderData) {
            // Launch the Decoder Activity
            Intent intent = new Intent(getActivity(), ActivityDecoder.class);
            startActivity(intent);
        }
        // Otherwise, time to start downloading all the data
        else {
            // Show the indeterminate progress bar to please the user a little
            mProgressBar.setVisibility(View.VISIBLE);

            // Disable the button so the user doesn't accidentally start multiple download instances
            startButton.setEnabled(false);

            // Start the download Decoder content process
            mContentParser.getDecoderContent(getActivity(), this);
        }
    }

    @Override
    public void gotDecoderContent() {
        // Let the user know that the app successfully got the data
        Toast.makeText(
                getActivity(),
                getResources().getString(R.string.decoder_download_success),
                Toast.LENGTH_LONG
        ).show();

        // Enable the start button and change what it says
        startButton.setText(getResources().getString(R.string.decoder_intro_btn_starttext));
        startButton.setEnabled(false);

        // Hide the progress bar now
        mProgressBar.setVisibility(View.GONE);

        // Remember that the decoder content is now stored completely
        mGotDecoderData = true;
    }

    @Override
    public void parseDecoderContentFromWebFailed() {
        Log.d(LOGTAG, "Failed to get decoder content");

        // Re-enable the download content button and stop the progress bar
        startButton.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);

        // Let the user know that the app failed to get the data, for one reason or another
        Toast.makeText(
                getActivity(),
                getResources().getString(R.string.decoder_download_failed),
                Toast.LENGTH_LONG
        ).show();
    }
}