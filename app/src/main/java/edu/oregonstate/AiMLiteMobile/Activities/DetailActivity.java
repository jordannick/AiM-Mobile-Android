package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        //Typeface FONTAWESOME = Typeface.createFromAsset(this.getAssets(), "fonts/FontAwesome.otf");

        /*MenuItem itemNotification = menu.findItem(R.id.menu_notification);
        MenuItem itemTimeLog = menu.findItem(R.id.menu_notification);

        TextView itemNotificationTextView = (TextView) itemNotification.getActionView();
        TextView itemTimeLogTextView = (TextView) itemTimeLog.getActionView();

        itemNotificationTextView.setTypeface(FONTAWESOME);
        itemNotificationTextView.setTypeface(FONTAWESOME);

        itemNotificationTextView.setText(getString(R.string.icon_notices));
        itemTimeLogTextView.setText(getString(R.string.icon_timeLog));


*/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_notification:
                createNoticesViewPopup();
                break;
            case R.id.action_queue:
                beginActionQueueActivity();
                break;
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

    private void createNoticesViewPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.popup_notes_list, null);

        convertView.findViewById(R.id.dialogNotes_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        NoticeAdapter noticesAdapter = new NoticeAdapter(this, sCurrentUser.getNotices());
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupNotes_listView);
        lv.setSelector(android.R.color.transparent);
        lv.setAdapter(noticesAdapter);
        alertDialog.show();
    }

    public void beginActionQueueActivity(){
        Intent i = new Intent(this, ActionQueueListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}
