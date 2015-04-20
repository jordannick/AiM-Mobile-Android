package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;

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
        mActivity = getActivity();
        mWorkOrder = ((DetailActivity)mActivity).getWorkOrder();

        NoteAdapter adapter = new NoteAdapter(mActivity, mWorkOrder.getNotes());

        setListAdapter(adapter);
    }

}
