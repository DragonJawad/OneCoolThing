package edu.umich.engin.cm.onecoolthing.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 20/10/14.
 *
 * Displays a single "CoolThing" object, with only title and background
 */
public class FragmentCoolThing extends Fragment {
    // View elements
    private ImageView background;
    private TextView titleView;

    private String titleText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cool_things, container, false);

        // Get the view elements
        background = (ImageView) view.findViewById(R.id.background);
        titleView = (TextView) view.findViewById(R.id.title);

        if(titleText != null) titleView.setText(titleText);

        return view;
    }

    public void setBackgroundImage(Bitmap bg) {
        background.setImageBitmap(bg);
    }

    public void setTitleText(String title) {
        this.titleText = title;
    }

    public void setTitleTextNow(String title) {
        this.titleText = title;
        titleView.setText(titleText);
    }
}
