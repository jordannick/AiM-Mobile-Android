package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
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

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Note;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/17/2015.
 */
public class ActionQueueListFragment extends ListFragment{
    public static final String TAG = "ActionQueueListFragment";

    private Activity mActivity;
    private Context mContext;
    private static CurrentUser sCurrentUser;
    private ArrayList<Action> actions;
    private ActionAdapter mActionQueueAdapter;
    private Callbacks mCallbacks;
    private static int actionCount = 0;

    private RelativeLayout recentlyViewedBarLayout;

    private View v;

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

        actions = sCurrentUser.getActions();

        mActionQueueAdapter = new ActionAdapter(mContext, actions);

        setListAdapter(mActionQueueAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mActionQueueAdapter != null) mActionQueueAdapter.notifyDataSetChanged();
        updateTotalHoursText();
        updateSyncCountsText();


        Log.d(TAG, "adapter count: " + mActionQueueAdapter.getCount() + " ; actionCount: " + actionCount);
        if (mActionQueueAdapter.getCount() > actionCount) {
            //TODO: reimplement Snackbar
            SnackbarManager.show(Snackbar.with(getActivity()).text("Action Added").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
        }
        actionCount = mActionQueueAdapter.getCount();
    }

    //Refresh the hours every time fragment comes into focus
    private void updateTotalHoursText(){

        TextView totalHoursTextView = (TextView)mActivity.findViewById(R.id.total_hours);
        int totalHours = 0;

        for (Action action : sCurrentUser.getActions()){
            if (action.getHours() > 0){
                totalHours += action.getHours();
            }
        }
        if (totalHoursTextView != null) totalHoursTextView.setText(String.valueOf(totalHours));
    }

    private void updateSyncCountsText(){
        TextView unsyncedCountTextView = (TextView)mActivity.findViewById(R.id.unsynced_count);
        unsyncedCountTextView.setText(String.valueOf(sCurrentUser.getActions().size()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.action_list, container, false);

        recentlyViewedBarLayout = (RelativeLayout)v.findViewById(R.id.actionList_recentlyViewedBarLayout);


        CurrentUser currentUser = CurrentUser.get(getActivity());
        final ArrayList<WorkOrder> recentWorkOrders  = currentUser.getRecentlyViewedWorkOrders();

        //TextView recentBarL = (TextView)v.findViewById(R.id.actionList_recentBarIconL);
        TextView recentBarR = (TextView)v.findViewById(R.id.actionList_recentBarIconR);
        TextView recentBarR2 = (TextView)v.findViewById(R.id.actionList_recentBarIconR2);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        //recentBarL.setTypeface(tf);
        recentBarR.setTypeface(tf);
        recentBarR2.setTypeface(tf);
        //recentBarL.setText(R.string.icon_recentBarExpand);
        recentBarR.setText(R.string.icon_recentBarExpand);
        recentBarR2.setText(R.string.icon_recentBarCollapse);

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
        final WorkOrder wo = workOrder;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Test test testaroo.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actions.add(new Action(wo, "Deed is done!", "ALL FUCKED UP", 4, new ArrayList<Note>()));
                mActionQueueAdapter.notifyDataSetInvalidated();
                toggleRecentlyViewed(true);
            }
        });

        builder.create().show();




    }

    private int getPx(int dimensionDp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
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
        //mCallbacks.onActionSelected(position);
        mCallbacks.onActionSelected(position);
        toggleRecentlyViewed(false);
        Log.d(TAG, "clicked action position: "+position);
    }



}
