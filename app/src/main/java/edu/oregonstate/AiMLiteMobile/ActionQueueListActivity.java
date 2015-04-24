package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
public class ActionQueueListActivity extends SingleFragmentActivity implements ActionQueueListFragment.Callbacks {
    private static final String TAG = "WorkOrderActionQueueActivity";
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


        for (Action action : sCurrentUser.getActions()){
            //Variables common to all API methods
            String username = sCurrentUser.getUsername();
            //String timestamp = //current system time
            String workOrderPhaseID = action.getWorkOrder().getProposalPhase();

             /*Unique variables:
                date, hours, timetype
                actiontaken
                note
                newStatus
                value (section)
            */

            Date date = action.getDateStamp();
            int hours = action.getHours();
            //timetype = ?
            String actionTaken = action.getActionTaken();
            //how to handle multiple notes...?
            String newStatus = action.getUpdatedStatus();
            //section value = daily or backlog

            //TODO: namevaluepairs deprecated find new way to pass in variables

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


            nameValuePairs.add(new BasicNameValuePair("username", username));
            //nameValuePairs.add(new BasicNameValuePair("timestamp", timestamp));
            nameValuePairs.add(new BasicNameValuePair("workOrderPhaseId", workOrderPhaseID));


            //  if (hours != 0) nameValuePairs.add(new BasicNameValuePair("hours", String.valueOf(hours)));


            String url = "appropriate function call url here";

            //TODO 4/15/2015 - get this thing working
            /*
            TaskPostAction task = new TaskPostAction(nameValuePairs, url, this);
            task.execute();
            */

            //TODO 3/12/2015 - gray out actions in list after successful submit

        }



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
