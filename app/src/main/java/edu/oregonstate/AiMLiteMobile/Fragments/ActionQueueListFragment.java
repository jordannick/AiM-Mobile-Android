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

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
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
    private ActionAdapter mActionQueueAdapter;
    private Callbacks mCallbacks;
    private static int actionCount = 0;

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

        ArrayList<Action> actions = sCurrentUser.getActions();

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

        LinearLayout recentlyViewedBarLayout = (LinearLayout)v.findViewById(R.id.actionList_recentlyViewedBarLayout);


        CurrentUser currentUser = CurrentUser.get(getActivity());
        ArrayList<WorkOrder> recentWorkOrders  = currentUser.getRecentlyViewedWorkOrders();
        TextView[] numTextViews = new TextView[5];
        numTextViews[0] = (TextView)v.findViewById(R.id.actionList_recentWorkOrderNum0);
        numTextViews[1] = (TextView)v.findViewById(R.id.actionList_recentWorkOrderNum1);
        numTextViews[2] = (TextView)v.findViewById(R.id.actionList_recentWorkOrderNum2);
        numTextViews[3] = (TextView)v.findViewById(R.id.actionList_recentWorkOrderNum3);
        numTextViews[4] = (TextView)v.findViewById(R.id.actionList_recentWorkOrderNum4);
        TextView[] descTextViews = new TextView[5];
        descTextViews[0] = (TextView)v.findViewById(R.id.actionList_recentDescription0);
        descTextViews[1] = (TextView)v.findViewById(R.id.actionList_recentDescription1);
        descTextViews[2] = (TextView)v.findViewById(R.id.actionList_recentDescription2);
        descTextViews[3] = (TextView)v.findViewById(R.id.actionList_recentDescription3);
        descTextViews[4] = (TextView)v.findViewById(R.id.actionList_recentDescription4);
        LinearLayout[] linearLayouts = new LinearLayout[5];
        linearLayouts[0] = (LinearLayout)v.findViewById(R.id.actionList_recentLayout0);
        linearLayouts[1] = (LinearLayout)v.findViewById(R.id.actionList_recentLayout1);
        linearLayouts[2] = (LinearLayout)v.findViewById(R.id.actionList_recentLayout2);
        linearLayouts[3] = (LinearLayout)v.findViewById(R.id.actionList_recentLayout3);
        linearLayouts[4] = (LinearLayout)v.findViewById(R.id.actionList_recentLayout4);


        TextView recentBarL = (TextView)v.findViewById(R.id.actionList_recentBarIconL);
        TextView recentBarR = (TextView)v.findViewById(R.id.actionList_recentBarIconR);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        recentBarL.setTypeface(tf); recentBarR.setTypeface(tf);
        recentBarL.setText(R.string.icon_recentBarExpand); recentBarR.setText(R.string.icon_recentBarExpand);

        int i;
        for (i = 0; i < recentWorkOrders.size(); i++) {
            WorkOrder workOrder = recentWorkOrders.get(i);
            numTextViews[i].setText(workOrder.getProposalPhase());
            descTextViews[i].setText(workOrder.getDescription());
        }
        for (; i < currentUser.recentlyViewedMax; i++) {
            numTextViews[i].setText("---");
            //numTextViews[i].setVisibility(View.GONE);
            descTextViews[i].setText("---");
            //descTextViews[i].setVisibility(View.GONE);
            //linearLayouts[i].setVisibility(View.GONE);
        }
        

        RelativeLayout relativeLayout = (RelativeLayout)v.findViewById(R.id.actionList_relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LAYOUT ON CLICK");
                hideRecentlyViewed();
            }
        });

        final LinearLayout recentlyViewedLayout = (LinearLayout)v.findViewById(R.id.actionList_recentlyViewedLayout);
        recentlyViewedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final Animation slideInFromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);

        recentlyViewedBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentlyViewedLayout.setVisibility(View.VISIBLE);
                recentlyViewedLayout.startAnimation(slideInFromBottom);
            }
        });

        return v;



    }

    private void hideRecentlyViewed(){
        LinearLayout layout = (LinearLayout)v.findViewById(R.id.actionList_recentlyViewedLayout);
        if(layout.getVisibility() == View.VISIBLE){
            layout.setVisibility(View.GONE);
            Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);
            layout.startAnimation(slideDown);
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
        hideRecentlyViewed();
        Log.d(TAG, "clicked action position: "+position);
    }



}
