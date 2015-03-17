package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by sellersk on 2/19/2015.
 */
public class WorkOrderActionQueueListActivity extends SingleFragmentActivity {
    private static final String TAG = "WorkOrderActionQueueActivity";

    Fragment fragment;

    private ActionBar mActionBar;

    @Override
    protected Fragment createFragment() {
        fragment = new WorkOrderActionQueueListFragment();
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
        setTitle(R.string.action_queue_activity_title);

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
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish(); //TODO: Add confirm to save info before finishing activity
                break;
            case R.id.action_sync:
                syncActions();
        }

        return super.onOptionsItemSelected(item);

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
        PostWorkOrdersTask task = new PostWorkOrdersTask(nameValuePairs, url, this);
        task.execute();


        //TODO 3/12/2015 - gray out actions in list after successful submit
        // sync notes


    }
}
