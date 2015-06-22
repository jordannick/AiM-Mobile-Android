package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseNotices;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import edu.oregonstate.AiMLiteMobile.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OverviewListActivity extends Activity {
    private static final String TAG = "OverviewListActivity";
    private static CurrentUser currentUser;
    private ListView listView;
    private WorkOrderAdapter adapter;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.overview_activity);
        currentUser = CurrentUser.get(getApplicationContext());

        initListView();
        initSectionIcons();ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        SnackbarManager.show(Snackbar.with(this).text("Logged in as " + currentUser.getUsername().toUpperCase()).duration(Snackbar.SnackbarDuration.LENGTH_LONG));

        //requestLastUpdated();
        requestWorkOrders();
        requestNotices();

    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        currentUser.logoutUser(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);
        return true;
    }

    private void initListView() {
        listView = (ListView)findViewById(R.id.overview_activity_listView);
        adapter = new WorkOrderAdapter(this, currentUser.getWorkOrders());
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkOrderListItem item = adapter.getItem(position);
                if (item.getType() == WorkOrderListItem.Type.ITEM) {
                    onWorkOrderSelected(item.getWorkOrder());
                }
            }
        });
    }

    private void initSectionIcons(){
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

    // Start an instance of DetailActivity
    public void onWorkOrderSelected(WorkOrder workOrder){
        //Intent i = new Intent(this, TestActivity.class);
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
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
        NoticeAdapter noticesAdapter = new NoticeAdapter(this, currentUser.getNotices());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_notification:
                createNoticesViewPopup();
                break;
            case  R.id.action_queue:
                beginActionQueueActivity();
                break;
            case R.id.log_out:
                currentUser.logoutUser(this);
                break;
            case R.id.force_refresh:
                //updateWorkOrderList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestLastUpdated(){

        ApiManager.getService().getLastUpdated(currentUser.getUsername(), currentUser.getToken(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "requestLastUpdated SUCCESS");
                //TODO: check retrieved last updated against stored last updated
                requestWorkOrders();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "requestLastUpdated FAIL");
                SnackbarManager.show(Snackbar.with(activity).text("Failed to retrieve last updated").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
            }
        });
    }

    private void requestWorkOrders(){

        ApiManager.getService().getWorkOrders(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseWorkOrders>() {
            @Override
            public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                Log.d(TAG, "requestWorkOrders SUCCESS");
                ArrayList<WorkOrder> workOrders = responseWorkOrders.getWorkOrders();
                currentUser.getPreferences().saveWorkOrders(responseWorkOrders.getRawJson());
                currentUser.setWorkOrders(workOrders);
                adapter.refreshWorkOrders(currentUser.getWorkOrders());
                setDimVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "requestWorkOrders FAIL");
                SnackbarManager.show(Snackbar.with(activity).text("Failed to retrieve work orders").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                setDimVisibility(View.GONE);
            }
        });
    }

    private void requestNotices(){

        ApiManager.getService().getNotices(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseNotices>() {
            @Override
            public void success(ResponseNotices responseNotices, Response response) {
                ArrayList<Notice> notices = responseNotices.getNotices();
                currentUser.setNotices(notices);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setDimVisibility(int visibility){
        activity.findViewById(R.id.overviewActivity_dimOverlay).setVisibility(visibility);
    }

}