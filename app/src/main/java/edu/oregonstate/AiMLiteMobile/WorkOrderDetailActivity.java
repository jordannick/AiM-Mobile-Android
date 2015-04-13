package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class WorkOrderDetailActivity extends Activity implements WorkOrderDetailFragment.Callbacks, WorkOrderNotesFragment.Callbacks {
    private static final String TAG = "WorkOrderDetailActivity";

    ActionBar.Tab Tab1, Tab2, Tab3;
    Fragment fragmentTab1 = new WorkOrderDetailFragment();
    Fragment fragmentTab2 = new WorkOrderNotesFragment();
    Fragment fragmentTab3 = new WorkOrderContactFragment();

    public UUID workOrderId;
    public WorkOrder mWorkOrder;

    private ActionBar actionBar;

    public void onWorkOrderUpdated() {

    }


    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_fragment);

        mWorkOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
       // workOrderId = (UUID)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_ID);
        //Log.d(TAG, "woID in detail activity: " + workOrderId);
        //mWorkOrder = CurrentUser.get(getApplicationContext()).getWorkOrder(workOrderId);
        //Log.d(TAG, "wo in detail activity: " + mWorkOrder);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab1 = actionBar.newTab().setText("Overview");
        Tab2 = actionBar.newTab().setText("Notes");
        Tab3 = actionBar.newTab().setText("Contact");

        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));
        Tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        if(savedInstanceState!=null){
            int currentTab = savedInstanceState.getInt("CurrentDetailsTab");
            actionBar.selectTab(actionBar.getTabAt(currentTab));
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentDetailsTab", actionBar.getSelectedTab().getPosition());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                Intent i = new Intent(this, WorkOrderAddActionActivity.class);

               // i.putExtra(WorkOrder.WORK_ORDER_ID, workOrderId);
                i.putExtra(WorkOrder.WORK_ORDER_EXTRA, mWorkOrder);
                startActivity(i);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);

    }
}
