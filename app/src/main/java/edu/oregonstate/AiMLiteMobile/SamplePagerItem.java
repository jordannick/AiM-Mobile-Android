package edu.oregonstate.AiMLiteMobile;

import android.support.v4.app.Fragment;

/**
 * Created by sellersk on 5/4/2015.
 */
public class SamplePagerItem {


    private String mTitle;
    private int mIndicatorColor;
    private int mDividerColor;

    private Fragment fragment;

    public SamplePagerItem(String mTitle, int mIndicatorColor, int mDividerColor, Fragment fragment) {
        this.mTitle = mTitle;
        this.mIndicatorColor = mIndicatorColor;
        this.mDividerColor = mDividerColor;
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmIndicatorColor() {
        return mIndicatorColor;
    }

    public void setmIndicatorColor(int mIndicatorColor) {
        this.mIndicatorColor = mIndicatorColor;
    }

    public int getmDividerColor() {
        return mDividerColor;
    }

    public void setmDividerColor(int mDividerColor) {
        this.mDividerColor = mDividerColor;
    }
}
