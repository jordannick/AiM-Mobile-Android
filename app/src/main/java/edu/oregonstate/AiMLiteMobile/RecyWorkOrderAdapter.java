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

    private List<WorkOrderListItem> workOrderListItems;
    private List<WorkOrder> workOrders;


    public int sectionDailyIndex, sectionBacklogIndex, sectionAdminIndex, sectionCompletedIndex;

    public RecyWorkOrderAdapter(List<WorkOrder> workOrders) {
        this.workOrders = workOrders;
        initListItems();
    }

    @Override
    public WorkOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_workorder, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "normal item height: " + v.getHeight());
                }
            });

        }else{
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_section, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "section item height: " + v.getHeight());
                }
            });
        }


        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkOrderViewHolder holder, int position) {
        WorkOrderListItem wo = workOrderListItems.get(position);


        if(wo.getType() != WorkOrderListItem.Type.SECTION){
            holder.vName.setText("TEST");
        }else{
            Log.d(TAG, "Type section: " + position);
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
        workOrderListItems = new ArrayList<>();
        sectionDailyIndex = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Daily", R.string.icon_daily, null));


        i++;
        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 0){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }
        sectionBacklogIndex = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Backlog", R.string.icon_backlog, null));


        i++;
        Log.d(TAG, "POST initListItems : backlog: " + sectionBacklogIndex + " vs size: " + workOrderListItems.size());

        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 1){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }
        sectionAdminIndex = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Admin", R.string.icon_admin, null));


        i++;

        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 2){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }
        sectionCompletedIndex = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Recently Completed", R.string.icon_recentlyCompleted, null));


        i++;
        while (i < workOrders.size() && workOrders.get(i).getSectionNum() == 3){
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, workOrders.get(i)));
            i++;
        }

        Log.d(TAG, "POST initListItems. " + sectionDailyIndex + " : " + sectionBacklogIndex + " : " + sectionAdminIndex + " : " + sectionCompletedIndex );
        Log.d(TAG, "POST initListItems. " + workOrderListItems.get(sectionBacklogIndex).getSectionTitle());


    }


    public static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public WorkOrderViewHolder(View v) {
            super(v);

            vName =  (TextView) v.findViewById(R.id.row_proposal);

        }
    }


}
