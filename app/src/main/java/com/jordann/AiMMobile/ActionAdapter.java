package com.jordann.AiMMobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionAdapter extends ArrayAdapter<Action> {
    private final static String TAG = "ActionAdapter";


    private final Context mContext;

    public ActionAdapter(Context c, ArrayList<Action> actions) {
        super(c, 0, actions);
        mContext = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView.... position: " + position + ", convertView: " + convertView + ", parent: " + parent);
        if(convertView == null){

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_action, parent, false);

        }
        Action action = getItem(position);


        TextView textView_id = (TextView)convertView.findViewById(R.id.action_work_order_id);

        textView_id.setText(String.valueOf(action.getWorkOrder().getProposalPhase()));

        ((TextView) convertView.findViewById(R.id.action_taken)).setText(action.getActionTaken());

        ((TextView) convertView.findViewById(R.id.action_newStatus)).setText(action.getUpdatedStatus());

        ((TextView) convertView.findViewById(R.id.action_timeSince)).setText(action.getDateStamp().toString());

        return convertView;
    }
}
