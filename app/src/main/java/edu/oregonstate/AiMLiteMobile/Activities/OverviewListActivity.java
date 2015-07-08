package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.NavigationAdapter;
import edu.oregonstate.AiMLiteMobile.InternalStorageWriter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.NavigationDrawer;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseNotices;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import edu.oregonstate.AiMLiteMobile.NotificationManager;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Adapters.RecyWorkOrderAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OverviewListActivity extends AppCompatActivity implements RecyWorkOrderAdapter.Callbacks, NavigationAdapter.NavigationClickHandler {
    private static final String TAG = "AiM_OverviewListACT";

    private static CurrentUser currentUser;
    private static NotificationManager notificationManager;
    private static NavigationDrawer navigationDrawer;
    private Activity activity;
    private Menu menu;

    private LinearLayoutManager linearLayoutManager;
    private RecyWorkOrderAdapter recAdapter;
    private SearchView searchView;

    private int screenWidth;
    private int screenHeight;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.overview_activity_shortcuts)
    LinearLayout sectionShortcutBar;
    @Bind(R.id.overview_activity_section_icon0)
    TextView sectionIcon0;
    @Bind(R.id.overview_activity_section_icon1)
    TextView sectionIcon1;
    @Bind(R.id.overview_activity_section_icon2)
    TextView sectionIcon2;
    @Bind(R.id.overview_activity_section_icon3)
    TextView sectionIcon3;
    @Bind(R.id.overviewActivity_dimOverlay)
    LinearLayout dimOverlay;

    @Bind(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;
    @Bind(R.id.left_drawer_recycler)
    RecyclerView recyclerViewDrawer;
    @Bind(R.id.right_drawer)
    RecyclerView recyclerViewDrawerNotification;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;



    @Override
    public void handleNavigationClick(int position) {
        Log.d(TAG, "HANDLE CLICK LISTENED : " + position);
        switch (position){
            case 1: //Time Log
                drawerLayout.closeDrawer(Gravity.LEFT);
                beginActionQueueActivity();
                break;
            case 2: //Notices
                drawerLayout.closeDrawer(Gravity.LEFT);
                notificationManager.openDrawer(drawerLayout);
                break;
            case 3: //Refresh
                break;
            case 4: //Settings
                //Todo: create settings activity/dialog
                InternalStorageWriter writer = new InternalStorageWriter(this, "testFilename");
                writer.writeToFile("Hello World!");
                writer.printFileContents();
                break;
            case 5: //Log Out
                drawerLayout.closeDrawer(Gravity.LEFT);
                currentUser.logoutUser(this);
                break;
        }


    }

    private TextView notifBox = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_overview);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Log.d(TAG, "oncreate!");

        activity = this;

        currentUser = CurrentUser.get(getApplicationContext());
        notificationManager = NotificationManager.get(this, recyclerViewDrawerNotification);
        navigationDrawer = new NavigationDrawer(this);


        SnackbarManager.show(Snackbar.with(this).text("Logged in as " + currentUser.getUsername().toUpperCase()).duration(Snackbar.SnackbarDuration.LENGTH_LONG));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recAdapter = new RecyWorkOrderAdapter(currentUser.getWorkOrders(), this);
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


        initSectionIcons();

        requestWorkOrders();
        requestNotices();

        //initNavigationDrawer();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

    }

    private void bottomSheetTest(){
        //bottomSheet.showWithSheetView(LayoutInflater.from(this).inflate(R.layout.activity_detail, bottomSheet, false));
        View v = LayoutInflater.from(this).inflate(R.layout.bottomsheet_detail, bottomSheet, false);
        //bottomSheet.addView(v, 100, 100);


        //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(.8*screenHeight));
        //bottomSheet.addView(v, 0, layoutParams);
        //v.setLayoutParams(layoutParams);
         //bottomSheet.sho
        bottomSheet.showWithSheetView(v);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "aa OverviewListActivity onDestroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(bottomSheet.isSheetShowing()){
            bottomSheet.dismissSheet();
        }else {
            currentUser.logoutUser(this);
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "aa On Stop Overview");
        super.onStop();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "aa On Save OverView");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);
        this.menu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    sectionShortcutBar.setVisibility(View.VISIBLE);
                    recAdapter.getFilter().filter("");
                } else { //Start filtering the RecycleView when chars are entered
                    sectionShortcutBar.setVisibility(View.GONE);
                    recAdapter.getFilter().filter(newText.toString());
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recAdapter.flushFilter(); //Bring back the full list when search is closed
                sectionShortcutBar.setVisibility(View.VISIBLE);
                return false;
            }
        });


        View menu_notification = menu.findItem(R.id.menu_notification).getActionView();
        menu_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createNoticesViewPopup();
                //drawerLayout.openDrawer(GravityCompat.END);
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

        setClickListener(sectionIcon0, "Daily");
        setClickListener(sectionIcon1, "Backlog");
        setClickListener(sectionIcon2, "Admin");
        setClickListener(sectionIcon3, "Recently Completed");
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
        searchView.setQuery("", false);
    }


    public void beginActionQueueActivity() {
        Intent i = new Intent(this, ActionQueueListActivity.class);

        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        searchView.setQuery("", false);
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
                activity.findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
                setDimVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "requestWorkOrders FAIL");
                SnackbarManager.show(Snackbar.with(activity).text("Failed to retrieve work orders").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                activity.findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
                setDimVisibility(View.GONE);
            }
        });
    }

    private void requestNotices() {

        ApiManager.getService().getNotices(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseNotices>() {
            @Override
            public void success(ResponseNotices responseNotices, Response response) {
                notificationManager.refreshNotices(responseNotices.getNotices());
                if (menu != null) notificationManager.updateNoticeCount(menu);
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