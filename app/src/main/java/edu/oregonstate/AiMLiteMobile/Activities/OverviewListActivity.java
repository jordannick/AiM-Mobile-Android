package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseNotices;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.RecyWorkOrderAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OverviewListActivity extends AppCompatActivity implements RecyWorkOrderAdapter.Callbacks {
    private static final String TAG = "OverviewListActivity";
    private static CurrentUser currentUser;
    private Activity activity;

    private LinearLayoutManager linearLayoutManager;
    private RecyWorkOrderAdapter recAdapter;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.overview_activity_section_icon0) TextView sectionIcon0;
    @Bind(R.id.overview_activity_section_icon1) TextView sectionIcon1;
    @Bind(R.id.overview_activity_section_icon2) TextView sectionIcon2;
    @Bind(R.id.overview_activity_section_icon3) TextView sectionIcon3;
    @Bind(R.id.overviewActivity_dimOverlay) LinearLayout dimOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        currentUser = CurrentUser.get(getApplicationContext());
        setContentView(R.layout.activity_overview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recAdapter = new RecyWorkOrderAdapter(currentUser.getWorkOrders(), this);
        recyclerView.setAdapter(recAdapter);



        initSectionIcons();

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
    public void onBackPressed() {
        currentUser.logoutUser(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);
        return true;
    }


    private void initSectionIcons() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");

        sectionIcon0.setTypeface(tf); sectionIcon1.setTypeface(tf); sectionIcon2.setTypeface(tf); sectionIcon3.setTypeface(tf);
        sectionIcon0.setText(R.string.icon_daily); sectionIcon1.setText(R.string.icon_backlog); sectionIcon2.setText(R.string.icon_admin); sectionIcon3.setText(R.string.icon_recentlyCompleted);

        setClickListener(sectionIcon0, recAdapter.wrapper.getSectionIndex(WorkOrder.DAILY_SECTION_ID));
        setClickListener(sectionIcon1, recAdapter.wrapper.getSectionIndex(WorkOrder.BACKLOG_SECTION_ID));
        setClickListener(sectionIcon2, recAdapter.wrapper.getSectionIndex(WorkOrder.ADMIN_SECTION_ID));
        setClickListener(sectionIcon3, recAdapter.wrapper.getSectionIndex(WorkOrder.RECENTLY_COMPLETED_SECTION_ID));
    }

    private void setClickListener(TextView tv, final int position) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recyclerView.smoothScrollToPosition(position);
                //recyclerView.getLayoutManager().scrollToPosition(position);
                linearLayoutManager.scrollToPositionWithOffset(position, 0);
            }
        });
    }

    // Start an instance of DetailActivity
    public void onWorkOrderSelected(WorkOrder workOrder){
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    private void createNoticesViewPopup() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog_notes_list, null);

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

    public void beginActionQueueActivity() {
        Intent i = new Intent(this, ActionQueueListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notification:
                createNoticesViewPopup();
                break;
            case R.id.action_queue:
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

    private void requestLastUpdated() {

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

    private void requestWorkOrders() {

        ApiManager.getService().getWorkOrders(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseWorkOrders>() {
            @Override
            public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                Log.d(TAG, "requestWorkOrders SUCCESS");
                ArrayList<WorkOrder> workOrders = responseWorkOrders.getWorkOrders();
                currentUser.getPreferences().saveWorkOrders(responseWorkOrders.getRawJson());
                currentUser.setWorkOrders(workOrders);
                //adapter.refreshWorkOrders(currentUser.getWorkOrders());
                recAdapter.refreshWorkOrders(currentUser.getWorkOrders());
                initSectionIcons();
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

    private void requestNotices() {

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

    private void setDimVisibility(int visibility) {
        dimOverlay.setVisibility(visibility);
    }

}