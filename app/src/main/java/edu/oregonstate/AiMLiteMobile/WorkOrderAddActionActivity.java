package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;


import java.util.UUID;

/**
 * Created by sellersk on 2/17/2015.
 */
public class WorkOrderAddActionActivity extends SingleFragmentActivity {
    private static final String TAG = "WorkOrderAddActionActivity";

    private WorkOrder mWorkOrder;
    private Action mAction;
    private boolean editMode = false;

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public Action getAction(){ return mAction; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra(WorkOrder.WORK_ORDER_EXTRA)){
            //ADD mode
            Log.d(TAG, "has extra work order");
            mWorkOrder = (WorkOrder) intent.getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        } else if (intent.hasExtra(Action.EDIT_ACTION_EXTRA)){
            //EDIT mode
            Log.d(TAG, "has extra edit action");
            editMode = true;
            mAction = (Action) intent.getSerializableExtra(Action.EDIT_ACTION_EXTRA);
            Log.d(TAG, "The action has work order: "+mAction.getWorkOrder().getDescription());
        } else {
            //Nothing
            finish();
        }



        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        setTitle(R.string.add_action_activity_title);

        if(savedInstanceState!=null){
            //Restore saved state
        }

        //Work in progress...
        /*
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        WorkOrderAddActionFragment newFragment = new WorkOrderAddActionFragment();
        Bundle bundle = new Bundle();
        Log.d(TAG, "activity edit mode to place in bundle:"+editMode );
        bundle.putBoolean("editMode", editMode);
        newFragment.setArguments(bundle);
        //fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.add(R.layout.activity_fragment, newFragment);
        fragmentTransaction.commit();
        */
    }

    @Override
    protected Fragment createFragment() {
        Fragment newFragment = new WorkOrderAddActionFragment();
        Bundle bundle = new Bundle();
        Log.d(TAG, "activity edit mode to place in bundle:"+editMode );
        bundle.putBoolean("editMode", editMode);
        newFragment.setArguments(bundle);

        return newFragment;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_action, menu);
        return true;
    }

}
