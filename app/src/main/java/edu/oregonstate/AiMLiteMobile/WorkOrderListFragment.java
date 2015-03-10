package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Created by jordan_n on 8/13/2014.
 */

public class WorkOrderListFragment extends ListFragment{

    private static CurrentUser sCurrentUser;
    private static final String TAG = "WorkOrderListFragment";
    private Callbacks mCallbacks;

    public void updateUI() {
        Log.d(TAG, "notifyDataSetChanged");
        ((WorkOrderAdapter)getListAdapter()).notifyDataSetChanged();
    }

/*
    public void onTaskCompleted(ArrayList<WorkOrder> workOrders) {
        WorkOrderAdapter adapter = new WorkOrderAdapter(getActivity(), workOrders);
        setListAdapter(adapter);

        this.getListView().setDivider(new ColorDrawable(0xFF888888));
        this.getListView().setDividerHeight(2);
        sAllWorkOrders.setWorkOrders(workOrders);
    }
*/

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onWorkOrderSelected(WorkOrder wo);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       // getActivity().setTitle(R.string.list_title);

        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());
        getActivity().setTitle(sCurrentUser.getUsername());



        WorkOrderAdapter adapter = new WorkOrderAdapter(getActivity(), sCurrentUser.getWorkOrders());

        setListAdapter(adapter);

        //TODO: fix setdivider crash
        //this.getListView().setDivider(new ColorDrawable(0xFF888888));
        //this.getListView().setDividerHeight(8);


        //sAllWorkOrders = AllWorkOrders.get(getActivity());

        //GetWorkOrdersTask task = new GetWorkOrdersTask(this, url);
        //task.execute();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WorkOrder wo = ((WorkOrderAdapter)getListAdapter()).getItem(position);
       // Log.d(TAG, "!wo: "+wo);

        for (WorkOrder workOrder : sCurrentUser.getWorkOrders()){
          //  Log.d(TAG, "1wo: "+workOrder);
        }
        mCallbacks.onWorkOrderSelected(wo);

    }



}
