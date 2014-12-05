package edu.umich.engin.cm.onecoolthing.MichEngMag;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 04/12/14.
 */
public class MEMDetailedFrag extends Fragment {
    // All the views that need to be cached and set up
    ImageView mTopImage;
    TextView mTitleText;
    TextView mCategoryText;
    TextView mAuthorText;
    WebView mWebView;

    // Determines whether the data has been set yet or not
    boolean hasDataBeenSet = false;

    // Data to cache
    Bitmap mTopImageBitmap;
    String mTitle;
    String mCategory;
    String mAuthor;
    String mWebHTMLData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mem_detailed, container, false);

        // Cache all the necessary views
        mTopImage = (ImageView) view.findViewById(R.id.topImage);
        mTitleText = (TextView) view.findViewById(R.id.title_main);
        mCategoryText = (TextView) view.findViewById(R.id.title_category);
        mAuthorText = (TextView) view.findViewById(R.id.title_author);
        mWebView = (WebView) view.findViewById(R.id.webview);

        // If the data has been set already, load all the views up
        if(hasDataBeenSet) setUpFrag();

        return view;
    }

    // Finally sets up all the views
    private void setUpFrag() {
        // Set the top image
        mTopImage.setImageBitmap(mTopImageBitmap);

        // Set the title texts
        mTitleText.setText(mTitle);
        mCategoryText.setText(mCategory);
        setAuthorText(mAuthor);

        // Load the HTML data into the webview
        mWebView.loadData(mWebHTMLData, "text/html; charset=UTF-8", null);
    }

    // Sets the author text
    private void setAuthorText(String author) {
        // Concatenate the string to its final format
        String finalText = "By " + author;

        // Display the text
        mAuthorText.setText(finalText);
    }

    // Load in the data
    public void setData(Bitmap bitmap, String title, String category, String author, String htmlData) {
        // Cache all the inputs
        this.mTopImageBitmap = bitmap;
        this.mTitle = title;
        this.mCategory = category;
        this.mAuthor = author;
        this.mWebHTMLData = htmlData;

        // State that the data was set
        hasDataBeenSet = true;

        // If the views have been inflated and set up, set up the fragment
        if(mWebView != null) setUpFrag();
    }
}
