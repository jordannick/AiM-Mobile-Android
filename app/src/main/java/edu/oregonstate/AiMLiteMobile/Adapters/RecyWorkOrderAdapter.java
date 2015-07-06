
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

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListWrapper;


/**
 * Created by sellersk on 6/15/2015.
 */

public class RecyWorkOrderAdapter extends RecyclerView.Adapter<RecyWorkOrderAdapter.WorkOrderViewHolder> implements Filterable {
    private static final String TAG = "AiM_RecyWorkOrderAdapter";

    private ArrayList<WorkOrderListItem> workOrderListItems;
    private ArrayList<WorkOrderListItem> workOrderListItemsOrig;
    private ArrayList<WorkOrder> workOrders;
    private Context context;
    public Callbacks mCallbacks;
    public WorkOrderListWrapper wrapper;

    public interface Callbacks {
        void onWorkOrderSelected(WorkOrder wo);
    }


    public RecyWorkOrderAdapter(ArrayList<WorkOrder> workOrders, Activity activity) {
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
        if (viewType == 1) {
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_workorder, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "normal item height: " + v.getHeight());
                }
            });

        } else {
            view = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_section, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "section item height: " + v.getHeight());
                }
            });
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

            switch (wo.getWorkOrder().getPriority()) {
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
        return (workOrderListItems.get(position).getType() == WorkOrderListItem.Type.SECTION) ? 0 : 1;
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
        }
    }


    public void flushFilter() {
        initListItems();
        notifyDataSetChanged();

        if (workOrderListItemsOrig != null) workOrderListItemsOrig.clear();
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
                                    if (item.getWorkOrder().getSection().equals("Daily")) {
                                        dailyCount++;
                                    } else if (item.getWorkOrder().getSection().equals("Backlog")) {
                                        backlogCount++;
                                    } else if (item.getWorkOrder().getSection().equals("Admin")) {
                                        adminCount++;
                                    } else if (item.getWorkOrder().getSection().equals("Recently Completed")) {
                                        completedCount++;
                                    }
                                }
                            } else { // Always add section to filtered list
                                results.add(item);
                            }
                        }


                        for (WorkOrderListItem item : results) {
                            if (item.getType() == WorkOrderListItem.Type.SECTION) {
                                if (item.getSectionTitle().equals("Daily")) {
                                    item.setSectionCount(dailyCount);
                                } else if (item.getSectionTitle().equals("Backlog")) {
                                    item.setSectionCount(backlogCount);
                                } else if (item.getSectionTitle().equals("Admin")) {
                                    item.setSectionCount(adminCount);
                                } else if (item.getSectionTitle().equals("Recently Completed")) {
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


}

