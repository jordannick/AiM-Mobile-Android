package edu.oregonstate.AiMLiteMobile;

import android.util.Log;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrderListItem;

/**
 * Created by sellersk on 6/24/2015.
 */
public class WorkOrderListWrapper {
    private static final String TAG = "WorkOrderListWrapper";
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
            Log.d(TAG, "Work order: " + wo.getProposalPhase() + " :: --> " + wo.getSectionNum());
            switch (wo.getSectionNum()){
                case WorkOrder.DAILY_SECTION_ID:
                    daily.add(wo);
                    Log.d(TAG, "Daily added!");
                    break;
                case WorkOrder.BACKLOG_SECTION_ID:
                    backlog.add(wo);
                    Log.d(TAG, "Backlog added!");
                    break;
                case WorkOrder.ADMIN_SECTION_ID:
                    admin.add(wo);
                    Log.d(TAG, "Admin added!");
                    break;
                case WorkOrder.RECENTLY_COMPLETED_SECTION_ID:
                    completed.add(wo);
                    Log.d(TAG, "Recently added!");
                    break;
                default:
            }
        }

        Log.d(TAG, "Array sizes. daily: " + daily.size() + ", admin: " + admin.size() + ", backlog: " + backlog.size() + " completed: " + completed.size());
        workOrderListItems = new ArrayList<>();
        sectionIndex[WorkOrder.DAILY_SECTION_ID] = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Daily", R.string.icon_daily, null, daily.size()));
        for (int i = 0; i < daily.size(); i++) {
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, daily.get(i), 0));
        }
        sectionIndex[WorkOrder.BACKLOG_SECTION_ID] = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Backlog", R.string.icon_backlog, null, backlog.size()));
        for (int i = 0; i < backlog.size(); i++) {
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, backlog.get(i), 0));
        }
        sectionIndex[WorkOrder.ADMIN_SECTION_ID] = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Admin", R.string.icon_admin, null, admin.size()));
        for (int i = 0; i < admin.size(); i++) {
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, admin.get(i), 0));
        }
        sectionIndex[WorkOrder.RECENTLY_COMPLETED_SECTION_ID] = workOrderListItems.size();
        workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.SECTION, "Recently Completed", R.string.icon_recentlyCompleted, null, completed.size()));
        for (int i = 0; i < completed.size(); i++) {
            workOrderListItems.add(new WorkOrderListItem(WorkOrderListItem.Type.ITEM, null, -1, completed.get(i), 0));
        }
    }

    public ArrayList<WorkOrderListItem> getWorkOrderListItems() {
        Log.d(TAG, "Returning getWorkOrderList: " + workOrderListItems.size());
        return workOrderListItems;
    }

    public int getSectionIndex(int i){
        return sectionIndex[i];
    }
}
