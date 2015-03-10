package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sellersk on 2/17/2015.
 */
public class WorkOrderActionQueueListFragment extends ListFragment{
    public static final String TAG = "WorkOrderActionQueueListFragment";

    private static CurrentUser sCurrentUser;
    private Context mContext;

    public void updateUI() {
        ((ActionAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mContext = getActivity();

        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());
        ArrayList<Action> actions = sCurrentUser.getActions();
        Log.d(TAG, "actions: " + actions);

        ActionAdapter adapter = new ActionAdapter(mContext, actions);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast toast = Toast.makeText(mContext, "Clicked position : " + position, Toast.LENGTH_SHORT);
        toast.show();
        //super.onListItemClick(l, v, position, id);
    }

    //    public static WorkOrderActionQueueListFragment newInstance(/*UUID workOrderId*/){
//        //Bundle args = new Bundle();
//        //args.putSerializable(WORK_ORDER_ID, workOrderId);
//        WorkOrderActionQueueListFragment fragment = new WorkOrderActionQueueListFragment();
//        //fragment.setArguments(args);
//        return fragment;
//    }
}
