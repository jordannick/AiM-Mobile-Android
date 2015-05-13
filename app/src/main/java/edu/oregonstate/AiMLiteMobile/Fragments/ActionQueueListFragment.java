package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/17/2015.
 */
public class ActionQueueListFragment extends ListFragment{
    public static final String TAG = "ActionQueueListFragment";

    private Activity mActivity;
    private Context mContext;
    private static CurrentUser sCurrentUser;
    private ActionAdapter mActionQueueAdapter;
    private Callbacks mCallbacks;
    private static int actionCount = 0;

    public interface Callbacks{
        void onActionSelected(int position);
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

        mActionQueueAdapter = new ActionAdapter(mContext, actions);

        setListAdapter(mActionQueueAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mActionQueueAdapter != null) mActionQueueAdapter.notifyDataSetChanged();
        updateTotalHoursText();


        Log.d(TAG, "adapter count: " + mActionQueueAdapter.getCount() + " ; actionCount: " + actionCount);
        if (mActionQueueAdapter.getCount() > actionCount) {
            SnackbarManager.show(Snackbar.with(getActivity()).text("Action Added").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
        }
        actionCount = mActionQueueAdapter.getCount();
    }

    //Refresh the hours every time fragment comes into focus
    private void updateTotalHoursText(){
       /* TextView hoursText = (TextView)mActivity.findViewById(R.id.hoursTotal_textView);

        int totalHours = 0;
        for (Action action : sCurrentUser.getActions()){
            totalHours += action.getHours();
        }

        if (hoursText!=null) hoursText.setText(String.valueOf(totalHours));*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      //  View v = super.onCreateView(inflater, container, savedInstanceState);
       // return v;

        return inflater.inflate(R.layout.action_list, container, false);
    }

/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View footerView = inflater.inflate(R.layout.action_list, getListView(), false);

        //Log.d(TAG, "footerView = "+footerView);
        //getListView().addFooterView(footerView);


    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Pass position for retrieving action from it later
        mCallbacks.onActionSelected(position);
    }

}