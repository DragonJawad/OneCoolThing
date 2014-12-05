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

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ImageLoaderNoCache;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 25/11/14.
 *
 * Adapter for the MichEngMag ListView
 */
public class MichEngMagListAdapter extends BaseAdapter implements ParseMichEngMag.MagParserSubscriber,
                    ImageLoaderNoCache.SpecializedManager {
    private static final String TAG = "MD/MichEngMagListAdapter";

    Context mContext;
    ImageLoaderNoCache mImageLoader;

    // Holds all of the rows' data in a single list
    ArrayList<MichEngMagRow> mRowList;

    // Caches all of the views and bitmaps for the rows in a single list
    ArrayList<RowViewDataHolder> mRowViewList;

    // Reference for who is to handle the MEMDetailed data
    MagazineViewer mMagazineViewer;

    // States whether or not already opening some detailed view
    boolean isAnItemOpening = false;

    // Interface for passing up the detailed data to whoever can use it
    public interface MagazineViewer{
        public void openMagazineItem(MEMDetailedData data);
    }

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

    public void setMagazineViewer(MagazineViewer magazineViewer) {
        this.mMagazineViewer = magazineViewer;
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
            if(itemData.getLevel() == 1 || itemData.getLevel() == 2) {
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

        // Instantiate the mRowViewList
        mRowViewList = new ArrayList<RowViewDataHolder>(mRowList.size());

        // Fill the rowDataHolder with dummy data for each row
        for(int i = 0; i < mRowList.size(); ++i) {
            // Create a new dummy RowViewDataHolder
            RowViewDataHolder rowViewDataHolder = new RowViewDataHolder();

            // Add the RowViewDataHolder to the list
            mRowViewList.add(rowViewDataHolder);
        }

        // Notify the adapter that the data set has been changed
        notifyDataSetChanged();
    }

    // Be notified once the data for a detailed view has been received
    @Override
    public void gotDetailedItem(MEMDetailedData data) {
        // Give the data the bitmap it specified by getting it from the appropriate row;
        RowViewDataHolder viewData = mRowViewList.get(data.getAdapterRowPosition());
        int spot = data.getAdapterSpot();
        if(spot == 0) {
            data.setmTopImageBitmap(viewData.singleLevelBitmap);
        }
        else if(spot == 1) {
            data.setmTopImageBitmap(viewData.splitLeftBitmap);
        }
        else if(spot == 2) {
            data.setmTopImageBitmap(viewData.splitRightBitmap);
        }
        else {
            Log.e(TAG, "gotDetailedItem(): Error! spot int is out of expected value range!");
        }

        // State that the item has opened
        isAnItemOpening = false;

        // Pass the data up the chain to set up the MEMDetailed display
        mMagazineViewer.openMagazineItem(data);
    }

    /**
     * Lazily loads in the bitmap, if necessary
     * @param bitmap
     * @param paramA - Determines the row this bitmap belongs to [0 is first row]
     * @param paramB - Determines the spot this bitmap belongs to [0 is singeLevel,
     *                      1 is splitLeft, 2 is splitRight]
     */
    @Override
    public void notifyRetrievedBitmap(Bitmap bitmap, int paramA, int paramB) {
        // First, get the rowViewData for this position
        RowViewDataHolder curViewData = mRowViewList.get(paramA);

        // Cache the bitmap in the appropriate location
        switch(paramB) {
            case 0:
                curViewData.singleLevelBitmap = bitmap;
                break;
            case 1:
                curViewData.splitLeftBitmap = bitmap;
                break;
            case 2:
                curViewData.splitRightBitmap = bitmap;
                break;
            default:
                Log.e(TAG, "Error: paramB is outside of expected values");
                break;
        }

        // See if the imageView for this location is still usable
        if(paramB == 0) {
            // Check the singleLevel imageView
            if(curViewData.singeLevelImage != null) {
                // If it's not null, then set the bitmap onto it
                curViewData.singeLevelImage.setImageBitmap(bitmap);
            }
        }
        else if(paramB == 1) {
            // Check the splitLeft imageView
            if(curViewData.splitLeftImage != null) {
                // If it's not null, then set the bitmap onto it
                curViewData.splitLeftImage.setImageBitmap(bitmap);
            }
        }
        else {
            // Check the splitLeft imageView
            if(curViewData.splitRightImage != null) {
                // If it's not null, then set the bitmap onto it
                curViewData.splitRightImage.setImageBitmap(bitmap);
            }
        }

        // State that finished loading the current row's bitmap
        curViewData.loadingBitmap = false;
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

            // Get the row view data so far
            RowViewDataHolder thisRowViewData = mRowViewList.get(position);
            // If the bitmap has already been cached, then use it
            if(thisRowViewData.singleLevelBitmap != null) {
                holder.singleLevelBackground.setImageBitmap(thisRowViewData.singleLevelBitmap);
            }
            // Otherwise, get the bitmap
            else {
                // Clear the background, just in case this is recycled and already has a bg
                holder.singleLevelBackground.setImageBitmap(null);

                // Add the current imageView to this row's view data
                thisRowViewData.singeLevelImage = holder.singleLevelBackground;

                // Check all other singleLevelImages to check if there is a match
                for(int i = 0; i < mRowViewList.size(); i++) {
                    // If this is the current row, then skip redundantly checking this row
                    if(i == position) continue;

                    // Get the current rowViewDataHolder
                    RowViewDataHolder curRowViewData = mRowViewList.get(i);

                    // Check if the current rowViewDataHolder holds the same imageView
                    if(curRowViewData.singeLevelImage == holder.singleLevelBackground) {
                        // Then set the repeat to null, to show that it's unneccessary now
                        curRowViewData.singeLevelImage = null;

                        // As there should only be one repeat possible, save some time and break
                        break;
                    }
                }

                // Check if not already loading the bitmap
                if(!thisRowViewData.loadingBitmap) {
                    // If not, then get the bitmap
                    mImageLoader.GetImage(curRowData.getFirstBgUrl(), this, position, 0);
                }
            }

            // Set the listener to open up this item in-depth
            holder.singeLevelContainer.setOnClickListener(
                    new MagazineClickListener(curRowData.getFirstUrl(), position, 0));
        }
        // Otherwise, set it up as a multi level row
        else {
            // Show the split container and make sure the single level container is not showing
            holder.splitLevelContainer.setVisibility(View.VISIBLE);
            holder.singeLevelContainer.setVisibility(View.GONE);

            // Set the titles
            holder.splitLeftTitle.setText(curRowData.getFirstShortTitle());
            holder.splitRightTitle.setText(curRowData.getSecondShortTitle());

            // Get the row view data so far
            RowViewDataHolder thisRowViewData = mRowViewList.get(position);

            // First, check the bitmap for the splitLeft item
            if(thisRowViewData.splitLeftBitmap != null) {
                // If the bitmap has already been cached, then use it
                holder.splitLeftBackground.setImageBitmap(thisRowViewData.splitLeftBitmap);
            }
            // Otherwise, get the bitmap
            else {
                // Clear the background, just in case this is recycled and already has a bg
                holder.splitLeftBackground.setImageBitmap(null);

                // Add the current imageView to this row's view data
                thisRowViewData.splitLeftImage = holder.splitLeftBackground;

                // Check all other singleLevelImages to check if there is a match
                for (int i = 0; i < mRowViewList.size(); i++) {
                    // If this is the current row, then skip redundantly checking this row
                    if (i == position) continue;

                    // Get the current rowViewDataHolder
                    RowViewDataHolder curRowViewData = mRowViewList.get(i);

                    // Check if the current rowViewDataHolder holds the same imageView
                    if (curRowViewData.splitLeftImage == holder.splitLeftBackground) {
                        // Then set the repeat to null, to show that it's unneccessary now
                        curRowViewData.splitLeftImage = null;

                        // As there should only be one repeat possible, save some time and break
                        break;
                    }
                }

                // Check if not already loading the bitmap
                if(!thisRowViewData.loadingBitmap) {
                    // If not, then get the bitmap
                    mImageLoader.GetImage(curRowData.getFirstBgUrl(), this, position, 1);
                }
            }

            // Now check the bitmap for the splitRight item
            if(thisRowViewData.splitRightBitmap != null) {
                // If the bitmap has already been cached, then use it
                holder.splitRightBackground.setImageBitmap(thisRowViewData.splitRightBitmap);
            }
            // Otherwise, get the bitmap
            else {
                // Clear the background, just in case this is recycled and already has a bg
                holder.splitRightBackground.setImageBitmap(null);

                // Add the current imageView to this row's view data
                thisRowViewData.splitRightImage = holder.splitRightBackground;

                // Check all other singleLevelImages to check if there is a match
                for (int i = 0; i < mRowViewList.size(); i++) {
                    // If this is the current row, then skip redundantly checking this row
                    if (i == position) continue;

                    // Get the current rowViewDataHolder
                    RowViewDataHolder curRowViewData = mRowViewList.get(i);

                    // Check if the current rowViewDataHolder holds the same imageView
                    if (curRowViewData.splitRightImage == holder.splitRightBackground) {
                        // Then set the repeat to null, to show that it's unneccessary now
                        curRowViewData.splitRightImage = null;

                        // As there should only be one repeat possible, save some time and break
                        break;
                    }
                }

                // Check if not already loading the bitmap
                if(!thisRowViewData.loadingBitmap) {
                    // If not, then get the bitmap
                    mImageLoader.GetImage(curRowData.getSecondBgUrl(), this, position, 2);
                }
            }

            // Set the listeners to open up these items in-depth
            holder.splitLevelContainerLeft.setOnClickListener(
                    new MagazineClickListener(curRowData.getFirstUrl(), position, 1));
            holder.splitLevelContainerRight.setOnClickListener(
                    new MagazineClickListener(curRowData.getSecondUrl(), position, 2));
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
        String targetUrl; // The URL to get the data from
        int rowPosition; // Corresponds to the row that this data belongs to
        int spot; // Corresponds to the spot that this data belongs to

        // Default constructor, save the target data
        public MagazineClickListener(String url, int rowPosition, int spot) {
            this.targetUrl = url;
            this.rowPosition = rowPosition;
            this.spot = spot;
        }

        @Override
        public void onClick(View v) {
            // Check that an item is not already loading
            if(!isAnItemOpening) {
                // State an item is now opening
                isAnItemOpening = true;

                // Create a new MEMDetailedData object and add the position and spot to it
                MEMDetailedData data = new MEMDetailedData();
                data.setAdapterRowPosition(rowPosition);
                data.setAdapterSpot(spot);

                // Use the parser to get the data
                ParseMichEngMag parser = new ParseMichEngMag();
                parser.getDetailedData(mContext, MichEngMagListAdapter.this, data, targetUrl);
            }
        }
    }

    // Holds the respective ImageViews and Bitmaps for a row
    class RowViewDataHolder {
        public boolean loadingBitmap;

        public ImageView singeLevelImage;
        public ImageView splitLeftImage;
        public ImageView splitRightImage;

        public Bitmap singleLevelBitmap;
        public Bitmap splitLeftBitmap;
        public Bitmap splitRightBitmap;
    }
}
