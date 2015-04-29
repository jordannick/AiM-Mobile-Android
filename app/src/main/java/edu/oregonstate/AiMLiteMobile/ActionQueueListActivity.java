package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



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

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);
        return true;
    }



    //Open edit action form for clicked action, with position of action in queue
    public void onActionSelected(int actionPosition){
        Intent i = new Intent(this, AddActionActivity.class);
        i.putExtra(Action.EDIT_ACTION_EXTRA, actionPosition);
        startActivity(i);
    }

    //Start the HTTP POSTs to submit actions from queue.
    //Remove from queue upon successful POST
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

        // %% DEBUG %%

        // %% END_DEBUG %%

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_sync:
                syncActions();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    public void onTaskSuccess(){
        Log.d(TAG, "SUCCESS QUEUE LIST");
    }

    public void onAuthenticateFail() {

    }


    public void onNetworkFail() {
        Log.d(TAG, "NET FAIL QUEUE LIST");
    }
}
