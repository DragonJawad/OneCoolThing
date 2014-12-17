package edu.umich.engin.cm.onecoolthing.Decoder;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by jawad on 16/12/14.
 */
public class DecoderAppMenuView extends LinearLayout
{

    int horizontalClipping = 0;


    public DecoderAppMenuView(Context context)
    {
        super(context);
    }


    public DecoderAppMenuView(Context context, AttributeSet attribute)
    {
        super(context, attribute);
    }


    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.clipRect(0, 0, horizontalClipping, canvas.getHeight());
        super.onDraw(canvas);
    }


    public void setHorizontalClipping(int hClipping)
    {
        horizontalClipping = hClipping;
        invalidate();
    }

}

