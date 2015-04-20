package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

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

        //Populate the layout items with the action data
        ((TextView) convertView.findViewById(R.id.action_work_order_id)).setText(action.getWorkOrder().getProposalPhase());

        ((TextView) convertView.findViewById(R.id.action_work_order_location)).setText(action.getWorkOrder().getBuilding());

        ((TextView) convertView.findViewById(R.id.action_taken)).setText(action.getActionTaken());

        ((TextView) convertView.findViewById(R.id.action_newStatus)).setText(action.getUpdatedStatus());

        ((TextView) convertView.findViewById(R.id.action_timeSince)).setText(action.getDateStamp().toString());

        ((TextView) convertView.findViewById(R.id.action_hours)).setText(String.valueOf(action.getHours()));

        return convertView;
    }
}
