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

    private Activity mActivity;
    private Context mContext;
    private static CurrentUser sCurrentUser;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onActionSelected(Action actionToEdit);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        sCurrentUser = CurrentUser.get(mContext);

        ArrayList<Action> actions = sCurrentUser.getActions();

        ActionAdapter adapter = new ActionAdapter(mContext, actions);
        setListAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Action actionToEdit = ((ActionAdapter)getListAdapter()).getItem(position);
        mCallbacks.onActionSelected(actionToEdit);
    }

}
