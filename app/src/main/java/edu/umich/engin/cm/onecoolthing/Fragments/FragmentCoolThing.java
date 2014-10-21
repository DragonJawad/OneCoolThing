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

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ImageLoader;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 20/10/14.
 *
 * Displays a single "CoolThing" object, with only title and background
 * TODO: Make setting the background and title farrrr more efficient
 */
public class FragmentCoolThing extends Fragment {
    // View elements
    private ImageView background;
    private TextView titleView;

    private String titleText;
    private String imageURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cool_things, container, false);

        // Get the view elements
        background = (ImageView) view.findViewById(R.id.background);
        titleView = (TextView) view.findViewById(R.id.title);

        // Assigning this now as, according to past usage, title and URL are already ready by now
            // Warning: Error prone, find alternative solution
        if(titleText != null) titleView.setText(titleText);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(imageURL != null) {
            // Trying to set the background image lazily
            ImageLoader imageLoader = new ImageLoader(getActivity());
            imageLoader.DisplayImage(imageURL, background, 1);

            // Force the imageView to stretch across the screen
            background.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public void setBackgroundImage(Bitmap bg) {
        if(background == null) Log.d("MD/CoolFrag", "BG is NULL! =<");
        background.setImageBitmap(bg);
    }

    public void setBackgroundURL(String url) {
        this.imageURL = url;
    }

    public void setTitleText(String title) {
        this.titleText = title;
    }
}
