package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;


/**
 * Created by sellersk on 8/19/2014.
 */
public class TabListener implements ActionBar.TabListener {

    Fragment mFragment;

    public TabListener(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragmentContainer, mFragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(mFragment);

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
