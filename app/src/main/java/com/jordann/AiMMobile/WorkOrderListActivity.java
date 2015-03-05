package com.jordann.AiMMobile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class WorkOrderListActivity extends SingleFragmentActivity implements WorkOrderListFragment.Callbacks, WorkOrderDetailFragment.Callbacks, GetWorkOrdersTask.OnTaskCompleted {

    private static final String TAG = "WorkOrderListActivity";

    @Override
    protected Fragment createFragment() {

        return new WorkOrderListFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }


    public void onWorkOrderUpdated(WorkOrder wo) {
        FragmentManager fm = getFragmentManager();
        WorkOrderListFragment listFragment = (WorkOrderListFragment)
                fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }


    public void onWorkOrderSelected(WorkOrder wo){
        if (findViewById(R.id.detailFragmentContainer) == null) {
        // Start an instance of WorkOrderDetailActivity
            Intent i = new Intent(this, WorkOrderDetailActivity.class);
            i.putExtra(WorkOrderDetailFragment.WORK_ORDER_ID, wo.getId());
            Log.d(TAG, "wo: "+wo);
            Log.d(TAG, "woID sent: "+wo.getId());
            startActivity(i);
        } else { //Useful for later if implement swipe to change work order while in detail view
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = WorkOrderDetailFragment.newInstance(wo.getId());
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
            Intent i = new Intent(this, WorkOrderActionQueueListActivity.class);
            //i.putExtra(WorkOrderDetailFragment.WORK_ORDER_ID, wo.getId());
            startActivity(i);
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        updateWorkOrderList();


    }


    private void updateWorkOrderList(){
        Log.d(TAG, "updateWorkOrderList");
        //Check if refresh is needed
        CurrentUser user = CurrentUser.get(getApplicationContext());
       // if(user.isRefreshNeeded()){
            Log.d(TAG, "refresh is needed, do new task execute");
            GetWorkOrdersTask task = new GetWorkOrdersTask(this, user.getURLGetAll(), user.getURLGetLastUpdated(), user, this);
            task.execute();
       // }
    }

    @Override
    public void onTaskSuccess() {
        Log.d(TAG, "list activity task success");
    }

    @Override
    public void onNetworkFail() {
        Log.d(TAG, "list activity net fail");
    }

    @Override
    public void onAuthenticateFail() {
        Log.d(TAG, "list activity auth fail");
    }
}
