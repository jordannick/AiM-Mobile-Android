package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.oregonstate.AiMLiteMobile.Adapters.NavigationAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;

/**
 * Created by sellersk on 7/6/2015.
 */
public class NavigationDrawer {

    private AppCompatActivity delegate;
    private CurrentUser currentUser;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    public NavigationDrawer(AppCompatActivity delegate) {
        this.delegate = delegate;
        currentUser = CurrentUser.get(delegate);
        drawerLayout = (DrawerLayout)delegate.findViewById(R.id.drawer_layout);
        toolbar = (Toolbar)delegate.findViewById(R.id.toolbar);
        initNavigationDrawer();
    }


    private void initNavigationDrawer(){
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(delegate, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
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
        ActionBar actionBar = delegate.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        LinearLayoutManager linearLayoutManagerDrawer = new LinearLayoutManager(delegate);
        RecyclerView recyclerViewDrawer = (RecyclerView)delegate.findViewById(R.id.left_drawer);
        recyclerViewDrawer.setLayoutManager(linearLayoutManagerDrawer);
        String[] navTitles = new String[4];
        int[] icons = new int[4];
/*
        navTitles[0] = "Overview";
        icons[0] = R.string.icon_home;*/
        navTitles[0] = "Time Log";
        icons[0] = R.string.icon_timeLog;
        navTitles[1] = "Notices";
        icons[1] = R.string.icon_notices;
        navTitles[2] = "Settings";
        icons[2] = R.string.icon_settings;
        navTitles[3] = "Log out";
        icons[3] = R.string.icon_logout;

        Typeface iconTypeface = Typeface.createFromAsset(delegate.getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        NavigationAdapter adapter = new NavigationAdapter(delegate, navTitles, icons, currentUser.getUsername().toUpperCase(), iconTypeface);
        recyclerViewDrawer.setAdapter(adapter);


    }
}
