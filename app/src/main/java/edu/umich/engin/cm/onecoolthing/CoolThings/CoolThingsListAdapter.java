package edu.umich.engin.cm.onecoolthing.CoolThings;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.NetworkUtils.ImageLoader;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 18/10/14.
 *
 * Used for debugging and testing JSON to Cool Thing systems
 */
public class CoolThingsListAdapter extends BaseAdapter {
    Context mContext;

    // ImageLoader used to lazy insert images into listViews
    ImageLoader imageLoader;

    // List of cool things
    ArrayList<CoolThingData> coolThings;

    // Determines whether or not to use images
    boolean useImages = false;

    public CoolThingsListAdapter(Context context, ArrayList<CoolThingData> coolThings) {
        this.coolThings = coolThings;
        this.mContext = context;

        // Set up the new mImageLoader, and clear its cache
        imageLoader = new ImageLoader(context);
        imageLoader.clearCache();
    }

    public CoolThingsListAdapter(Context context, ArrayList<CoolThingData> coolThings, boolean useImages) {
        this.coolThings = coolThings;
        this.mContext = context;
        this.useImages = useImages;

        // Set up the new mImageLoader, and clear its cache
        imageLoader = new ImageLoader(context);
        imageLoader.clearCache();
    }

    @Override
    public int getCount() {
        return coolThings.size();
    }

    @Override
    public Object getItem(int position) {
        return coolThings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        ViewHolder holder;

        if(convertView == null) {
            // Then gotta set up this row for the first time
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_jsontest, parent, false);

            // Create a ViewHolder to save all the different parts of the row
            holder = new ViewHolder();
            holder.id = (TextView) row.findViewById(R.id.id);
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.body = (TextView) row.findViewById(R.id.body);
            holder.image = (ImageView) row.findViewById(R.id.icon);

            // Make the row reuse the ViewHolder
            row.setTag(holder);
        }
        else { // Otherwise, use the recycled view
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        // Set the current row's data
        CoolThingData thisThing = coolThings.get(position);
        holder.id.setText(thisThing.getId());
        holder.name.setText(thisThing.getTitle());
        holder.body.setText(thisThing.getBodyText());

        // Lazily load the image
        String url = thisThing.getImageURL();
        Log.d("MD/Adapter", "URL: "+url);
        imageLoader.DisplayImage(url, holder.image);

        return row;
    }

    public static class ViewHolder{

        public TextView id;
        public TextView name;
        public TextView body;

        public ImageView image;

    }
}
