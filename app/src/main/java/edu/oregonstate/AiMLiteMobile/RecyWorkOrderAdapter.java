package edu.oregonstate.AiMLiteMobile;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;

/**
 * Created by sellersk on 6/15/2015.
 */
public class RecyWorkOrderAdapter extends RecyclerView.Adapter<RecyWorkOrderAdapter.WorkOrderViewHolder> {
    private static final String TAG = "RecyWorkOrderAdapter";

    private List<WorkOrderListItem> workOrderListItems = new ArrayList<>();
    private List<WorkOrder> workOrders;


    public int sectionDailyIndex, sectionBacklogIndex, sectionAdminIndex, sectionCompletedIndex;

    public RecyWorkOrderAdapter(List<WorkOrder> workOrders) {
        this.workOrders = workOrders;
        initListItems();
    }

    @Override
    public WorkOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d(TAG, "@@#onCreateViewHolder");
        if(viewType == 1) {
            Log.d(TAG, "@@#inflating view");
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_workorder, parent, false);
        }else{
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_section, parent, false);
        }

        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkOrderViewHolder holder, int position) {
        WorkOrderListItem wo = workOrderListItems.get(position);

        if(wo.getType() != WorkOrderListItem.Type.SECTION){
            holder.vName.setText("TEST");
        }
/*        holder.vName.setText(wo.name);
        holder.vSurname.setText(wo.surname);
        holder.vEmail.setText(ci.email);
        holder.vTitle.setText(ci.name + " " + ci.surname);*/
    }

    @Override
    public int getItemViewType(int position) {
        return (workOrderListItems.get(position).getType() == WorkOrderListItem.Type.SECTION) ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return workOrderListItems.size();
    }


    private void initListItems(){
        int i = 0;
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Daily", R.string.icon_daily, null));
        sectionDailyIndex = i;
        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 0){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }

        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Backlog", R.string.icon_backlog, null));

        sectionBacklogIndex = i;

        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 1){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }

        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Admin", R.string.icon_admin, null));

        sectionAdminIndex = i;

        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 2){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }

        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Recently Completed", R.string.icon_recentlyCompleted, null));

        sectionCompletedIndex = i;

        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 3){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }
    }


    public static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public WorkOrderViewHolder(View v) {
            super(v);

            vName =  (TextView) v.findViewById(R.id.row_proposal);
            Log.d(TAG, "@@# setTextView " + vName);
        }
    }


}
