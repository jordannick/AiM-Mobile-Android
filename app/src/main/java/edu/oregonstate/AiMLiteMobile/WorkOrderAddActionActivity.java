package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

/**
 * Created by sellersk on 2/17/2015.
 */
public class WorkOrderAddActionActivity extends Activity{
    private static final String TAG = "WorkOrderAddActionActivity";

    private static CurrentUser sCurrentUser;
    private WorkOrder mWorkOrder;
    private Action mAction;
    private boolean editMode = false; //False = add new action from scratch //True = edit existing action

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public Action getAction(){ return mAction; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sCurrentUser = CurrentUser.get(getApplicationContext());
        Intent intent = getIntent();

        if(intent.hasExtra(WorkOrder.WORK_ORDER_EXTRA)){ //Add mode
            mWorkOrder = (WorkOrder) intent.getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        } else if (intent.hasExtra(Action.EDIT_ACTION_EXTRA)){ //Edit mode
            editMode = true;
            int actionPosition = intent.getIntExtra(Action.EDIT_ACTION_EXTRA, 0);
            mAction = sCurrentUser.getAction(actionPosition);
        } else { //Neither mode, get out of here!
            finish();
        }

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        setTitle(R.string.add_action_activity_title);


        setContentView(getLayoutResId());
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

    }


    protected Fragment createFragment() {
        Fragment newFragment = new WorkOrderAddActionFragment();
        Bundle bundle = new Bundle();
        //Send which mode we're in to the fragment
        bundle.putBoolean("editMode", editMode);
        newFragment.setArguments(bundle);

        return newFragment;
    }


    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_action, menu);
        return true;
    }


}
