package edu.umich.engin.cm.onecoolthing.Core;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 05/01/15.
 */
public class AnalyticsHelper extends Application {
    // Enums for quickly and easily sending the correct screen names with trackers
    public enum TrackerScreen {
        ABOUT,      // The About page
        ARVIEW,     // The page that explains the Decoder (the Decoder Intro)
        ARWEB,      // The page that opens after Decoder find something (aka, send this BEFORE opening link)
        CAMVIEW,    // Decoder itself- I didn't make this naming convention, alright? It's from iOS
        DETAILVIEW, // The right sliding menu. Title of the story should be sent alongside this
        GENWEBVIEW, // The general webviews (Tumblr blogs, etc). Url should be sent alongside this
        MAGSTORY,   // The detailed MichEngMag view- concatenate with the story title
        MAINVIEW,   // The main, center scrolling OneCoolFeed view
        MICHENG,    // The MichEngMag view
        MODALWEB,   // Same as GenWebView actually- just send the url alongside this
        PRIVACYPOL  // The Privacy Policy view
    }

    // The currently only used tracker to send Google Analytics data
    Tracker mTracker = null;

    // Get the primary tracker- try to only instantiate one!
    synchronized Tracker getTracker() {
        // If there is no tracker, create it
        if (mTracker == null) {
            mTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.app_tracker);
        }

        return mTracker;
    }

    // Easily send a tracker screenView using the simple TrackerScreen types- should NOT need additional screen data
    synchronized void sendScreenView(TrackerScreen trackerType) {
        // Simply call on the overloaded function
        sendScreenView(trackerType, null);
    }

    synchronized void sendScreenView(TrackerScreen trackerType, String moreInfo) {
        // Represents the final screenName to send along
        String finalScreenName;

        switch(trackerType) {
            case ABOUT:
                finalScreenName = "About Page";
                break;
            case ARVIEW:
                finalScreenName = "AR View Controller";
                break;
            case ARWEB:
                finalScreenName = "AR Web View Controller";
                break;
            case CAMVIEW:
                finalScreenName = "Camera View Controller";
                break;
            case DETAILVIEW:
            case GENWEBVIEW:
            case MODALWEB:
                // Simply should be the title of the story or url- what the "moreInfo" should be
                finalScreenName = moreInfo;
                break;
            case MAGSTORY:
                // Concatenate name of the story - moreInfo - with the heading
                finalScreenName = "Magazine Story - " + moreInfo;
                break;
            case MAINVIEW:
                finalScreenName = "Main View Controller";
                break;
            case MICHENG:
                finalScreenName = "Michigan Engineer";
                break;
            case PRIVACYPOL:
                finalScreenName = "Privacy Policy";
                break;
            default:
                Log.e("MD/AnalyticsHelper", "ERROR! Went to default logic branch!");
                return;
        }

        // Finally, send the screen view with the name
        sendScreenView(finalScreenName);
    }

    // Send an Analytics hit with the given screenName
    private void sendScreenView(String screenName) {
        // First, get the tracker
        Tracker tracker = getTracker();

        // Then give it the screenName
        tracker.setScreenName(screenName);

        // Finally, send the screen view
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
