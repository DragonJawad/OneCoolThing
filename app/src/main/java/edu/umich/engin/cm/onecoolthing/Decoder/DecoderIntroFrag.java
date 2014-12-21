package edu.umich.engin.cm.onecoolthing.Decoder;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 21/12/14.
 */
public class DecoderIntroFrag extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decoder_intro, container, false);

        return view;
    }
}
