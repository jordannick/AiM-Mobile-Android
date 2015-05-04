package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailActivity extends FragmentActivity implements DetailMainFragment.Callbacks, DetailNotesFragment.Callbacks {
    private static final String TAG = "DetailActivity";

    public WorkOrder mWorkOrder;
    private ActionBar actionBar;
    private View v;

    //Sliding Tabs
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    //Callback
    public void onWorkOrderUpdated() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        v = this.findViewById(R.id.detail_activity_layout);

        mWorkOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);


        final List<DetailPagerItem> mTabs = new ArrayList<>();
        mTabs.add(new DetailPagerItem("Overview", Color.BLUE, Color.GRAY, new DetailMainFragment()));
        mTabs.add(new DetailPagerItem("Notes", Color.RED, Color.GRAY, new DetailNotesFragment()));
        mTabs.add(new DetailPagerItem("Contact", Color.YELLOW, Color.GRAY, new DetailContactFragment()));

        //Set ViewPager Adapter
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mTabs.get(position).getFragment();
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public CharSequence getPageTitle(int position){
                return mTabs.get(position).getTitle();
            }


        });

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);




        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }
        });


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

/*        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab Tab1, Tab2, Tab3;*/

/*        Fragment fragmentTab1 = new DetailMainFragment();
        Fragment fragmentTab2 = new DetailNotesFragment();
        Fragment fragmentTab3 = new DetailContactFragment();*/

/*        Tab1 = actionBar.newTab().setText("Overview");
        Tab1.setTabListener(new TabListener(fragmentTab1));
        actionBar.addTab(Tab1); */


        if(savedInstanceState!=null){
            //Restore the tab the user was last at
            int currentTab = savedInstanceState.getInt("CurrentDetailsTab");
            actionBar.selectTab(actionBar.getTabAt(currentTab));
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save which tab the user was last at
        outState.putInt("CurrentDetailsTab", actionBar.getSelectedTab().getPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                //Create and start the AddAction activity, sending the work order object
                Intent i = new Intent(this, AddActionActivity.class);
                i.putExtra(WorkOrder.WORK_ORDER_EXTRA, mWorkOrder);
                startActivity(i);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }
}
