package edu.umich.engin.cm.onecoolthing.CoolFeeds;

/**
 * Created by jawad on 19/09/15.
 */
public class OneCoolFeed extends CoolFeed {
    private static final String FEED_URL = "http://www.engin.umich.edu/college/about/news/news/coolthingindexjson";

    @Override
    public String getBaseUrl() {
        return FEED_URL;
    }
}
