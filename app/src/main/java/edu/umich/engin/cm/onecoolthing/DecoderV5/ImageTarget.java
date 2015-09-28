package edu.umich.engin.cm.onecoolthing.DecoderV5;

/**
 * Created by jawad on 27/09/15.
 */
public class ImageTarget {
    private String targetName;
    private String targetUrl;
    private String targetEmailTo;
    private String targetEmailSubject;

    // Default constructor
    ImageTarget(String name, String url, String emailTo, String emailSubject) {
        setTargetName(name);
        setTargetUrl(url);
        setTargetEmailTo(emailTo);
        setTargetEmailSubject(emailSubject);
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTargetEmailTo() {
        return targetEmailTo;
    }

    public void setTargetEmailTo(String targetEmailTo) {
        this.targetEmailTo = targetEmailTo;
    }

    public String getTargetEmailSubject() {
        return targetEmailSubject;
    }

    public void setTargetEmailSubject(String targetEmailSubject) {
        this.targetEmailSubject = targetEmailSubject;
    }
}
