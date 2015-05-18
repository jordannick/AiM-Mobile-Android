package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Fragments.ActionQueueListFragment;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Network.TaskPostAction;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionQueueListActivity extends SingleFragmentActivity implements ActionQueueListFragment.Callbacks, TaskPostAction.OnTaskCompleted {
    private static final String TAG = "ActionQueueActivity";
    private static CurrentUser sCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        setTitle(R.string.action_queue_activity_title);
        sCurrentUser = CurrentUser.get(getApplicationContext());

        if(savedInstanceState!=null){
            //Restore saved state
        }
    }

    @Override
    protected Fragment createFragment() {
        return new ActionQueueListFragment();
    }

    //Open edit action form for clicked action, with position of action in queue
    public void onActionSelected(int actionPosition){
        Intent i = new Intent(this, AddActionActivity.class);
        i.putExtra(Action.EDIT_ACTION_EXTRA, actionPosition);
        Log.d(TAG, "put edit action extra");
        startActivity(i);
    }

    //Start the HTTP POSTs to submit actions from queue.
    //Remove from queue upon successful POST
    //TODO put in real url
    public void syncActions(){
        final TaskPostAction task = new TaskPostAction(this, "testurl",getApplicationContext());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    task.execute(sCurrentUser.getAction(0));
                } catch (Exception e) {
                    Log.e(TAG, "Exception e: " + e);
                }

            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
           case R.id.action_sync:
                syncActions();
                break;
            case R.id.log_out:
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onTaskSuccess(){
        Log.d(TAG, "SUCCESS QUEUE LIST");
    }

    public void onAuthenticateFail() {}

    public void onNetworkFail() {
        Log.d(TAG, "NET FAIL QUEUE LIST");
    }
}
