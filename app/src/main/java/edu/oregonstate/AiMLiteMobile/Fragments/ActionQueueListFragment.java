package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/17/2015.
 */
public class ActionQueueListFragment extends ListFragment{
    public static final String TAG = "ActionQueueListFragment";

    private Activity activity;
    private Context context;
    private static CurrentUser currentUser;
    private ArrayList<Action> actions;
    private ActionAdapter actionQueueAdapter;
    private Callbacks callbacks;
    private static int actionCount = 0;
    public static final int TIMELOG_FRAGMENT = 1;
    private RelativeLayout recentlyViewedBarLayout;
    private View v;

    public interface Callbacks{
        void onActionSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = activity.getApplicationContext();
        currentUser = CurrentUser.get(context);
        actions = currentUser.getActions();
        actionQueueAdapter = new ActionAdapter(context, actions);

        setListAdapter(actionQueueAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(actionQueueAdapter != null) actionQueueAdapter.notifyDataSetChanged();
        updateTotalHoursText();
        updateSyncCountsText();

        Log.d(TAG, "adapter count: " + actionQueueAdapter.getCount() + " ; actionCount: " + actionCount);
        if (actionQueueAdapter.getCount() > actionCount) {
            SnackbarManager.show(Snackbar.with(getActivity()).text("Action Added").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
        }
        actionCount = actionQueueAdapter.getCount();
    }

    //Refresh the hours every time fragment comes into focus
    private void updateTotalHoursText(){
        TextView totalHoursTextView = (TextView) activity.findViewById(R.id.total_hours);

        int totalHours = 0;
        for (Action action : currentUser.getActions()){
            if (action.getHours() > 0){
                totalHours += action.getHours();
            }
        }
        if (totalHoursTextView != null) totalHoursTextView.setText(String.valueOf(totalHours));
    }

    private void updateSyncCountsText(){
        TextView unsyncedCountTextView = (TextView) activity.findViewById(R.id.unsynced_count);
        unsyncedCountTextView.setText(String.valueOf(currentUser.getActions().size()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.action_list, container, false);

        recentlyViewedBarLayout = (RelativeLayout)v.findViewById(R.id.actionList_recentlyViewedBarLayout);

        final ArrayList<WorkOrder> recentWorkOrders  = currentUser.getRecentlyViewedWorkOrders();

        //TextView recentBarL = (TextView)v.findViewById(R.id.actionList_recentBarIconL);
        TextView recentBarR = (TextView)v.findViewById(R.id.actionList_recentBarIconR);
        TextView recentBarL = (TextView)v.findViewById(R.id.actionList_recentBarIconL);
        TextView recentBarR2 = (TextView)v.findViewById(R.id.actionList_recentBarIconR2);
        TextView recentBarL2 = (TextView)v.findViewById(R.id.actionList_recentBarIconL2);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        recentBarR.setTypeface(tf); recentBarL.setTypeface(tf);
        recentBarR2.setTypeface(tf); recentBarL2.setTypeface(tf);
        recentBarL.setText(R.string.icon_recentBarExpand);
        recentBarR.setText(R.string.icon_recentBarExpand);
        recentBarR2.setText(R.string.icon_recentBarCollapse);
        recentBarL2.setText(R.string.icon_recentBarCollapse);

        final LinearLayout recentlyViewedLayout = (LinearLayout)v.findViewById(R.id.actionList_recentlyViewedLayout);
        recentlyViewedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecentlyViewed(false);
            }
        });

        int i;
        for (i = 0; i < recentWorkOrders.size(); i++) {
            WorkOrder workOrder = recentWorkOrders.get(i);
            recentlyViewedLayout.addView(createRecentRowView(workOrder));
        }

        RelativeLayout relativeLayout = (RelativeLayout)v.findViewById(R.id.actionList_relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LAYOUT ON CLICK");
                toggleRecentlyViewed(true);
            }
        });

        recentlyViewedBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recentWorkOrders.size() > 0) {
                    toggleRecentlyViewed(false);
                    recentlyViewedLayout.setVisibility(View.VISIBLE);
                }else{
                    //Show snackbar alert of "no recents"
                    SnackbarManager.show(Snackbar.with(getActivity()).text("No Recently Viewed").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));

                }
            }
        });

        return v;
    }

    private View createRecentRowView(final WorkOrder workOrder){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View rowView = (View)inflater.inflate(R.layout.time_log_recent_item, null);
        TextView workOrderNum = (TextView)rowView.findViewById(R.id.timeLog_recentWorkOrderNum);
        TextView description = (TextView)rowView.findViewById(R.id.timeLog_recentDescription);

        workOrderNum.setText(workOrder.getProposalPhase());
        description.setText(workOrder.getDescription());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimeEntryDialog(workOrder);
            }
        });

        return rowView;
    }


    private void createTimeEntryDialog(WorkOrder workOrder){
        AddActionDialogFragment actionFragment = new AddActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("WorkOrder", workOrder);
        bundle.putString("Title", "New From Recent");
        actionFragment.setArguments(bundle);
        actionFragment.setTargetFragment(this, TIMELOG_FRAGMENT);
        actionFragment.show(getFragmentManager(), "Diag");

    }

    private void toggleRecentlyViewed(boolean onlyHide){
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
        Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);
        LinearLayout layout = (LinearLayout)v.findViewById(R.id.actionList_recentlyViewedLayout);
        if(layout.getVisibility() == View.VISIBLE){
            layout.setVisibility(View.GONE);
            layout.startAnimation(slideDown);
            recentlyViewedBarLayout.startAnimation(slideUp);
        }else if(!onlyHide){
            layout.setVisibility(View.VISIBLE);
            layout.startAnimation(slideUp);
            recentlyViewedBarLayout.startAnimation(slideDown);
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Pass position for retrieving action from it later
        //callbacks.onActionSelected(position);
        callbacks.onActionSelected(position);
        toggleRecentlyViewed(false);
        Log.d(TAG, "clicked action position: "+position);
    }

    public void onPostActionDialog() {
        toggleRecentlyViewed(true);
        actionQueueAdapter.notifyDataSetInvalidated();

    }
}
