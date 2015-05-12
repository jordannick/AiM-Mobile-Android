package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Fragments.DetailMainFragment;
import edu.oregonstate.AiMLiteMobile.Fragments.OverviewListFragment;
import edu.oregonstate.AiMLiteMobile.Helpers.OverviewPagerItem;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Helpers.SlidingTabLayout;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;


public class OverviewListActivity extends FragmentActivity implements OverviewListFragment.Callbacks, DetailMainFragment.Callbacks {
    private static final String TAG = "OverviewListActivity";

    private static CurrentUser sCurrentUser;
    private ActionBar actionBar;
    private AlertDialog logoutDialog; //Set in onBackPressed()

    //Sliding Tabs
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private FragmentTabHost mTabHost;

    private int num_daily = 0;
    private int num_backlog = 0;

    private OverviewPagerItem dailyPagerItem;
    private OverviewPagerItem backlogPagerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        //actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setBackgroundDrawable(new ColorDrawable(R.color.theme_primary));

        sCurrentUser = CurrentUser.get(getApplicationContext());

       /* ActionBar.Tab Tab1, Tab2;
        Fragment fragmentTab1 = new OverviewListFragment();
        Fragment fragmentTab2 = new OverviewListFragment();*/


        dailyPagerItem = new OverviewPagerItem("Daily", num_daily ,getResources().getColor(R.color.tab_color), Color.GRAY);
        backlogPagerItem = new OverviewPagerItem("Backlog", num_backlog ,getResources().getColor(R.color.tab_color), Color.GRAY);

        final List<OverviewPagerItem> mTabs = new ArrayList<>();
        mTabs.add(dailyPagerItem);
        mTabs.add(backlogPagerItem);

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
            public CharSequence getPageTitle(int position) {
                return mTabs.get(position).getTitle();
            }


        });


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setBackgroundResource(R.color.theme_primary);
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
        //outState.putInt("CurrentSectionTab", actionBar.getSelectedTab().getPosition());

    }
/*
    public void onWorkOrderUpdated() {
        FragmentManager fm = getFragmentManager();
        OverviewListFragment listFragment = (OverviewListFragment)
                fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }
*/

    public void onWorkOrderSelected(WorkOrder workOrder){
/*        if (findViewById(R.id.detailFragmentContainer) == null) {
        // Start an instance of DetailActivity
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
            startActivity(i);
        } else { //Useful for later if implement swipe to change work order while in detail view
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = DetailMainFragment.newInstance(workOrder);
            if (oldDetail != null) {
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }*/
        // Start an instance of DetailActivity
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }


    // FUNCTION:
    //      Creates a new AlertDialog if one hasn't been already. Then displays it.     //
    //      Prompts the user to confirm Logout or Cancel.                               //
    @Override
    public void onBackPressed(){
        if(logoutDialog == null) {
            quickLog("logoutDialog was null");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set attributes
            builder.setTitle("Are you done?");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Logout by finishing the Activity
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Cancel
                }
            });
            logoutDialog = builder.create();
        }
        logoutDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_queue){
            Intent i = new Intent(this, ActionQueueListActivity.class);
            //i.putExtra(DetailMainFragment.WORK_ORDER_ID, wo.getId());
            startActivity(i);
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        }
       /* else if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }



    //Makes new request, if refresh needed, CurrentUser workorders will repopulate, and displayed list updated
    /*private void updateWorkOrderList(){

        CurrentUser currentUser = CurrentUser.get(getApplicationContext());

        TaskGetWorkOrders task = new TaskGetWorkOrders(this, currentUser, this, false);
        task.execute();
        onWorkOrderUpdated();

    }*/


    public ActionBar.Tab getCurrentlySelectedTab(){
        return actionBar.getSelectedTab();
    }

    private void quickLog(String text){
        Log.d(TAG, text);
    }

    //Currently nonfunctional
    public void updateSectionCounts() {
        //Update the number of work orders in each section
       /*for (WorkOrder wo : sCurrentUser.getWorkOrders()) {
            if (wo.getSection().equals("Daily")) {
                num_daily += 1;
            } else if (wo.getSection().equals("Backlog")) {
                num_backlog += 1;
            }

        }*/
        num_daily += 1;
        num_backlog += 3;


        Log.d(TAG, "updating section counts");
        dailyPagerItem.updateTitle("Daily", num_daily);
        backlogPagerItem.updateTitle("Backlog", num_backlog);

        //mViewPager.getAdapter().notifyDataSetChanged();

        Log.d(TAG, "daily: "+dailyPagerItem.getTitle());

        mViewPager.getAdapter().notifyDataSetChanged();
        mSlidingTabLayout.invalidate();
        mViewPager.destroyDrawingCache();
    }

}
