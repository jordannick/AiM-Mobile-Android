package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.NavigationAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Euler;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseNotices;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Adapters.RecyWorkOrderAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OverviewListActivity extends AppCompatActivity implements RecyWorkOrderAdapter.Callbacks, NavigationAdapter.NavigationClickHandler {
    private static final String TAG = "AiM_OverviewListACT";
    private static CurrentUser currentUser;
    private Activity activity;

    private LinearLayoutManager linearLayoutManager;
    private RecyWorkOrderAdapter recAdapter;

    private int screenWidth;
    private int screenHeight;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
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


    @Override
    public void handleClick(int position) {
        Log.d(TAG, "HANDLE CLICK LISTENED : " + position);
    }

    private TextView notifBox = null;


/*    @Bind(R.id.left_drawer) RecyclerView recyclerViewDrawer;*/
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        activity = this;
        currentUser = CurrentUser.get(getApplicationContext());
        setContentView(R.layout.activity_overview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

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


        initNavigationDrawer();


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


        //int number = 999 * 999;
        //Euler.listAllPalindromes(number);

        Euler.findSumAmicableNumbers(10000);
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

    private void initNavigationDrawer(){
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActionBar().setTitle();

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        LinearLayoutManager linearLayoutManagerDrawer = new LinearLayoutManager(this);
        RecyclerView recyclerViewDrawer = (RecyclerView)findViewById(R.id.left_drawer);
        recyclerViewDrawer.setLayoutManager(linearLayoutManagerDrawer);
        String[] navTitles = new String[4];
        int[] icons = new int[4];

        navTitles[0] = "Notices";
        icons[0] = R.string.icon_notices;
        navTitles[1] = "Time Log";
        icons[1] = R.string.icon_timeLog;
        navTitles[2] = "Settings";
        icons[2] = R.string.icon_settings;
        navTitles[3] = "Log out";
        icons[3] = R.string.icon_logout;



        Typeface iconTypeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        NavigationAdapter adapter = new NavigationAdapter(this, navTitles, icons, currentUser.getUsername().toUpperCase(), iconTypeface);
        recyclerViewDrawer.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "OverviewListActivity onDestroy");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "text changed");
                if (TextUtils.isEmpty(newText)) {
                    recAdapter.getFilter().filter("");
                } else {
                    recAdapter.getFilter().filter(newText.toString());
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recAdapter.flushFilter();
                return false;
            }
        });


        final View menu_notification = menu.findItem(R.id.menu_notification).getActionView();
        notifBox = (TextView) menu_notification.findViewById(R.id.hotlist_hot);

        setNotifCount(currentUser.getNotices().size());

        menu_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNoticesViewPopup();
            }
        });

        return true;
    }

    private void setNotifCount(int count) {
        //notifBox.setText(String.valueOf(count));
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
                //linearLayoutManager.scrollToPositionWithOffset(position, 0);

                // Look for the matching section title to scroll to
                for (WorkOrderListItem item : recAdapter.getWorkOrderListItems()) {
                    if (item.getType() == WorkOrderListItem.Type.SECTION) {
                        if (section.equals(item.getSectionTitle())) {
                            linearLayoutManager.scrollToPositionWithOffset(recAdapter.getWorkOrderListItems().indexOf(item), 0);
                        }
                    }
                }
            }
        });
    }

    // Start an instance of DetailActivity
    public void onWorkOrderSelected(WorkOrder workOrder) {
        //recAdapter.flushFilter();


        bottomSheetTest();

        /*Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        searchView.setQuery("", false);*/
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
        searchView.setQuery("", false);
    }
/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notification:

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
*/


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
                ArrayList<Notice> notices = responseNotices.getNotices();
                currentUser.setNotices(notices);
                setNotifCount(currentUser.getNotices().size());
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