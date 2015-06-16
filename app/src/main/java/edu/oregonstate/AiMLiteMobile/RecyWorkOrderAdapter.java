package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private Context context;
    public Callbacks mCallbacks;

    public interface Callbacks {
        void onWorkOrderSelected(WorkOrder wo);
    }

    public int sectionDailyIndex, sectionBacklogIndex, sectionAdminIndex, sectionCompletedIndex;

    public RecyWorkOrderAdapter(List<WorkOrder> workOrders, Activity activity) {
        mCallbacks = (Callbacks) activity;
        context = activity;
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
    public void onBindViewHolder(WorkOrderViewHolder holder, final int position) {
        final WorkOrderListItem wo = workOrderListItems.get(position);

        if(wo.getType() != WorkOrderListItem.Type.SECTION){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onWorkOrderSelected(wo.getWorkOrder());
                }
            });

            holder.phaseId.setText(wo.getWorkOrder().getProposalPhase());
            holder.description.setText(wo.getWorkOrder().getDescription());

            switch (wo.getWorkOrder().getPriority()){
                case "TIME SENSITIVE":
                    holder.priorityIcon.setImageResource(R.drawable.priority_time_sensitive);
                    break;
                case "URGENT":
                    holder.priorityIcon.setImageResource(R.drawable.priority_urgent);
                    break;
                case "EMERGENCY":
                    holder.priorityIcon.setImageResource(R.drawable.priority_emergency);
                    break;
                case "ROUTINE":
                    holder.priorityIcon.setImageResource(R.drawable.priority_none);
                    break;
                case "SCHEDULED":
                    holder.priorityIcon.setImageResource(R.drawable.priority_none);
                    break;

            }

            holder.valueAgo.setText(wo.getWorkOrder().getDateElements()[3]);
            holder.stringAgo.setText(wo.getWorkOrder().getDateElements()[4]);
        } else {
            Typeface FONTAWESOME = Typeface.createFromAsset(context.getAssets(), "fonts/FontAwesome.otf");
            holder.sectionIcon.setText(wo.getSectionIcon());
            holder.sectionIcon.setTypeface(FONTAWESOME);
            holder.sectionTitle.setText(wo.getSectionTitle());




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


    public static class WorkOrderViewHolder extends RecyclerView.ViewHolder{
        protected TextView phaseId;
        protected TextView description;
        protected ImageView priorityIcon;
        protected TextView valueAgo;
        protected TextView stringAgo;

        protected TextView sectionIcon;
        protected TextView sectionTitle;


        public WorkOrderViewHolder(View v) {
            super(v);

            phaseId =  (TextView) v.findViewById(R.id.row_proposal);
            description = (TextView) v.findViewById(R.id.row_description);
            priorityIcon = (ImageView) v.findViewById(R.id.imageView_priorityIconOverview);
            valueAgo = (TextView) v.findViewById(R.id.actionRow_valueAgo);
            stringAgo = (TextView) v.findViewById(R.id.actionRow_stringAgo);


            sectionIcon = (TextView) v.findViewById(R.id.listItem_section_icon);
            sectionTitle = (TextView) v.findViewById(R.id.listItem_section);
        }
    }


}
