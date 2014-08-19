package com.jordann.AiMMobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class WorkOrderDetailActivity extends Activity implements WorkOrderDetailFragment.Callbacks {

    ActionBar.Tab Tab1, Tab2, Tab3;
    Fragment fragmentTab1 = new WorkOrderDetailFragment();
    Fragment fragmentTab2 = new WorkOrderNotesFragment();
    Fragment fragmentTab3 = new WorkOrderContactFragment();

    public UUID workOrderId;

    public void onWorkOrderUpdated(WorkOrder wo) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        workOrderId = (UUID)getIntent().getSerializableExtra(WorkOrderDetailFragment.WORK_ORDER_ID);


        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab1 = actionBar.newTab().setText("Overview");
        Tab2 = actionBar.newTab().setText("Notes");
        Tab3 = actionBar.newTab().setText("Contact");

        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));
        Tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);

    }

}
