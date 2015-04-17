package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by sellersk on 2/19/2015.
 */
public class WorkOrderActionQueueListActivity extends SingleFragmentActivity implements WorkOrderActionQueueListFragment.Callbacks {
    private static final String TAG = "WorkOrderActionQueueActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        setTitle(R.string.action_queue_activity_title);

        if(savedInstanceState!=null){
            //Restore saved state
        }
    }


    @Override
    protected Fragment createFragment() {
        return new WorkOrderActionQueueListFragment();
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
        Intent i = new Intent(this, WorkOrderAddActionActivity.class);
        i.putExtra(Action.EDIT_ACTION_EXTRA, actionPosition);
        startActivity(i);
    }

    public void syncActions(){

        String username = "";
        String timestamp = "";
        String workOrderPhaseID = "";

        //Create namevalue items required by the POST to pass to SubmitChange
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //Variables common to all the methods
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("timestamp", timestamp));
        nameValuePairs.add(new BasicNameValuePair("workOrderPhaseId", workOrderPhaseID));
    /*
        //Unique variables
        if (hours != 0) nameValuePairs.add(new BasicNameValuePair("hours", String.valueOf(hours)));

    */

        String url = "appropriate function call url here";

        //TODO 4/15/2015 - get this thing working
        /*
        PostWorkOrdersTask task = new PostWorkOrdersTask(nameValuePairs, url, this);
        task.execute();
        */

        //TODO 3/12/2015 - gray out actions in list after successful submit

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
}
