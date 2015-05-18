package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Fragments.OverviewListFragment;
import edu.oregonstate.AiMLiteMobile.Helpers.OverviewPagerItem;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Helpers.SlidingTabLayout;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;


public class OverviewListActivity extends FragmentActivity implements OverviewListFragment.Callbacks {
    private static final String TAG = "OverviewListActivity";

    private AlertDialog logoutDialog;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private OverviewPagerItem dailyPagerItem;
    private OverviewPagerItem backlogPagerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        // Create tab and pager items
        final List<OverviewPagerItem> mTabs = new ArrayList<>();
        mTabs.add(new OverviewPagerItem("Daily", 0 ,getResources().getColor(R.color.tab_color), Color.GRAY));
        mTabs.add(new OverviewPagerItem("Backlog", 0 ,getResources().getColor(R.color.tab_color), Color.GRAY));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case  R.id.action_queue:
                Intent i = new Intent(this, ActionQueueListActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                break;
       /* else if (id == R.id.action_settings) {
            return true;
        }*/
            case R.id.log_out:
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}