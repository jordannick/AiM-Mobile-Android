package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Gravity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.NavigationAdapter;
import edu.oregonstate.AiMLiteMobile.Constants;
import edu.oregonstate.AiMLiteMobile.Fragments.CustomSearchView;
import edu.oregonstate.AiMLiteMobile.Helpers.InternalStorageWriter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.NavigationDrawer;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseLastUpdated;
import edu.oregonstate.AiMLiteMobile.Network.ResponseNotices;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import edu.oregonstate.AiMLiteMobile.Helpers.NotificationManager;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OverviewListActivity extends AppCompatActivity implements WorkOrderAdapter.Callbacks, NavigationAdapter.NavigationClickHandler, CustomSearchView.Callbacks {
    private static final String TAG = "AiM_OverviewListACT";

    private static CurrentUser currentUser;
    private static NotificationManager notificationManager;
    private static NavigationDrawer navigationDrawer;
    private Activity activity;
    private MenuCompat menu;
    private OverviewListActivity self = this;

    private LinearLayoutManager linearLayoutManager;
    private WorkOrderAdapter recAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.overview_activity_shortcuts) LinearLayout sectionShortcutBar;
    @Bind(R.id.overview_activity_section_icon0) TextView sectionIcon0;
    @Bind(R.id.overview_activity_section_icon1) TextView sectionIcon1;
    @Bind(R.id.overview_activity_section_icon2) TextView sectionIcon2;
    @Bind(R.id.overview_activity_section_icon3) TextView sectionIcon3;
    @Bind(R.id.overviewActivity_dimOverlay) LinearLayout dimOverlay;

    @Bind(R.id.overview_activity_search_container) FrameLayout searchViewContainer;

    @Bind(R.id.left_drawer_recycler) RecyclerView recyclerViewDrawer;
    @Bind(R.id.right_drawer) RecyclerView recyclerViewDrawerNotification;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;

    private CustomSearchView customSearchView;

    /*INTERFACE*/
    @Override
    public void handleNavigationClick(int position) {
        Log.d(TAG, "HANDLE CLICK LISTENED : " + position);
        switch (position){
            case 1: //Search
                drawerLayout.closeDrawer(Gravity.LEFT);
                //searchMenuItem.expandActionView();
                enableSearchView();
                customSearchView.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;
            case 2: //Sort
                drawerLayout.closeDrawer(Gravity.LEFT);
                notificationManager.openDrawer(drawerLayout);
                break;
            case 3: //Refresh
                currentUser.setLastUpdated(0L);
                requestLastUpdated(true);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case 4: //Settings
                drawerLayout.closeDrawer(Gravity.LEFT);
                beginSettingsActivity();
                //Todo: create settings activity/dialog
                break;
            case 5: //Log Out
                drawerLayout.closeDrawer(Gravity.LEFT);
                currentUser.logoutUser(this);
                break;
        }
    }

    @Override
    public void onTextChangedFilter(String newText, final TextView countText) {
        recAdapter.getFilter().filter(newText);
        recAdapter.getFilter().filter(newText, new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                countText.setText(String.valueOf(recAdapter.getItemCount() - recAdapter.getNumSections()));
            }
        });


    }

    private void enableSearchView(){
        if(searchViewContainer.getChildCount() == 0) {
            customSearchView = new CustomSearchView(this, R.color.colorPrimaryDark, searchViewContainer, currentUser.getWorkOrders().size());
            customSearchView.attachView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_overview);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        activity = this;

        currentUser = CurrentUser.get(getApplicationContext());

        /*
        A quick cheese fix to get around the crashing if OverviewListActivity is destroyed and everything in CurrentUser is null.
        Just goes back to login screen and starts over.
        */
        if (currentUser.getUsername() == null){
            currentUser.forceLogout(this);
            return;
        }

        notificationManager = NotificationManager.get(this, recyclerViewDrawerNotification);
        navigationDrawer = new NavigationDrawer(this);


        initRecyclerView();
        initSectionIcons();

        if(getIntent().getBooleanExtra(LoginActivity.EXTRA_LOGIN, false)){
            Log.d(TAG, "Extra Login true");
            SnackbarManager.show(Snackbar.with(this).text("Logged in as " + currentUser.getUsername().toUpperCase()).duration(Snackbar.SnackbarDuration.LENGTH_LONG));
            requestWorkOrders(false);
            requestNotices();
        }else{
            Log.d(TAG, "Extra Login false");
        }
        if(getIntent().getBooleanExtra(LoginActivity.EXTRA_OFFLINE, false)){
            setupOfflineMode();
        }

        boolean hasSavedActions = InternalStorageWriter.hasSavedActions(getApplicationContext(), currentUser.getUsername());
        if (hasSavedActions) {
            currentUser.loadSavedActions();
            Log.d(TAG, "actions = " + currentUser.getActions());
        }
    }

    private void initRecyclerView(){
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recAdapter = new WorkOrderAdapter(currentUser.getWorkOrders(), this);
        recyclerView.setAdapter(recAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        });


    }


    private void setupOfflineMode(){




        boolean hasSavedData = InternalStorageWriter.hasSavedData(getApplicationContext(), currentUser.getUsername());


        if (hasSavedData) currentUser.loadSavedWorkOrders(recAdapter);

        Log.d(TAG, "username: "+currentUser.getUsername());
        Log.d(TAG, "password: "+currentUser.getPassword());
        Log.d(TAG, "token: "+currentUser.getToken());
        Log.d(TAG, "workorders: "+currentUser.getWorkOrders());
        Log.d(TAG, "actions: "+currentUser.getActions());


    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        Log.d(TAG, "onResume");
        if(currentUser.isRefreshIntervalExceeded()){
            Log.d(TAG, "RefreshInterval exceeded");
            requestLastUpdated(true);
        }else{
            Log.d(TAG, "RefreshInterval NOT exceeded. " + currentUser.getLastRefreshed());
        }

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "OverviewListActivity DESTROYED");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        currentUser.logoutUser(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(customSearchView != null){
            customSearchView.close();
        }
        recAdapter.flushFilter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);

        MenuItem menu_timeLog = menu.findItem(R.id.menu_time_log);
        menu_timeLog.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                beginActionQueueActivity();
                return true;
            }
        });

        View menu_notification = menu.findItem(R.id.menu_notification).getActionView();
        menu_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.openDrawer(drawerLayout);
            }
        });
        return true;
    }


    private void initSectionIcons() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");

        sectionIcon0.setTypeface(tf);
        sectionIcon1.setTypeface(tf);
        sectionIcon2.setTypeface(tf);
        sectionIcon3.setTypeface(tf);
        sectionIcon0.setText(R.string.icon_daily);
        sectionIcon1.setText(R.string.icon_backlog);
        sectionIcon2.setText(R.string.icon_admin);
        sectionIcon3.setText(R.string.icon_recentlyCompleted);

        setClickListener(sectionIcon0, Constants.SECTION_DAILY);
        setClickListener(sectionIcon1, Constants.SECTION_BACKLOG);
        setClickListener(sectionIcon2, Constants.SECTION_ADMIN);
        setClickListener(sectionIcon3, Constants.SECTION_COMPLETED);
    }


    private void setClickListener(TextView tv, final String section) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Look for the matching section title to scroll to (Section title positions may change during searches)
                for (WorkOrderListItem item : recAdapter.getWorkOrderListItems()) {
                    if (item.getType() == WorkOrderListItem.Type.SECTION) {
                        if (section.equals(item.getSectionTitle())) {
                            Log.e(TAG, "scroll to: " + recAdapter.getWorkOrderListItems().indexOf(item));
                            linearLayoutManager.scrollToPositionWithOffset(recAdapter.getWorkOrderListItems().indexOf(item), 0);
                        }
                    }
                }
            }
        });
    }

    // Start an instance of DetailActivity
    public void onWorkOrderSelected(WorkOrder workOrder) {
       // bottomSheetTest();

        //recAdapter.flushFilter();

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        //searchView.setQuery("", false);
    }


    private void beginActionQueueActivity() {
        Intent i = new Intent(this, ActionQueueListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    private void beginSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }


    private void requestLastUpdated(final boolean displayConfirmation) {
        ApiManager.getService().getLastUpdated(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseLastUpdated>() {
            @Override
            public void success(ResponseLastUpdated responseLastUpdated, Response response) {
                Log.d(TAG, "requestLastUpdated SUCCESS ::: " + responseLastUpdated.getDate());

                if (responseLastUpdated.isNullReturn()) {
                    if (currentUser.isUpdateExpired(System.currentTimeMillis())) { //If current time exceeds expirationDate of data, update
                        Log.d(TAG, "requestLastUpdated ::: expired, update");
                        requestWorkOrders(displayConfirmation);
                    } else {
                        //Update unneeded, wait for expiration
                        Log.d(TAG, "requestLastUpdated ::: Data hasn't expired. No Update");
                        currentUser.setLastRefreshed(System.currentTimeMillis());
                    }
                } else {
                    if (currentUser.isUpdateNeeded(responseLastUpdated.getDate().getTime())) { //If newer time was retrieved, update.
                        Log.d(TAG, "requestLastUpdated ::: newerTime, update");
                        requestWorkOrders(displayConfirmation);
                    } else {
                        Log.d(TAG, "requestLastUpdated ::: same time, no update needed");
                        currentUser.setLastRefreshed(System.currentTimeMillis());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "requestLastUpdated FAIL");
                SnackbarManager.show(Snackbar.with(activity).text("Failed to retrieve last updated").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
            }
        });
    }

    private void requestWorkOrders(final boolean displayConfirmation) {
        if(displayConfirmation){
            recyclerView.setVisibility(View.INVISIBLE);
            setDimVisibility(View.VISIBLE);
        }

        ApiManager.getService().getWorkOrders(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseWorkOrders>() {
            @Override
            public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                Log.d(TAG, "requestWorkOrders SUCCESS");
                //currentUser.getPreferences().saveWorkOrders(responseWorkOrders.getRawJson());
                currentUser.setWorkOrders(responseWorkOrders.getWorkOrders());
                currentUser.backupWorkOrders(responseWorkOrders.getWorkOrders());
                recAdapter.refreshWorkOrders(currentUser.getWorkOrders());
                recyclerView.setVisibility(View.VISIBLE);
                setDimVisibility(View.GONE);

                currentUser.setLastRefreshed(System.currentTimeMillis());
                currentUser.setLastUpdated(System.currentTimeMillis());

                if (displayConfirmation)
                    SnackbarManager.show(Snackbar.with(activity).text("Work Orders Updated").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failed requestWorkOrders\nError: " + error);
                SnackbarManager.show(Snackbar.with(activity).text("Failed to retrieve work orders").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                recyclerView.setVisibility(View.VISIBLE);
                setDimVisibility(View.GONE);

                //promptUserLoadOfflineData();
            }
        });
    }

    private void requestNotices() {
        ApiManager.getService().getNotices(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseNotices>() {
            @Override
            public void success(ResponseNotices responseNotices, Response response) {
                notificationManager.refreshNotices(responseNotices.getNotices());
                //if (menu != null) notificationManager.updateNoticeCount(menu);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failed requestNotices\nError: " + error);
            }
        });
    }

    private void setDimVisibility(int visibility) {
        dimOverlay.setVisibility(visibility);
    }

    private void promptUserLoadOfflineData(){


    }

}