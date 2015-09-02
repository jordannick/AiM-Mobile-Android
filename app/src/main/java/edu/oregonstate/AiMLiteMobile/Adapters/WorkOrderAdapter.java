package edu.oregonstate.AiMLiteMobile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.Constants;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.R;


/**
 * Created by sellersk on 6/15/2015.
 */

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder> implements Filterable {
    private static final String TAG = "AiM_WorkOrderAdapter";

    private ArrayList<WorkOrderListItem> workOrderListItems;
    private ArrayList<WorkOrderListItem> workOrderListItemsOrig;
    private ArrayList<WorkOrder> workOrders;
    private Context context;
    public Callbacks mCallbacks;
    public WorkOrderListWrapper wrapper;

    private int numSections = 4;

    public interface Callbacks {
        void onWorkOrderSelected(WorkOrder wo);
    }


    public WorkOrderAdapter(ArrayList<WorkOrder> workOrders, Activity activity) {
        mCallbacks = (Callbacks) activity;
        context = activity;
        this.workOrders = workOrders;
        initListItems();
    }

    public void refreshWorkOrders(ArrayList<WorkOrder> workOrders) {
        this.workOrders = workOrders;
        initListItems();
        notifyDataSetChanged();
    }


    @Override
    public WorkOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == WorkOrderListItem.WORK_ORDER_ITEM) {
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_workorder, parent, false);
        } else {
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_section, parent, false);
        }


        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkOrderViewHolder holder, final int position) {
        final WorkOrderListItem wo = workOrderListItems.get(position);

        if (wo.getType() != WorkOrderListItem.Type.SECTION) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onWorkOrderSelected(wo.getWorkOrder());
                }
            });

            holder.phaseId.setText(wo.getWorkOrder().getProposalPhase());
            holder.description.setText(wo.getWorkOrder().getDescription());

            holder.priority.setText(wo.getWorkOrder().getPriority());
            switch (wo.getWorkOrder().getPriority()) {
                case "TIME SENSITIVE":
                    //holder.priorityIcon.setImageResource(R.drawable.priority_time_sensitive);
                    holder.priority.setBackgroundColor(context.getResources().getColor(R.color.timeSensitive_yellow));
                    break;
                case "URGENT":
                    //holder.priorityIcon.setImageResource(R.drawable.priority_urgent);
                    holder.priority.setBackgroundColor(context.getResources().getColor(R.color.urgent_orange));
                    break;
                case "EMERGENCY":
                    //holder.priorityIcon.setImageResource(R.drawable.priority_emergency);
                    holder.priority.setBackgroundColor(context.getResources().getColor(R.color.emergency_red));
                    break;
                case "ROUTINE":
                    //holder.priorityIcon.setImageResource(R.drawable.priority_none);
                    holder.priority.setBackgroundColor(context.getResources().getColor(R.color.routine_green));
                    break;
                case "SCHEDULED":
                    //holder.priorityIcon.setImageResource(R.drawable.priority_none);
                    holder.priority.setBackgroundColor(context.getResources().getColor(R.color.scheduled_blue));
                    break;

            }

            holder.valueAgo.setText(wo.getWorkOrder().getDateElements()[3]);
            holder.stringAgo.setText(wo.getWorkOrder().getDateElements()[4]);
        } else {
            Log.d(TAG, "Type section: " + position);
            Typeface FONTAWESOME = Typeface.createFromAsset(context.getAssets(), "fonts/FontAwesome.otf");
            holder.sectionIcon.setText(wo.getSectionIcon());
            holder.sectionIcon.setTypeface(FONTAWESOME);
            holder.sectionTitle.setText(wo.getSectionTitle());
            holder.count.setText(String.valueOf(wo.getSectionCount()));

        }

    }

    @Override
    public int getItemViewType(int position) {
        return (workOrderListItems.get(position).getType() == WorkOrderListItem.Type.SECTION) ? WorkOrderListItem.SECTION_ITEM : WorkOrderListItem.WORK_ORDER_ITEM;
    }

    @Override
    public int getItemCount() {
        return workOrderListItems.size();
    }

    private void initListItems() {
        wrapper = new WorkOrderListWrapper(workOrders);
        workOrderListItems = wrapper.getWorkOrderListItems();
    }

    public ArrayList<WorkOrderListItem> getWorkOrderListItems() {
        return workOrderListItems;
    }

    public static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        protected TextView phaseId;
        protected TextView description;
        protected ImageView priorityIcon;
        protected TextView valueAgo;
        protected TextView stringAgo;
        protected TextView sectionIcon;
        protected TextView sectionTitle;
        protected TextView count;
        protected TextView priority;

        public WorkOrderViewHolder(View v) {
            super(v);
            phaseId = (TextView) v.findViewById(R.id.row_proposal);
            description = (TextView) v.findViewById(R.id.row_description);
            priorityIcon = (ImageView) v.findViewById(R.id.imageView_priorityIconOverview);
            valueAgo = (TextView) v.findViewById(R.id.actionRow_valueAgo);
            stringAgo = (TextView) v.findViewById(R.id.actionRow_stringAgo);
            sectionIcon = (TextView) v.findViewById(R.id.listItem_section_icon);
            sectionTitle = (TextView) v.findViewById(R.id.listItem_section);
            count = (TextView) v.findViewById(R.id.list_item_section_count);
            priority = (TextView) v.findViewById(R.id.row_priority);
        }
    }


    public void flushFilter() {
        initListItems();
        notifyDataSetChanged();

        if (workOrderListItemsOrig != null) workOrderListItemsOrig.clear();
    }

    public int getNumSections(){
        return  numSections;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterReturn = new FilterResults();

                final List<WorkOrderListItem> results = new ArrayList<WorkOrderListItem>();

                if (workOrderListItemsOrig == null) {
                    workOrderListItemsOrig = new ArrayList<>();
                    workOrderListItemsOrig.addAll(workOrderListItems);
                } else if (workOrderListItemsOrig.isEmpty()) {
                    workOrderListItemsOrig.addAll(workOrderListItems);
                }

                int dailyCount = 0;
                int backlogCount = 0;
                int adminCount = 0;
                int completedCount = 0;

                if (constraint != null) {
                    if (workOrderListItemsOrig != null && workOrderListItemsOrig.size() > 0) {
                        for (WorkOrderListItem item : workOrderListItemsOrig) {
                            if (item.getType() != WorkOrderListItem.Type.SECTION) {
                                if (item.getWorkOrder().getDescription().toLowerCase().contains(constraint.toString().toLowerCase())) { // Add only if matched string
                                    results.add(item);

                                    //Recalculate section counts for filtered list
                                    if (item.getWorkOrder().getSection().equals(Constants.SECTION_DAILY)) {
                                        dailyCount++;
                                    } else if (item.getWorkOrder().getSection().equals(Constants.SECTION_BACKLOG)) {
                                        backlogCount++;
                                    } else if (item.getWorkOrder().getSection().equals(Constants.SECTION_ADMIN)) {
                                        adminCount++;
                                    } else if (item.getWorkOrder().getSection().equals(Constants.SECTION_COMPLETED)) {
                                        completedCount++;
                                    }
                                }
                            } else { // Always add section to filtered list
                                results.add(item);
                            }
                        }


                        for (WorkOrderListItem item : results) {
                            if (item.getType() == WorkOrderListItem.Type.SECTION) {
                                if (item.getSectionTitle().equals(Constants.SECTION_DAILY)) {
                                    item.setSectionCount(dailyCount);
                                } else if (item.getSectionTitle().equals(Constants.SECTION_BACKLOG)) {
                                    item.setSectionCount(backlogCount);
                                } else if (item.getSectionTitle().equals(Constants.SECTION_ADMIN)) {
                                    item.setSectionCount(adminCount);
                                } else if (item.getSectionTitle().equals(Constants.SECTION_COMPLETED)) {
                                    item.setSectionCount(completedCount);
                                }
                            }
                        }
                    }
                    filterReturn.values = results;
                }

                return filterReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                workOrderListItems.clear();
                workOrderListItems.addAll((ArrayList<WorkOrderListItem>) results.values);
                notifyDataSetChanged();
            }

        };
    }



    public class WorkOrderListWrapper {
        private static final String TAG = "AiM_WorkOrderListWrapper";
        private ArrayList<WorkOrder> workOrders;
        private ArrayList<WorkOrderListItem> workOrderListItems;


        private int[] sectionIndex;

        public WorkOrderListWrapper(ArrayList<WorkOrder> workOrders) {
            this.workOrders = workOrders;
            sectionIndex = new int[4];
            init();
        }

        private void init(){
            ArrayList<WorkOrder> daily, backlog, admin, completed;
            daily = new ArrayList<>();
            backlog = new ArrayList<>();
            admin = new ArrayList<>();
            completed = new ArrayList<>();

            for (int i = 0; i < workOrders.size(); i++) {
                WorkOrder wo = workOrders.get(i);
                //Log.d(TAG, "Work order: " + wo.getProposalPhase() + " :: --> " + wo.getSectionNum());
                switch (wo.getSectionNum()){
                    case WorkOrder.DAILY_SECTION_ID:
                        daily.add(wo);
                        //Log.d(TAG, "Daily added!");
                        break;
                    case WorkOrder.BACKLOG_SECTION_ID:
                        backlog.add(wo);
                        //Log.d(TAG, "Backlog added!");
                        break;
                    case WorkOrder.ADMIN_SECTION_ID:
                        admin.add(wo);
                        //Log.d(TAG, "Admin added!");
                        break;
                    case WorkOrder.RECENTLY_COMPLETED_SECTION_ID:
                        completed.add(wo);
                        //Log.d(TAG, "Recently added!");
                        break;
                    default:
                }
            }

            //Log.d(TAG, "Array sizes. daily: " + daily.size() + ", admin: " + admin.size() + ", backlog: " + backlog.size() + " completed: " + completed.size());
            workOrderListItems = new ArrayList<>();
            sectionIndex[WorkOrder.DAILY_SECTION_ID] = workOrderListItems.size();
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, Constants.SECTION_DAILY, R.string.icon_daily, null, daily.size()));
            for (int i = 0; i < daily.size(); i++) {
                workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, daily.get(i), 0));
            }
            sectionIndex[WorkOrder.BACKLOG_SECTION_ID] = workOrderListItems.size();
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, Constants.SECTION_BACKLOG, R.string.icon_backlog, null, backlog.size()));
            for (int i = 0; i < backlog.size(); i++) {
                workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, backlog.get(i), 0));
            }
            sectionIndex[WorkOrder.ADMIN_SECTION_ID] = workOrderListItems.size();
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, Constants.SECTION_ADMIN, R.string.icon_admin, null, admin.size()));
            for (int i = 0; i < admin.size(); i++) {
                workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, admin.get(i), 0));
            }
            sectionIndex[WorkOrder.RECENTLY_COMPLETED_SECTION_ID] = workOrderListItems.size();
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, Constants.SECTION_COMPLETED, R.string.icon_recentlyCompleted, null, completed.size()));
            for (int i = 0; i < completed.size(); i++) {
                workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, completed.get(i), 0));
            }
        }

        public ArrayList<WorkOrderListItem> getWorkOrderListItems() {
            //Log.d(TAG, "Returning getWorkOrderList: " + workOrderListItems.size());
            return workOrderListItems;
        }

        public int getSectionIndex(int i){
            return sectionIndex[i];
        }
    }





}

