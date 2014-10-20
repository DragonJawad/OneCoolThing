package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by jawad on 18/10/14.
 */
public class CoolThing {
    private String id;
    private String publishDate;

    private String title;
    private String subTitle;
    private String bodyText;

    private Bitmap image;
    private String imageURL;

    private String collegeURL;
    private String fullItemURL;


    // Getter/setter methods:
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(String publishDate) {
            this.publishDate = publishDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getBodyText() {
            return bodyText;
        }

        public void setBodyText(String bodyText) {
            this.bodyText = bodyText;
        }

        public Bitmap getImage() { return image; }

        public void setImage(Bitmap image) { this.image = image; }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getCollegeURL() {
            return collegeURL;
        }

        public void setCollegeURL(String collegeURL) {
            this.collegeURL = collegeURL;
        }

        public String getFullItemURL() {
            return fullItemURL;
        }

        public void setFullItemURL(String fullItemURL) {
            this.fullItemURL = fullItemURL;
        }
    // end of getter/setter methods

    public CoolThing(String id, String title, String bodyText){
        setId(id);
        setTitle(title);
        setBodyText(bodyText);
    }
}
