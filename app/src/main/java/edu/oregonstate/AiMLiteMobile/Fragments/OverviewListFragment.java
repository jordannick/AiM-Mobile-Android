package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.view.ViewCompat;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import edu.oregonstate.AiMLiteMobile.Activities.OverviewListActivity;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Network.TaskGetWorkOrders;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;

/**
 * Created by jordan_n on 8/13/2014.
 */

public class OverviewListFragment extends ListFragment implements TaskGetWorkOrders.OnTaskCompleted {

    private static CurrentUser sCurrentUser;
    private static final String TAG = "OverviewListFragment";


    private static final int TAB_POSITION_DAILY = 0;
    private static final int TAB_POSITION_BACKLOG = 1;

    private Callbacks mCallbacks;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OverviewListActivity hostActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        //final View listFragmentView = inflater.inflate(R.layout.workorder_list_with_last_updated, container, false);

        //Set activity var
        hostActivity = (OverviewListActivity)getActivity();

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




    public interface Callbacks {
        void onWorkOrderSelected(WorkOrder wo);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*TextView lastUpdatedTextView = ((TextView) getActivity().findViewById(R.id.lastUpdated_TextView));
        lastUpdatedTextView.setText(sCurrentUser.getLastUpdated());*/
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

        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());
        getActivity().setTitle("Work Order List");
        /*getActivity().setTitle("  "+sCurrentUser.getUsername().toUpperCase());*/

        Bundle bundle = this.getArguments();
        final String sectionFilter = bundle.getString("sectionFilter");

        final WorkOrderAdapter adapter = new WorkOrderAdapter(getActivity(), sCurrentUser.getWorkOrders());

        // Update the Tab text with latest number of work orders in the section
        final Activity activity = this.getActivity();
/*        Filter.FilterListener countListener = new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int i) {
                if (sectionFilter.equals("Daily")) {
                    activity.getActionBar().getTabAt(0).setText("Daily (" + adapter.getCount() + ")");
                } else if (sectionFilter.equals("Backlog")) {
                    activity.getActionBar().getTabAt(1).setText("Backlog (" + adapter.getCount() + ")");
                }
            }
        };*/

        //adapter.getFilter().filter(sectionFilter, countListener);
        adapter.getFilter().filter(sectionFilter);


        setListAdapter(adapter);
        getListView().setDividerHeight(0);

        //Sets up long-click listener to allow WO Section to be updated
        setCustomOnLongClickListHandler();

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
        //TextView lastUpdatedTextView = ((TextView) getActivity().findViewById(R.id.lastUpdated_TextView));
        //lastUpdatedTextView.setText(sCurrentUser.getLastUpdated());
        hostActivity.updateSectionCounts();
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        WorkOrder wo = ((WorkOrderAdapter)getListAdapter()).getItem(position);
        mCallbacks.onWorkOrderSelected(wo);
    }

    private void setCustomOnLongClickListHandler() {
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Get current Tab
                ActionBar.Tab tab = hostActivity.getCurrentlySelectedTab();
                quickLog("Tab is : " + tab.getText() + ", with position: " + tab.getPosition());

                String tabName = "Daily";
                if (tab.getPosition() == TAB_POSITION_DAILY) tabName = "Backlog";



                //Shows alert dialog to confirm section change on Work Order
                return showAlertConfirmSectionMove("12345-001", tabName);
            }
        });
    }

    private boolean showAlertConfirmSectionMove(String workOrder, String destination) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Move " + workOrder + " to " + destination + "?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
        return true;
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

    private void quickLog(String text){
        Log.d(TAG, text);
    }



}
