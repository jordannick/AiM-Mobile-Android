package edu.oregonstate.AiMLiteMobile;

import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import edu.oregonstate.AiMLiteMobile.Adapters.NavigationAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;

/**
 * Created by sellersk on 7/6/2015.
 */
public class NavigationDrawer {
    public static final String TAG = "NavigationDrawer";

    private AppCompatActivity delegate;
    private static CurrentUser currentUser;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Typeface iconTypeface;
    private NavigationAdapter adapter;

    private final int ORIENTATION_LANSCAPE = 2;
    private final int ORIENTATION_PORTRAIT = 1;
    private int orientation;


    public NavigationDrawer(AppCompatActivity delegate) {
        this.delegate = delegate;
        currentUser = CurrentUser.get(delegate);
        drawerLayout = (DrawerLayout) delegate.findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) delegate.findViewById(R.id.toolbar);
        orientation = delegate.getResources().getConfiguration().orientation;
        initNavigationDrawer();
        initFooterViews();
    }

    private void initNavigationDrawer() {

        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(delegate, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if(newState == DrawerLayout.STATE_SETTLING && !drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    //Drawer is opening
                    adapter.refreshHeaderLastUpdated();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActionBar().setTitle();
                //((OverviewListActivity)delegate).beginActionQueueActivity();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        ActionBar actionBar = delegate.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        LinearLayoutManager linearLayoutManagerDrawer = new LinearLayoutManager(delegate);
        RecyclerView recyclerViewDrawer = (RecyclerView) delegate.findViewById(R.id.left_drawer_recycler);
        recyclerViewDrawer.setLayoutManager(linearLayoutManagerDrawer);
        String[] navTitles = new String[5];
        int[] icons = new int[5];

        navTitles[0] = "Search";
        icons[0] = R.string.icon_search;
        navTitles[1] = "Sort";
        icons[1] = R.string.icon_sort;
        navTitles[2] = "Force Refresh";
        icons[2] = R.string.icon_refresh;
        navTitles[3] = "Settings";
        icons[3] = R.string.icon_settings;
        navTitles[4] = "Log out";
        icons[4] = R.string.icon_logout;

        if(orientation == ORIENTATION_PORTRAIT){
            navTitles = new String[]{navTitles[0], navTitles[1]};
            icons = new int[]{icons[0], icons[1]};
        }

        iconTypeface = Typeface.createFromAsset(delegate.getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        Log.d(TAG, "delegate = " + delegate);
        Log.d(TAG, "currentUser = " + currentUser + " ; username = " + currentUser.getUsername() + " ; workorders = " + currentUser.getWorkOrders());
        adapter = new NavigationAdapter(delegate, navTitles, icons, currentUser.getUsername().toUpperCase(), iconTypeface);
        recyclerViewDrawer.setAdapter(adapter);

    }

    private void initFooterViews() {
        if(orientation == ORIENTATION_PORTRAIT) {
            TextView refreshIcon = (TextView) delegate.findViewById(R.id.nav_footer_refresh_icon);
            TextView refreshTitle = (TextView) delegate.findViewById(R.id.nav_footer_refresh_title);
            TextView settingsIcon = (TextView) delegate.findViewById(R.id.nav_footer_settings_icon);
            TextView settingsTitle = (TextView) delegate.findViewById(R.id.nav_footer_settings_title);
            TextView logoutIcon = (TextView) delegate.findViewById(R.id.nav_footer_logout_icon);
            TextView logoutTitle = (TextView) delegate.findViewById(R.id.nav_footer_logout_title);

            refreshIcon.setTypeface(iconTypeface);
            refreshIcon.setText(R.string.icon_refresh);
            settingsIcon.setTypeface(iconTypeface);
            settingsIcon.setText(R.string.icon_settings);
            logoutIcon.setTypeface(iconTypeface);
            logoutIcon.setText(R.string.icon_logout);
            refreshTitle.setText("Force Refresh");
            settingsTitle.setText("Settings");
            logoutTitle.setText("Logout");
        }
    }

    public void invalidateAdapter() {
        adapter.notifyDataSetChanged();
    }
}
