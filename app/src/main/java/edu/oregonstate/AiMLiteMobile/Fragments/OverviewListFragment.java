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

import java.util.ArrayList;
import java.util.Date;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.TaskGetWorkOrders;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.ResponseWorkOrders;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class OverviewListFragment extends ListFragment {
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
                //((OverviewListActivity)getActivity()).lockScreen();
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

        updateListView();
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

        /*Bundle bundle = this.getArguments();
        //Retrieve section from bundle, filter this fragment instance to display either Daily or Backlog
        final String sectionFilter = bundle.getString("sectionFilter");
        adapter.getFilter().filter(sectionFilter);*/

        final WorkOrderAdapter adapter = new WorkOrderAdapter(getActivity(), sCurrentUser.getWorkOrders());
        setListAdapter(adapter);
        getListView().setDividerHeight(0);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WorkOrderAdapter adapter = (WorkOrderAdapter)getListAdapter();
        WorkOrderListItem item = adapter.getItem(position);
        if (item.getType() == WorkOrderListItem.Type.ITEM) {
            mCallbacks.onWorkOrderSelected(item.getWorkOrder());
        } else {
            //toggle expand?
        }
    }

    private void updateWorkOrderList(){
        Log.i(TAG, "Requested update work order list");
        ApiManager.getService().getWorkOrders(CurrentUser.getUsername(), CurrentUser.getToken(), new Callback<ResponseWorkOrders>() {
            @Override
            public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                ArrayList<WorkOrder> workOrders = responseWorkOrders.getWorkOrders();
                String logStr = "API MANAGER: getWorkOrders :: OK :: Size :" + workOrders.size();
                for (int i = 0; i < workOrders.size(); i++) {
                    WorkOrder workOrder = workOrders.get(i);
                    logStr += "\nWO #" + workOrder.getProposalPhase() + " " + workOrder.getDescription();
                }
                Log.d(TAG, logStr);

                //Save raw JSON for offline use
                sCurrentUser.getPrefsEditor().putString("work_order_data", responseWorkOrders.getRawJson());

                //Save new lastUpdated
                Date retrievedDate = new Date(System.currentTimeMillis());
                Log.i(TAG, "Saving new last_updated: " + retrievedDate.toString());
                CurrentUser.setLastUpdatedDate(retrievedDate);
                sCurrentUser.getPrefsEditor().putLong("last_updated", retrievedDate.getTime());
                sCurrentUser.getPrefsEditor().apply();
                SnackbarManager.show(Snackbar.with(getActivity()).text("Updated " + CurrentUser.getLastUpdatedDate().toString()).duration(Snackbar.SnackbarDuration.LENGTH_SHORT));

                //Save the workOrders
                sCurrentUser.setWorkOrders(workOrders);
                stopRefreshAnimation();
                updateListView();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "API MANAGER: getWorkOrders :: ERROR :: " + error);
                stopRefreshAnimation();
                SnackbarManager.show(Snackbar.with(getActivity()).text("Failed to retrieve work orders").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
            }
        });
    }
    private void stopRefreshAnimation(){
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    //Refresh the list display to reflect new data
    public void updateListView() {
        ((WorkOrderAdapter)getListAdapter()).notifyDataSetChanged();
    }
    public void onSectionChanged(){
        updateListView();
    }

}
