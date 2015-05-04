package edu.oregonstate.AiMLiteMobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by sellersk on 5/4/2015.
 */
public class OverviewPagerItem {

    private final CharSequence mTitle;
    private final int mIndicatorColor;
    private final int mDividerColor;
    private final Fragment mFragment;

    OverviewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
        mTitle = title;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
        mFragment = new OverviewListFragment();


        Bundle bundle = new Bundle();
        bundle.putString("sectionFilter", title.toString());
        mFragment.setArguments(bundle);
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
