package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.OverviewListFragment;
import edu.oregonstate.AiMLiteMobile.Helpers.OverviewPagerItem;
import edu.oregonstate.AiMLiteMobile.Helpers.SlidingTabLayout;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;


public class OverviewListActivity extends Activity {

    private static final String TAG = "OverviewListActivity";

    private static CurrentUser sCurrentUser;
    private AlertDialog logoutDialog;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private View dimOverlay;

    private final List<OverviewPagerItem> mTabs = new ArrayList<>();

    private OverviewListActivity self;
    private ListView listView;
    private WorkOrderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        self = this;
        sCurrentUser = CurrentUser.get(getApplicationContext());

        //Debug, not avail in AppCompat
        //getActionBar().setTitle("AiM Lite Mobile");

        //dimOverlay = findViewById(R.id.dim_overlay);
        listView = (ListView)findViewById(R.id.overview_activity_listView);
        adapter = new WorkOrderAdapter(this, sCurrentUser.getWorkOrders());
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);



/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkOrderListItem item = adapter.getItem(position);
                if (item.getType() == WorkOrderListItem.Type.ITEM) {
                    onWorkOrderSelected(item.getWorkOrder());
                }
            }
        });*/

        setupSectionIcons();

        if (sCurrentUser.getNotices().size() > 0) {
            SnackbarManager.show(Snackbar.with(this).text("You have " + sCurrentUser.getNotices().size() + " new notice").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
        }

    }

    private void setupSectionIcons(){
        TextView tv0 = (TextView)findViewById(R.id.overview_activity_section_icon0);
        TextView tv1 = (TextView)findViewById(R.id.overview_activity_section_icon1);
        TextView tv2 = (TextView)findViewById(R.id.overview_activity_section_icon2);
        TextView tv3 = (TextView)findViewById(R.id.overview_activity_section_icon3);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");
        tv0.setTypeface(tf); tv1.setTypeface(tf); tv2.setTypeface(tf); tv3.setTypeface(tf);
        tv0.setText(R.string.icon_daily); tv1.setText(R.string.icon_backlog); tv2.setText(R.string.icon_admin); tv3.setText(R.string.icon_recentlyCompleted);

        setClickListener(tv0, adapter.sectionDailyIndex);
        setClickListener(tv1, adapter.sectionBacklogIndex+1);
        setClickListener(tv2, adapter.sectionAdminIndex+2);
        setClickListener(tv3, adapter.sectionCompletedIndex+3);
    }

    private void setClickListener(TextView tv, final int position) {

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listView.smoothScrollToPositionFromTop(adapter.sectionBacklogIndex, 0);
                listView.setSelection(position);


            }
        });
    }




    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();

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
        //Intent i = new Intent(this, TestActivity.class);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       // menu.findItem(R.id.menu_notification).setIcon(R.drawable.osu_icon);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_notification:
                createNoticesViewPopup();
                break;
            case  R.id.action_queue:
                beginActionQueueActivity();
                break;
            case R.id.log_out:
                sCurrentUser.prepareLogout();
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.force_refresh:
                //updateWorkOrderList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

/*    public int getDailyCount(){
        OverviewListFragment listFragment = (OverviewListFragment) mTabs.get(1).getFragment();
        return ((WorkOrderAdapter)listFragment.getListAdapter()).sectionBacklogIndex - 1;
    }*/


/*    public int getBacklogCount(){
        OverviewListFragment listFragment = (OverviewListFragment) mTabs.get(1).getFragment();
        return ((WorkOrderAdapter)listFragment.getListAdapter()).sectionAdminIndex - getDailyCount() - 1; // lol at this
    }*/

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

/*
    private void updateWorkOrderList(){
        Log.i(TAG, "Requested update work order list");
        ApiManager.getService().getWorkOrders(CurrentUser.getUsername(), CurrentUser.getToken(), new Callback<ResponseWorkOrders>() {
            @Override
            public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                ArrayList<WorkOrder> workOrders = responseWorkOrders.getWorkOrders();
                String logStr = "API MANAGER: getWorkOrders :: OK :: Size :" + workOrders.size();
                for (int i = 0; i < workOrders.size(); i++) {
                    WorkOrder workOrder = workOrders.get(i);
                    logStr += "\nWO #" + workOrder.getProposalPhase() + " " + workOrder.getDescription();
                }
                Log.d(TAG, logStr);

                //Save raw JSON for offline use
                sCurrentUser.getPrefsEditor().putString("work_order_data", responseWorkOrders.getRawJson());

                //Save new lastUpdated
                Date retrievedDate = new Date(System.currentTimeMillis());
                Log.i(TAG, "Saving new last_updated: " + retrievedDate.toString());
                CurrentUser.setLastUpdatedDate(retrievedDate);
                sCurrentUser.getPrefsEditor().putLong("last_updated", retrievedDate.getTime());
                sCurrentUser.getPrefsEditor().apply();
                SnackbarManager.show(Snackbar.with(self).text("Updated " + CurrentUser.getLastUpdatedDate().toString()).duration(Snackbar.SnackbarDuration.LENGTH_SHORT));

                //Save the workOrders
                sCurrentUser.setWorkOrders(workOrders);
                //stopRefreshAnimation();
                adapter.notifyDataSetInvalidated();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "API MANAGER: getWorkOrders :: ERROR :: " + error);
                //stopRefreshAnimation();
                SnackbarManager.show(Snackbar.with(self).text("Failed to retrieve work orders").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
            }
        });
    }*/

}