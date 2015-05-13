package edu.oregonstate.AiMLiteMobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Random;

import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by jordan_n on 8/14/2014.
 */


public class WorkOrderAdapter extends ArrayAdapter implements Filterable{
    private static final String TAG = "WorkOrderAdapter";
    private final Context context;
    private ArrayList<WorkOrder> filteredWorkOrders;
    private ArrayList<WorkOrder> mWorkOrders;

    public WorkOrderAdapter(Context c, ArrayList<WorkOrder> workOrders) {
        super(c, 0, workOrders);
        this.filteredWorkOrders = workOrders;
        this.mWorkOrders = workOrders;
        this.context = c;
    }

    public int getCount()
    {
        return filteredWorkOrders.size();
    }

    public WorkOrder getItem(int position)
    {
        return filteredWorkOrders.get(position);
    }
/*
    public long getItemId(int position)
    {
        return position;
    }
*/



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
        //WorkOrder wo = filteredWorkOrders.get(position);

        //((TextView)convertView.findViewById(R.id.row_workCode)).setText(wo.getMinCraftCode());

        ((TextView)convertView.findViewById(R.id.row_proposal)).setText(wo.getProposalPhase());

        //((TextView)convertView.findViewById(R.id.row_buildingTextView)).setText(wo.getBuilding());

        //((TextView)convertView.findViewById(R.id.row_category)).setText(wo.getCategory());

        ((TextView)convertView.findViewById(R.id.row_dayOfWeek)).setText(wo.getDateElements()[0]);

        ((TextView)convertView.findViewById(R.id.row_monthDay)).setText(wo.getDateElements()[1]);

        /*((TextView)convertView.findViewById(R.id.row_year)).setText(wo.getDateElements()[2]);*/

        ((TextView)convertView.findViewById(R.id.actionRow_valueAgo)).setText(wo.getDateElements()[3]);
        ((TextView)convertView.findViewById(R.id.actionRow_stringAgo)).setText(wo.getDateElements()[4]);

        //((TextView) convertView.findViewById(R.id.row_priorityCodeSection)).setText(wo.getPriorityLetter());

        //Log.d("TAG123", ""+(wo.getPriorityColor()));
        //((TextView)convertView.findViewById(R.id.row_priorityCodeSection)).setBackgroundResource(wo.getPriorityColor());

        //convertView.findViewById(R.id.row_priorityStrip).setBackgroundResource(wo.getPriorityColor());
        convertView.findViewById(R.id.row_proposal).setBackgroundResource(wo.getPriorityColor());

        ((TextView)convertView.findViewById(R.id.row_description)).setText(wo.getDescription());

/*        //Temp Random
        Random random = new Random();
        int i = random.nextInt();
        if(i % 5 == -1){
            //((TextView)convertView.findViewById(R.id.row_newIndicator)).setText("NEW");
        }else
            ((TextView)convertView.findViewById(R.id.row_newIndicator)).setText("");

        //TODO: New Indicator
        //((TextView)convertView.findViewById(R.id.row_newIndicator))*/

        return convertView;
    }

    //Filters the work order list to display, based on the constraint passed in -- "Daily" or "Backlog"
    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<WorkOrder> filteredResultsData = new ArrayList<WorkOrder>();

                if (mWorkOrders!=null) {
                    for (WorkOrder wo : mWorkOrders) {
                        if (wo.getSection().equals(constraint)) {
                            filteredResultsData.add(wo);
                        }
                    }
                    results.values = filteredResultsData;
                    results.count = filteredResultsData.size();
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredWorkOrders = (ArrayList<WorkOrder>) results.values;

                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


}