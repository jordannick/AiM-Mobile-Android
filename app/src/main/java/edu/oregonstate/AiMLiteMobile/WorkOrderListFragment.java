package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.view.ViewCompat;
import android.widget.TabHost;

import java.util.ArrayList;


/**
 * Created by jordan_n on 8/13/2014.
 */

public class WorkOrderListFragment extends ListFragment implements GetWorkOrdersTask.OnTaskCompleted{

    private static CurrentUser sCurrentUser;
    private static final String TAG = "WorkOrderListFragment";
    private Callbacks mCallbacks;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public void updateUI() {
        Log.d(TAG, "notifyDataSetChanged");
        ((WorkOrderAdapter)getListAdapter()).notifyDataSetChanged();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());

        mSwipeRefreshLayout.addView(listFragmentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );

        final Activity activity = this.getActivity();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWorkOrderList();
                //activity.getActionBar().hide();

            }
        });



        return mSwipeRefreshLayout;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "listfragment onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "listfragment onPause");
        super.onDestroy();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

        public ListFragmentSwipeRefreshLayout(Context context) {
            super(context);
        }

            @Override
            public boolean canChildScrollUp() {
                final ListView listView = getListView();
                if (listView.getVisibility() == View.VISIBLE) {
                    return canListViewScrollUp(listView);
                } else {
                    return false;
                }
        }
    }

    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }


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
    public void onActivityCreated(Bundle savedInstanceState) {

        //this.getListView().setPadding(0, 96, 0, 0);
        //this.getListView().setClipToPadding(false);

        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());
        getActivity().setTitle(sCurrentUser.getUsername());

        Bundle bundle = this.getArguments();
        final String sectionFilter = bundle.getString("sectionFilter");

        final WorkOrderAdapter adapter = new WorkOrderAdapter(getActivity(), sCurrentUser.getWorkOrders());


        // Update the Tab text with latest number of work orders in the section
        final Activity activity = this.getActivity();
        Filter.FilterListener countListener = new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int i) {
                if (sectionFilter.equals("Daily")) {
                    activity.getActionBar().getTabAt(0).setText("Daily (" + adapter.getCount() + ")");
                } else if (sectionFilter.equals("Backlog")) {
                    activity.getActionBar().getTabAt(1).setText("Backlog (" + adapter.getCount() + ")");
                }
            }
        };

        adapter.getFilter().filter(sectionFilter, countListener);

        setListAdapter(adapter);

        Log.d(TAG, ""+sCurrentUser.getWorkOrders());

        super.onActivityCreated(savedInstanceState);
    }

    private void updateWorkOrderList(){
        Log.d(TAG, "updateWorkOrderList");

        CurrentUser currentUser = CurrentUser.get(getActivity().getApplicationContext());

        GetWorkOrdersTask task = new GetWorkOrdersTask(this, currentUser, getActivity(), false);
        task.execute();
        updateUI();

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


    //Callback methods

    public void onTaskSuccess() {
        Log.d(TAG, "list task success");

        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    public void onNetworkFail() {
        Log.d(TAG, "list net fail");
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    public void onAuthenticateFail() {
        Log.d(TAG, "list auth fail");
    }



}
