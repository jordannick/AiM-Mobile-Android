package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.support.v4.app.ListFragment;

import android.os.Bundle;
import android.util.Log;

import edu.oregonstate.AiMLiteMobile.Activities.DetailActivity;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Adapters.NoteAdapter;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 8/19/2014.
 */
public class DetailNotesFragment extends ListFragment {

    private static final String TAG = "DetailNotesFragment";
    private Activity mActivity;
    private Callbacks mCallbacks;
    private static CurrentUser sCurrentUser;
    private WorkOrder mWorkOrder;

    public interface Callbacks {
        void onWorkOrderUpdated();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getListView().setDividerHeight(0);
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();
        mWorkOrder = ((DetailActivity)mActivity).getWorkOrder();

        Log.d(TAG, "DetailNotes mWorkOrder: " + mWorkOrder + ", mActivity: " + mActivity);

        NoteAdapter adapter = new NoteAdapter(mActivity, mWorkOrder.getNotes());
        getListView().setSelector(android.R.color.transparent);
        setListAdapter(adapter);

    }
}
