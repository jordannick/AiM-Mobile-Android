package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class OverviewListActivity extends Activity implements OverviewListFragment.Callbacks, DetailMainFragment.Callbacks {
    private static final String TAG = "OverviewListActivity";

    private static CurrentUser sCurrentUser;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        sCurrentUser = CurrentUser.get(getApplicationContext());

        ActionBar.Tab Tab1, Tab2;
        Fragment fragmentTab1 = new OverviewListFragment();
        Fragment fragmentTab2 = new OverviewListFragment();

        //Show the number of work orders in each section
        int num_daily = 0;
        int num_backlog = 0;
        for (WorkOrder wo : sCurrentUser.getWorkOrders()){
            if (wo.getSection().equals("Daily")){
                num_daily += 1;
            } else if (wo.getSection().equals("Backlog")){
                num_backlog += 1;
            }
        }

        Tab1 = actionBar.newTab().setText("Daily ("+num_daily+")");
        Tab2 = actionBar.newTab().setText("Backlog ("+num_backlog+")");

        Bundle bundle1 = new Bundle();
        bundle1.putString("sectionFilter", "Daily");

        Bundle bundle2 = new Bundle();
        bundle2.putString("sectionFilter", "Backlog");

        fragmentTab1.setArguments(bundle1);
        fragmentTab2.setArguments(bundle2);

        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        if(savedInstanceState!=null){
            int currentTab = savedInstanceState.getInt("CurrentSectionTab");
            actionBar.selectTab(actionBar.getTabAt(currentTab));
        }

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
        outState.putInt("CurrentSectionTab", actionBar.getSelectedTab().getPosition());

    }
/*
    public void onWorkOrderUpdated() {
        FragmentManager fm = getFragmentManager();
        OverviewListFragment listFragment = (OverviewListFragment)
                fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }
*/

    public void onWorkOrderSelected(WorkOrder workOrder){
        if (findViewById(R.id.detailFragmentContainer) == null) {
        // Start an instance of DetailActivity
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(WorkOrder.WORK_ORDER_EXTRA, workOrder);
            startActivity(i);
        } else { //Useful for later if implement swipe to change work order while in detail view
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = DetailMainFragment.newInstance(workOrder);
            if (oldDetail != null) {
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }

    }


    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you done?");

        // Add the buttons
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Logout stuff
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_queue){
            Intent i = new Intent(this, ActionQueueListActivity.class);
            //i.putExtra(DetailMainFragment.WORK_ORDER_ID, wo.getId());
            startActivity(i);
        }
       /* else if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }



    //Makes new request, if refresh needed, CurrentUser workorders will repopulate, and displayed list updated
    /*private void updateWorkOrderList(){

        CurrentUser currentUser = CurrentUser.get(getApplicationContext());

        TaskGetWorkOrders task = new TaskGetWorkOrders(this, currentUser, this, false);
        task.execute();
        onWorkOrderUpdated();

    }*/





}
