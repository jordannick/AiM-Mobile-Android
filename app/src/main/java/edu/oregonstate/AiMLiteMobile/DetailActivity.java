package edu.oregonstate.AiMLiteMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;


/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailActivity extends Activity implements DetailMainFragment.Callbacks, DetailNotesFragment.Callbacks, TabHost.OnTabChangeListener {
    private static final String TAG = "DetailActivity";

    public WorkOrder mWorkOrder;
    private ActionBar actionBar;
    private View v;

    public void onWorkOrderUpdated() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        v = this.findViewById(android.R.id.content);

        mWorkOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab Tab1, Tab2, Tab3;
        Fragment fragmentTab1 = new DetailMainFragment();
        Fragment fragmentTab2 = new DetailNotesFragment();
        Fragment fragmentTab3 = new DetailContactFragment();

        Tab1 = actionBar.newTab().setText("Overview");
        Tab2 = actionBar.newTab().setText("Notes");
        Tab3 = actionBar.newTab().setText("Contact");

        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));
        Tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        if(savedInstanceState!=null){
            //Restore the tab the user was last at
            int currentTab = savedInstanceState.getInt("CurrentDetailsTab");
            actionBar.selectTab(actionBar.getTabAt(currentTab));
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save which tab the user was last at
        outState.putInt("CurrentDetailsTab", actionBar.getSelectedTab().getPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                //Create and start the AddAction activity, sending the work order object
                Intent i = new Intent(this, AddActionActivity.class);
                i.putExtra(WorkOrder.WORK_ORDER_EXTRA, mWorkOrder);
                startActivity(i);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    //For possible tab switch animation
    @Override
    public void onTabChanged(String tabId) {
        /*View currentView = this.getCurrentView();
        if (getTabHost().getCurrentTab() > currentTab)
        {
            currentView.setAnimation( inFromRightAnimation() );
        }
        else
        {
            currentView.setAnimation( outToRightAnimation() );
        }

        currentTab = getTabHost().getCurrentTab();*/

    }

/*
    public Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(240);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToRightAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(240);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
*/


}