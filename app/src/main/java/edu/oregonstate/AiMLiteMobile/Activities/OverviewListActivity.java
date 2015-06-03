package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.OverviewListFragment;
import edu.oregonstate.AiMLiteMobile.Helpers.OverviewPagerItem;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Helpers.SlidingTabLayout;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;


public class OverviewListActivity extends FragmentActivity implements OverviewListFragment.Callbacks {
    private static final String TAG = "OverviewListActivity";

    private static CurrentUser sCurrentUser;
    private AlertDialog logoutDialog;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private OverviewPagerItem dailyPagerItem;
    private OverviewPagerItem backlogPagerItem;
    private View dimOverlay;

    private final List<OverviewPagerItem> mTabs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);


        sCurrentUser = CurrentUser.get(getApplicationContext());
        getActionBar().setTitle("AiM Lite Mobile");


        dimOverlay = findViewById(R.id.dim_overlay);

        // Create tab and pager items

        mTabs.add(new OverviewPagerItem("Overview",getResources().getColor(R.color.tab_color), Color.GRAY));
        mTabs.add(new OverviewPagerItem("Work Orders",getResources().getColor(R.color.tab_color), Color.GRAY));

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
            public CharSequence getPageTitle(int position) {
                return mTabs.get(position).getTitle();
            }
        });

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setBackgroundResource(R.color.theme_secondary);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO 3/12/2015 - check stored last updated time against current time, if longer than some interval, refresh
        // updateWorkOrderList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Start an instance of DetailActivity
    public void onWorkOrderSelected(WorkOrder workOrder){
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    /* Creates a new AlertDialog if one hasn't been already. Then displays it.
    Prompts the user to confirm Logout or Cancel. */
    @Override
    public void onBackPressed(){
        if(logoutDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you done?");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Logout by finishing the Activity
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            logoutDialog = builder.create();
        }
        logoutDialog.show();
    }

    public void lockScreen(){
        Log.d(TAG, "dim overlay appear");
        dimOverlay.setVisibility(View.VISIBLE);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_dim);
        fadeInAnimation.setFillAfter(true);

        dimOverlay.startAnimation(fadeInAnimation);
    }

    public void unlockScreen(){
        Log.d(TAG, "dim overlay gone");
        dimOverlay.setVisibility(View.INVISIBLE);

        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_dim);
        fadeOutAnimation.setFillBefore(true);

        dimOverlay.startAnimation(fadeOutAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);
        return true;
    }

    public void beginActionQueueAcitivity(){
        Intent i = new Intent(this, ActionQueueListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case  R.id.action_queue:
                beginActionQueueAcitivity();
                break;
       /* else if (id == R.id.action_settings) {
            return true;
        }*/
            case R.id.log_out:
                sCurrentUser.prepareLogout();
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getDailyCount(){
        OverviewListFragment listFragment = (OverviewListFragment) mTabs.get(1).getFragment();
        return ((WorkOrderAdapter)listFragment.getListAdapter()).sectionBacklogIndex - 1;
    }


    public int getBacklogCount(){
        OverviewListFragment listFragment = (OverviewListFragment) mTabs.get(1).getFragment();
        return ((WorkOrderAdapter)listFragment.getListAdapter()).sectionAdminIndex - getDailyCount() - 1; // lol at this
    }

    public void scrollToSection(String section){
        mViewPager.setCurrentItem(1, true); //switch tabs
        OverviewListFragment listFragment = (OverviewListFragment) mTabs.get(1).getFragment();

        switch (section){
            case "Daily":
                listFragment.getListView().smoothScrollToPositionFromTop(((WorkOrderAdapter)listFragment.getListAdapter()).sectionDailyIndex, 0, 400); //scroll to position
                break;
            case "Backlog":
                listFragment.getListView().smoothScrollToPositionFromTop(((WorkOrderAdapter)listFragment.getListAdapter()).sectionBacklogIndex, 0, 400); //scroll to position
                break;
            case "Admin":
                listFragment.getListView().smoothScrollToPositionFromTop(((WorkOrderAdapter)listFragment.getListAdapter()).sectionAdminIndex, 0, 400); //scroll to position
                break;
            case "Completed":
                listFragment.getListView().smoothScrollToPositionFromTop(((WorkOrderAdapter)listFragment.getListAdapter()).sectionCompletedIndex, 0, 400); //scroll to position
                break;
        }
        //((OverviewListFragment)mTabs.get(1).getFragment()).getListView().smoothScrollToPositionFromTop(position, 0, 700); //scroll to position

    }

}