package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionFragment;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 2/17/2015.
 */
public class AddActionActivity extends SingleFragmentActivity{
    private static final String TAG = "AddActionActivity";

    private static CurrentUser sCurrentUser;
    private WorkOrder mWorkOrder;
    private Action mAction;
    private boolean editMode = false; //False = add new action from scratch //True = edit existing action
    private static AddActionFragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        sCurrentUser = CurrentUser.get(getApplicationContext());
        Intent intent = getIntent();

        if(intent.hasExtra(WorkOrder.WORK_ORDER_EXTRA)){ //Add mode
            mWorkOrder = (WorkOrder) intent.getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
            setTitle(R.string.add_action_activity_title);
            Log.d(TAG, "has extra WorkOrder");
        } else if (intent.hasExtra(Action.EDIT_ACTION_EXTRA)){ //Edit mode
            editMode = true;
            int actionPosition = intent.getIntExtra(Action.EDIT_ACTION_EXTRA, 0);
            mAction = sCurrentUser.getAction(actionPosition);
            setTitle(R.string.edit_action_activity_title);
            Log.d(TAG, "has extra EditAction");
        } else { //Neither mode, get out of here!
            finish();
        }

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        super.onCreate(savedInstanceState);
    }



    @Override
    protected Fragment createFragment() {
        Fragment newFragment = new AddActionFragment();
        Bundle bundle = new Bundle();

        //Send which mode we're in to the fragment
        bundle.putBoolean("editMode", editMode);
        newFragment.setArguments(bundle);
        Log.d(TAG, "ACTIVITY editMode = " + editMode);
        mFragment = (AddActionFragment) newFragment;
        return newFragment;
    }

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public Action getAction(){ return mAction; }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.no_action, R.anim.slide_out_bottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_queue:

                if (editMode){
                    if (mFragment.saveEdits()) mFragment.createConfirmDialog();
                } else {
                    if (mFragment.validateAction()) mFragment.createConfirmDialog();
                }

                return true;
            case android.R.id.home:
                mFragment.clear();
                finish();
                //overridePendingTransition(R.anim.no_action, R.anim.slide_out_bottom);
                return false;
            case R.id.log_out:
                sCurrentUser.prepareLogout();
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }
}
