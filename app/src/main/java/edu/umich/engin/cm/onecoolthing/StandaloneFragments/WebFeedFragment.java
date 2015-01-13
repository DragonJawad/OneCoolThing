package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.umich.engin.cm.onecoolthing.Core.AnalyticsHelper;
import edu.umich.engin.cm.onecoolthing.NetworkUtils.CheckNetworkConnection;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 06/11/14.
 *
 * A Fragment that shows a feed via a WebView
 */
public class WebFeedFragment extends android.support.v4.app.Fragment {
    private String mUrl; // URL of this feed
    private String mTitle; // Title of this feed

    static private final String KEY_URL = "KEY_URL";
    static private final String KEY_TITLE = "KEY_TITLE";

    private WebView mWebView;

    // States whether or not the activity has been created/attached already
    private boolean activityCreated = false; // TODO: Determine if necessary or not

    // Creates a new instance- note, non-default constructors should not be used with a fragment
    public static WebFeedFragment newInstance(String url, String title) {
        // Instantiate the fragment
        WebFeedFragment frag = new WebFeedFragment();

        // Create a new bundle that the fragment will later access
        Bundle bundle = new Bundle(2);

        // Insert the values into the bundle
        bundle.putString(KEY_URL, url);
        bundle.putString(KEY_TITLE, title);

        // Give the bundle to the fragment, so it can access it later
        frag.setArguments(bundle);

        // Return the fragment
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webfeed, container, false);

        // Get the mUrl and mTitle from the arguments
        mUrl = getArguments().getString(KEY_URL);
        mTitle = getArguments().getString(KEY_TITLE);

        // Get and cache the webview from the layout
        mWebView = (WebView) view.findViewById(R.id.webview);

        // If Activity has already been attached, initialize the webview
        if(activityCreated) initWebView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up the webview, if the views have already been cached
        if(mWebView != null) initWebView();
    }

    private void initWebView() {
        // Send some data that the web view was opened
        ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.GENWEBVIEW, mUrl);

        // If the internet can be connected to, load the WebView
        if(CheckNetworkConnection.isConnectionAvailable(getActivity())) {
            // First, enable JavaScript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            // TODO: Should javascript really be enabled? XSS vulnerabilities, potentialy!

            // Force links and redirects to open in the WebView instead of in a browser
            TumblrWebViewClient webViewClient = new TumblrWebViewClient();
            mWebView.setWebViewClient(webViewClient);

            // Finally, load the URL
            mWebView.loadUrl(mUrl);
        }
        // Otherwise, show a dialog that failed to connect to the internet
        else {
            CheckNetworkConnection.showNoConnectionDialog(getActivity());
        }
    }

    /**
     * WebViewClient that handles URL redirecting [currently]
     */
    private class TumblrWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Default behavior
        //    return super.shouldOverrideUrlLoading(view, url);

            // For now, simply return false- ie, ALL links will open in the webView
                // TODO: Fix this! Only open what links?
                    // Help for later: https://developer.chrome.com/multidevice/webview/gettingstarted
            return false;
        }
    }
}
