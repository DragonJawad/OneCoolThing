package edu.umich.engin.cm.onecoolthing.Decoder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 21/12/14.
 */
public class DecoderIntroFrag extends android.support.v4.app.Fragment implements View.OnClickListener {
    // The button that, once clicked, starts the actual Decoder
    Button startButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decoder_intro, container, false);

        // Get and set up the button to launch the Decoder
        startButton = (Button) view.findViewById(R.id.button_start);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Now set up the button
        startButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Launch the Decoder Activity
        Intent intent = new Intent(getActivity(), DecoderActivity.class);
        startActivity(intent);
    }
}
