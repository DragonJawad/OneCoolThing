package edu.umich.engin.cm.onecoolthing.CoolFeeds;

/**
 * Created by jawad on 19/09/15.
 */
public class MichiganCoolFeed extends CoolFeed {
    private static final String FEED_URL = "http://www.engin.umich.edu/college/about/news/news/michengindexjson";

    @Override
    public String getBaseUrl() {
        return FEED_URL;
    }
}
