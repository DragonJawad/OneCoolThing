package edu.umich.engin.cm.onecoolthing.MichEngMag;

/**
 * Created by jawad on 25/11/14.
 *
 * Represents a single row in the ListView for the Michigan Engineering Magazine
 */
public class MichEngMagRow {
    // Determines if this is a level1 [single] item. If false, then multi-level item
    boolean singleLevel = false;

    // Data for first level or first part
    String firstTitle;
    String firstShortTitle;
    String firstUrl;
    String firstBgUrl;

    // Data for second part, if necessary
    String secondTitle;
    String secondShortTitle;
    String secondUrl;
    String secondBgUrl;

    public boolean isSingleLevel() {
        return singleLevel;
    }

    public void setSingleLevel(boolean singleLevel) {
        this.singleLevel = singleLevel;
    }

    public String getFirstTitle() {
        return firstTitle;
    }

    public void setFirstTitle(String firstTitle) {
        this.firstTitle = firstTitle;
    }

    public String getFirstShortTitle() {
        return firstShortTitle;
    }

    public void setFirstShortTitle(String firstShortTitle) {
        this.firstShortTitle = firstShortTitle;
    }

    public String getFirstUrl() {
        return firstUrl;
    }

    public void setFirstUrl(String firstUrl) {
        this.firstUrl = firstUrl;
    }

    public String getFirstBgUrl() {
        return firstBgUrl;
    }

    public void setFirstBgUrl(String firstBgUrl) {
        this.firstBgUrl = firstBgUrl;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getSecondShortTitle() {
        return secondShortTitle;
    }

    public void setSecondShortTitle(String secondShortTitle) {
        this.secondShortTitle = secondShortTitle;
    }

    public String getSecondUrl() {
        return secondUrl;
    }

    public void setSecondUrl(String secondUrl) {
        this.secondUrl = secondUrl;
    }

    public String getSecondBgUrl() {
        return secondBgUrl;
    }

    public void setSecondBgUrl(String secondBgUrl) {
        this.secondBgUrl = secondBgUrl;
    }
}
