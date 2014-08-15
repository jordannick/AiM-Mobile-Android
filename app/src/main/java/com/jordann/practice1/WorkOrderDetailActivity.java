package com.jordann.practice1;

import android.app.Fragment;

import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class WorkOrderDetailActivity extends SingleFragmentActivity implements WorkOrderDetailFragment.Callbacks {

    public void onWorkOrderUpdated(WorkOrder wo) {

    }

    @Override
    protected Fragment createFragment() {
       // return new WorkOrderDetailFragment();
        UUID workOrderId = (UUID)getIntent()
                .getSerializableExtra(WorkOrderDetailFragment.WORK_ORDER_ID);
        return WorkOrderDetailFragment.newInstance(workOrderId);
    }

}
