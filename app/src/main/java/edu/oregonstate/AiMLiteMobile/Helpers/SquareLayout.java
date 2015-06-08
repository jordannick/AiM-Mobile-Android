package edu.oregonstate.AiMLiteMobile.Helpers;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by sellersk on 6/8/2015.
 */
public class SquareLayout extends LinearLayout{

    public SquareLayout(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }
}
