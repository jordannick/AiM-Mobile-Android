package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Fragments.DetailMainFragment;
import edu.oregonstate.AiMLiteMobile.Fragments.DetailNotesFragment;
import edu.oregonstate.AiMLiteMobile.Helpers.DetailPagerItem;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Helpers.SlidingTabLayout;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;


/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailActivity extends SingleFragmentActivity implements DetailNotesFragment.Callbacks {
    private static final String TAG = "DetailActivity";

    private static CurrentUser sCurrentUser;
    public static WorkOrder mWorkOrder;
    //private ActionBar actionBar;
    //private View v;


    //Sliding Tabs
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    //Callback
    public void onWorkOrderUpdated() {

    }
    @Override
    protected Fragment createFragment() {
        return new DetailMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.detail_activity);

        //v = this.findViewById(R.id.detail_activity_layout);

        sCurrentUser = CurrentUser.get(getApplicationContext());
        mWorkOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        Log.d(TAG, "DetailActivity mWorkOrder: " + mWorkOrder);

        final List<DetailPagerItem> mTabs = new ArrayList<>();
       // mTabs.add(new DetailPagerItem("Overview", getResources().getColor(R.color.tab_color), Color.GRAY, new DetailMainFragment()));
       // mTabs.add(new DetailPagerItem("Notes", getResources().getColor(R.color.tab_color), Color.GRAY, new DetailNotesFragment()));
        //mTabs.add(new DetailPagerItem("Requestor", getResources().getColor(R.color.tab_color), Color.GRAY, new DetailContactFragment()));

        //Set ViewPager Adapter
       /* mViewPager = (ViewPager) findViewById(R.id.viewpager);
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
        mSlidingTabLayout.setBackgroundResource(R.color.theme_primary);
        mSlidingTabLayout.setViewPager(mViewPager);






        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }
        });*/


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);



    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save which tab the user was last at
        //outState.putInt("CurrentDetailsTab", actionBar.getSelectedTab().getPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.log_out:
                sCurrentUser.prepareLogout();
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }
}
