package edu.oregonstate.AiMLiteMobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionAdapter extends ArrayAdapter<Action> {
    private final static String TAG = "ActionAdapter";

    private final Context mContext;
    private ArrayList<Action> mActions;

    public ActionAdapter(Context c, ArrayList<Action> actions) {
        super(c, 0, actions);
        mContext = c;
        mActions = actions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_action, parent, false);
        }

        Action action = mActions.get(position);
        RelativeLayout relativeLayout = (RelativeLayout)convertView.findViewById(R.id.action_layout);
        if(action.isSynced()){
            relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.synced_green));
        }else{
            relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.unsynced_red));
        }

        //Populate the layout items with the action data
        ((TextView) convertView.findViewById(R.id.action_work_order_id)).setText(action.getWorkOrder().getProposalPhase());

        ((TextView) convertView.findViewById(R.id.action_work_order_location)).setText(action.getWorkOrder().getBuilding());

        ((TextView) convertView.findViewById(R.id.action_taken)).setText(action.getActionTakenString());

        ((TextView) convertView.findViewById(R.id.action_newStatus)).setText(action.getUpdatedStatus());

        ((TextView) convertView.findViewById(R.id.action_timeSince)).setText(action.getDateStamp().toString());

        ((TextView) convertView.findViewById(R.id.action_hours)).setText(String.valueOf(action.getHours()));

        return convertView;
    }
}
