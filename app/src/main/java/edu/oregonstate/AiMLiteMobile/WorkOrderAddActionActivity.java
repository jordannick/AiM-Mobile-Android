package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


import java.util.UUID;

/**
 * Created by sellersk on 2/17/2015.
 */
public class WorkOrderAddActionActivity extends SingleFragmentActivity{
    private static final String TAG = "WorkOrderActionActivity";

    Fragment fragment;

    private ActionBar mActionBar;
    public WorkOrder mWorkOrder;
    public UUID mWorkOrderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if(intent.hasExtra(WorkOrder.WORK_ORDER_EXTRA)){
            //mWorkOrderId = (UUID)intent.getSerializableExtra(WorkOrder.WORK_ORDER_ID);
           /* mWorkOrderId = (String)intent.getSerializableExtra(WorkOrder.WORK_ORDER_ID);
            if(mWorkOrderId != null){
                //Has workOrderId, load Add Action Tab with selected WorkOrder
                mWorkOrder = CurrentUser.get(getApplicationContext()).getWorkOrder(mWorkOrderId);
            }*/
            mWorkOrder = (WorkOrder) intent.getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        }



        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
      /*  Intent intent = getIntent();
        if(intent.hasExtra(WorkOrder.WORK_ORDER_EXTRA)){

            mWorkOrder = (WorkOrder) intent.getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        }

*/
        fragment = new WorkOrderAddActionFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        setTitle(R.string.add_action_activity_title);

        if(savedInstanceState!=null){
            //Restore saved state
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected in Activity");
        switch (item.getItemId()){
            case R.id.action_queue:
                //TODO: form validation
                //Handled by hosted fragment
                return false;
                //break;
            case android.R.id.home:
                this.finish(); //Add confirm to save info before finishing activity
                return true;
        }

        return false;
    }

    public void onCheckBoxClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();
        findViewById(R.id.spinner_updateStatus).setClickable(checked);
        findViewById(R.id.spinner_updateStatus).setEnabled(checked);

        //TODO 3/17/15: return spinner to original item if unchecked.
    }


    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public UUID getWorkOrderId() {
        return mWorkOrderId;
    }
}
