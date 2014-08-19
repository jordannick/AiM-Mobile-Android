package com.jordann.AiMMobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jordan_n on 8/14/2014.
 */


public class WorkOrderAdapter extends ArrayAdapter<WorkOrder> {

    private final Context context;

    public WorkOrderAdapter(Context c, ArrayList<WorkOrder> WorkOrders) {
        super(c, 0, WorkOrders);
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // If we weren't given a view, inflate one
        if (convertView == null) {
            /*
            //convertView = getActivity().getLayoutInflater()
            convertView = context.getApplicationContext().getLayoutInflater()
                    .inflate(R.layout.list_item_workorder, null);

                    */
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflator.inflate(R.layout.list_item_workorder, parent, false);
        }


        // Configure the view for a Work Order row
        WorkOrder wo = getItem(position);

        ((TextView)convertView.findViewById(R.id.row_workCode)).setText(wo.getMinCraftCode());

        ((TextView)convertView.findViewById(R.id.row_proposal)).setText(wo.getProposalPhase());

        ((TextView)convertView.findViewById(R.id.row_buildingTextView)).setText(wo.getBuilding());

        ((TextView)convertView.findViewById(R.id.row_category)).setText(wo.getCategory());

        ((TextView)convertView.findViewById(R.id.row_dayOfWeek)).setText(wo.getDateElements()[0]);

        ((TextView)convertView.findViewById(R.id.row_monthDay)).setText(wo.getDateElements()[1]);

        ((TextView)convertView.findViewById(R.id.row_year)).setText(wo.getDateElements()[2]);

        ((TextView)convertView.findViewById(R.id.row_daysAgo)).setText(wo.getDateElements()[3]);

        ((TextView) convertView.findViewById(R.id.row_priorityCodeSection)).setText(wo.getPriorityLetter());

        Log.d("TAG123", ""+(wo.getPriorityColor()));
        ((TextView)convertView.findViewById(R.id.row_priorityCodeSection)).setBackgroundResource(wo.getPriorityColor());

        //Temp Random
        Random random = new Random();
        int i = random.nextInt();
        if(i % 5 == 0){
            ((TextView)convertView.findViewById(R.id.row_newIndicator)).setText("NEW");
        }else
            ((TextView)convertView.findViewById(R.id.row_newIndicator)).setText("");

        //TODO: New Indicator
        //((TextView)convertView.findViewById(R.id.row_newIndicator))

        return convertView;
    }


}