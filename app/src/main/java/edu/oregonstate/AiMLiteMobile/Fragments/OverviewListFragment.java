package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.view.ViewCompat;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import edu.oregonstate.AiMLiteMobile.Activities.DetailActivity;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Network.TaskGetWorkOrders;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class OverviewListFragment extends ListFragment implements TaskGetWorkOrders.OnTaskCompleted {
    private static final String TAG = "OverviewListFragment";

    private static CurrentUser sCurrentUser;
    private Callbacks mCallbacks;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public interface Callbacks {
        void onWorkOrderSelected(WorkOrder wo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        //Add our fragment view to swipe view, allows pull-to-refresh list
        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());
        mSwipeRefreshLayout.addView(listFragmentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWorkOrderList();
            }
        });

        return mSwipeRefreshLayout;
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
            // Pre-ICS we need to manually check the first visible item and the child view's top value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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
        getActivity().setTitle("Work Order List");
        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());
        Bundle bundle = this.getArguments();

        //Retrieve section from bundle, filter this fragment instance to display either Daily or Backlog
        final String sectionFilter = bundle.getString("sectionFilter");
        final WorkOrderAdapter adapter = new WorkOrderAdapter(getActivity(), sCurrentUser.getWorkOrders());
        adapter.getFilter().filter(sectionFilter);

        setListAdapter(adapter);
        getListView().setDividerHeight(0);

        super.onActivityCreated(savedInstanceState);
    }

    private void updateWorkOrderList(){
        Log.i(TAG, "Requested update work order list");

        CurrentUser currentUser = CurrentUser.get(getActivity().getApplicationContext());
        TaskGetWorkOrders task = new TaskGetWorkOrders(this, currentUser, getActivity(), true);
        task.execute();
    }

    //Refresh the list display to reflect new data
    public void updateUI() {
        ((WorkOrderAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WorkOrder wo = ((WorkOrderAdapter)getListAdapter()).getItem(position);
        mCallbacks.onWorkOrderSelected(wo);
    }

    //Callback methods
    public void onTaskSuccess() {
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        updateUI();
        SnackbarManager.show(Snackbar.with(getActivity()).text("Updated " + sCurrentUser.getLastUpdated()).duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
    }

    public void onNetworkFail() {
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        SnackbarManager.show(Snackbar.with(getActivity()).text("Network Access Failed").actionLabel("DISMISS").actionColor(Color.RED).duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE));
    }

    public void onAuthenticateFail() {

    }

    public void onSectionChanged(){
        updateUI();
    }

}
