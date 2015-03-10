package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


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
        //getMenuInflater().inflate(R.menu.menu_add_action, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish(); //TODO: Add confirm to save info before finishing activity
                break;
        }

        return super.onOptionsItemSelected(item);

    }
}
