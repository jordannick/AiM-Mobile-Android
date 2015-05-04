package edu.oregonstate.AiMLiteMobile;

import android.support.v4.app.Fragment;

/**
 * Created by sellersk on 5/4/2015.
 */
public class DetailPagerItem {

    private final CharSequence mTitle;
    private final int mIndicatorColor;
    private final int mDividerColor;
    private final Fragment mFragment;

    DetailPagerItem(CharSequence title, int indicatorColor, int dividerColor, Fragment fragment) {
        mTitle = title;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
        mFragment = fragment;
    }

    CharSequence getTitle() {
        return mTitle;
    }

    int getIndicatorColor() {
        return mIndicatorColor;
    }

    int getDividerColor() {
        return mDividerColor;
    }

    public Fragment getFragment() {
        return mFragment;
    }
}
