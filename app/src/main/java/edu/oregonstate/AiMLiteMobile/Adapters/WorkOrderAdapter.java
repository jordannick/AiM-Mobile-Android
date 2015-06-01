package edu.oregonstate.AiMLiteMobile.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
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
    //private TreeSet<Integer> sectionIndexes;

    private LayoutInflater inflator;

    private ArrayList<WorkOrderListItem> listItems;

    public int sectionDailyIndex, sectionBacklogIndex, sectionAdminIndex, sectionCompletedIndex;


    public WorkOrderAdapter(Context c, ArrayList<WorkOrder> workOrders) {
        super(c, 0, workOrders);
        this.filteredWorkOrders = workOrders;
        this.mWorkOrders = workOrders;
        this.context = c;


        inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initListItems();
    }

    private void initListItems(){

        listItems = new ArrayList<WorkOrderListItem>();

        int i = 0;

        listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Daily", null));
        sectionDailyIndex = i;

        while (i < mWorkOrders.size() && mWorkOrders.get(i).getSectionNum() == 0){
            listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, mWorkOrders.get(i)));
            i++;
        }

        listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Backlog", null));
        sectionBacklogIndex = i;

        while (i < mWorkOrders.size() && mWorkOrders.get(i).getSectionNum() == 1){
            listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, mWorkOrders.get(i)));
            i++;
        }

        listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Admin", null));
        sectionAdminIndex = i;

        while (i < mWorkOrders.size() && mWorkOrders.get(i).getSectionNum() == 2){
            listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, mWorkOrders.get(i)));
            i++;
        }

        listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Recently Completed", null));
        sectionCompletedIndex = i;

        while (i < mWorkOrders.size() && mWorkOrders.get(i).getSectionNum() == 3){
            listItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, mWorkOrders.get(i)));
            i++;
        }

    }

    public int getCount()
    {
        //return filteredWorkOrders.size();
        return listItems.size();
    }

    public WorkOrderListItem getItem(int position)
    {
        //return filteredWorkOrders.get(position);
        return listItems.get(position);
    }
/*
    public long getItemId(int position)
    {
        return position;
    }
*/

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return (listItems.get(position).getType() == WorkOrderListItem.Type.SECTION) ? 0 : 1;

    }

    @Override
    public int getViewTypeCount() {
        //return super.getViewTypeCount();
        return WorkOrderListItem.numTypes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WorkOrderListItem item = getItem(position);
        if (item.getType() == WorkOrderListItem.Type.SECTION){
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.list_item_section, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.listItem_section)).setText(item.getSectionTitle());

        } else {
            WorkOrder wo = item.getWorkOrder();
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.list_item_workorder, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.row_proposal)).setText(wo.getProposalPhase());
            //%% DEBUG %%
            ((TextView)convertView.findViewById(R.id.row_section)).setText(wo.getSection());
            //%% END DEBUG %%
            ((TextView)convertView.findViewById(R.id.row_dayOfWeek)).setText(wo.getDateElements()[0]);
            ((TextView)convertView.findViewById(R.id.row_monthDay)).setText(wo.getDateElements()[1]);
            ((TextView)convertView.findViewById(R.id.actionRow_valueAgo)).setText(wo.getDateElements()[3]);
            ((TextView)convertView.findViewById(R.id.actionRow_stringAgo)).setText(wo.getDateElements()[4]);
            convertView.findViewById(R.id.row_proposal).setBackgroundResource(wo.getPriorityColor());
            ((TextView)convertView.findViewById(R.id.row_description)).setText(wo.getDescription());
        }

        return convertView;
    }


}