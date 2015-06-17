package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Fragments.ActionQueueListFragment;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionQueueListActivity extends SingleFragmentActivity implements ActionQueueListFragment.Callbacks {
    private static final String TAG = "ActionQueueActivity";
    private static CurrentUser sCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar = getActionBar();
//        mActionBar.setDisplayHomeAsUpEnabled(true);
    //    mActionBar.setHomeButtonEnabled(true);
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
        //TODO open edit action dialog fragment
    }

    //Start the HTTP POSTs to submit actions from queue.
    //Remove from queue upon successful POST
    public void syncActions(){
        //TODO: implement retrofit addAction
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_notification:
                createNoticesViewPopup();
                break;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
           case R.id.action_sync:
                syncActions();
                break;
            case R.id.log_out:
                sCurrentUser.prepareLogout();
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

    private void createNoticesViewPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.popup_notes_list, null);

        convertView.findViewById(R.id.dialogNotes_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        NoticeAdapter noticesAdapter = new NoticeAdapter(this, sCurrentUser.getNotices());
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupNotes_listView);
        lv.setSelector(android.R.color.transparent);
        lv.setAdapter(noticesAdapter);
        alertDialog.show();
    }
}
