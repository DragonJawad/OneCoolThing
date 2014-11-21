package edu.umich.engin.cm.onecoolthing.Activity;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by jawad on 20/11/14.
 */
public class NavListView extends ListView {
    public NavListView(Context context) {
        super(context);
    }

    public NavListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if(gainFocus) {
            setSelection(0);
        } else {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }
}
