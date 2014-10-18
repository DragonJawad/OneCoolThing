package edu.umich.engin.cm.onecoolthing.NetworkUtils;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 18/10/14.
 *
 * Used for debugging and testing JSON to Cool Thing systems
 */
public class CoolThingsListAdapter extends BaseAdapter {
    Context mContext;

    // List of cool things
    ArrayList<CoolThing> coolThings;

    public CoolThingsListAdapter(Context context, ArrayList<CoolThing> coolThings) {
        this.coolThings = coolThings;
        this.mContext = context;
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
        // TODO: Use the ViewHolder optimization design pattern

        View row = null;
        if(convertView == null) {
            // Then gotta set up this row for the first time
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_jsontest, parent, false);
        }
        else { // Otherwise, use the recycled view
            row = convertView;
        }

        // Get the layout views
        TextView id = (TextView) row.findViewById(R.id.id);
        TextView name = (TextView) row.findViewById(R.id.name);
        TextView body = (TextView) row.findViewById(R.id.body);

        // Set the current row's data
        CoolThing thisThing = coolThings.get(position);
        id.setText(thisThing.getId());
        name.setText(thisThing.getTitle());
        body.setText(thisThing.getBodyText());

        return row;
    }
}
