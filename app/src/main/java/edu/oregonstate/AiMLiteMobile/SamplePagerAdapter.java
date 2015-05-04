package edu.oregonstate.AiMLiteMobile;

import android.support.v4.view.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sellersk on 5/4/2015.
 */
public class SamplePagerAdapter extends PagerAdapter {


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
