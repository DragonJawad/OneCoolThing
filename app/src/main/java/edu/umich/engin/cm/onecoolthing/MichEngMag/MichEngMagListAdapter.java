package edu.umich.engin.cm.onecoolthing.MichEngMag;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ImageLoaderNoCache;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 25/11/14.
 *
 * Adapter for the MichEngMag ListView
 */
public class MichEngMagListAdapter extends BaseAdapter implements ParseMichEngMag.MagParserSubscriber {
    private static final String TAG = "MD/MichEngMagListAdapter";

    Context mContext;
    ImageLoaderNoCache mImageLoader;

    // Holds all of the rows' data in a single list
    ArrayList<MichEngMagRow> mRowList;

    // Default constructor
    MichEngMagListAdapter(Context context) {
        this.mContext = context;

        // Initialize the arrayList of all the data
        mRowList = new ArrayList<MichEngMagRow>();

        // Initialize the lazily loading imageLoader
        mImageLoader = new ImageLoaderNoCache();

        // Start the process for retrieving the JSON data
        ParseMichEngMag parser = new ParseMichEngMag();
        parser.getData(mContext, this);
    }

    public interface MagazineViewer{
        public void openItem(Bitmap image, String url);
    }

    // Be notified once the magazineList is retrieved and parsed
    @Override
    public void gotMagazine(ArrayList<MichEngMagItem> magazineList) {
        // Go through all the data and parse them into rows
        for(int i = 0; i < magazineList.size(); ++i) {
            // Create a new row instance
            MichEngMagRow rowData = new MichEngMagRow();

            // Get the current item of the magazine
            MichEngMagItem itemData = magazineList.get(i);

            // Insert the itemData's into the rowData
            rowData.setFirstTitle(itemData.getTitle());
            rowData.setFirstShortTitle(itemData.getShortTitle());
            rowData.setFirstUrl(itemData.getUrl());
            rowData.setFirstBgUrl(itemData.getImageUrl());

            // If the item is a single level item, then set up the row as such
            if(itemData.getLevel() == 1) {
                // Tell the rowData that this IS a single level row
                rowData.setSingleLevel(true);
            }

            // Otherwise, this is a multi-level item
            else {
                // Tell the rowData that this is NOT a single level row
                rowData.setSingleLevel(false);

                // Increment i again to represent the next item's data has been taken care of
                i += 1;

                // Get the next item's data as well
                MichEngMagItem secondItemData = magazineList.get(i);

                // Set that data into the row
                rowData.setSecondTitle(secondItemData.getTitle());
                rowData.setSecondShortTitle(secondItemData.getShortTitle());
                rowData.setSecondUrl(secondItemData.getUrl());
                rowData.setSecondBgUrl(secondItemData.getImageUrl());
            }

            // Add the rowData to the row list
            mRowList.add(rowData);
        }

        // Notify the adapter that the data set has been changed
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRowList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    } // To disable ListView clicking

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Size of data: " + mRowList.size() + " | @pos: " + position);

        View row = null;
        ViewHolder holder;

        if(convertView == null) {
            // Then gotta set up this row for the first time
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_michengmag_row, parent, false);

            // Create a ViewHolder to save all the different parts of the row
            holder = new ViewHolder();

            holder.singeLevelContainer = (View) row.findViewById(R.id.container_single);
            holder.singleLevelBackground = (ImageView) row.findViewById(R.id.level1_background);
            holder.singeLevelTitle = (TextView) row.findViewById(R.id.level1_title);

            holder.splitLevelContainer = (View) row.findViewById(R.id.container_split);
            holder.splitLevelContainerLeft = (View) row.findViewById(R.id.level2_left);
            holder.splitLevelContainerRight = (View) row.findViewById(R.id.level2_right);

            holder.splitLeftBackground = (ImageView) row.findViewById(R.id.level2_left_background);
            holder.splitLeftTitle = (TextView) row.findViewById(R.id.level2_left_title);
            holder.splitRightBackground = (ImageView) row.findViewById(R.id.level2_right_background);
            holder.splitRightTitle = (TextView) row.findViewById(R.id.level2_right_title);

            // Make the row reuse the ViewHolder
            row.setTag(holder);
        }
        else { // Otherwise, use the recycled view
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        // Get the data for the current row
        MichEngMagRow curRowData = mRowList.get(position);

        // If the data specifies a single level row, then set it up as such
        if(curRowData.isSingleLevel()) {
            // Hide the split container and make sure the single level container is showing
            holder.splitLevelContainer.setVisibility(View.GONE);
            holder.singeLevelContainer.setVisibility(View.VISIBLE);

            // Set the title
            holder.singeLevelTitle.setText(curRowData.getFirstShortTitle());

            // Lazily load the background
            mImageLoader.DisplayImage(curRowData.getFirstBgUrl(), holder.singleLevelBackground);

            // Set the listener to open up this item in-depth
            holder.singeLevelContainer.setOnClickListener(
                    new MagazineClickListener(holder.singleLevelBackground, curRowData.getFirstUrl()));
        }
        // Otherwise, set it up as a multi level row
        else {
            // Show the split container and make sure the single level container is not showing
            holder.splitLevelContainer.setVisibility(View.VISIBLE);
            holder.singeLevelContainer.setVisibility(View.GONE);

            // Set the titles
            holder.splitLeftTitle.setText(curRowData.getFirstShortTitle());
            holder.splitRightTitle.setText(curRowData.getSecondShortTitle());

            // Lazily load the backgrounds
            mImageLoader.DisplayImage(curRowData.getFirstBgUrl(), holder.splitLeftBackground);
            mImageLoader.DisplayImage(curRowData.getSecondBgUrl(), holder.splitRightBackground);

            // Set the listeners to open up these items in-depth
            holder.splitLevelContainerLeft.setOnClickListener(
                    new MagazineClickListener(holder.splitLeftBackground, curRowData.getFirstUrl()));
            holder.splitLevelContainerRight.setOnClickListener(
                    new MagazineClickListener(holder.splitRightBackground, curRowData.getSecondUrl()));
        }

        return row;
    }

    // Represents the necessary view components for a single row
    class ViewHolder {
        // Views for a single level display
        View singeLevelContainer;
        ImageView singleLevelBackground;
        TextView singeLevelTitle;

        // Views for a split display
        View splitLevelContainer;
        View splitLevelContainerLeft;
        View splitLevelContainerRight;

        ImageView splitLeftBackground;
        TextView splitLeftTitle;
        ImageView splitRightBackground;
        TextView splitRightTitle;
    }

    // The click listener for each magazine item
    class MagazineClickListener implements View.OnClickListener {
        ImageView targetImageView; // The ImageView which contains the target bitmap
        String targetUrl; // The URL to get the text from

        // Default constructor, save the target data
        public MagazineClickListener(ImageView imageView, String url) {
            this.targetImageView = imageView;
            this.targetUrl = url;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "Clicked on an item with url: " + targetUrl, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
