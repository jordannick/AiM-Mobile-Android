package edu.oregonstate.AiMLiteMobile.Models;

/**
 * Created by SellersK on 6/1/2015.
 */
public class WorkOrderListItem {
    private static final String TAG = "AiM_WorkOrderListItem";
    public static int numTypes = 2;

    public WorkOrderListItem(Type type, String sectionTitle, int sectionIcon, WorkOrder workOrder, int sectionCount) {
        this.type = type;
        this.sectionIcon = sectionIcon;
        this.sectionTitle = sectionTitle;
        this.workOrder = workOrder;
        this.sectionCount = sectionCount;
    }

    private Type type;
    public enum Type{
        SECTION, ITEM
    }

    private String sectionTitle;
    private int sectionIcon;
    private int sectionCount;
    private WorkOrder workOrder;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public int getSectionIcon() {
        return sectionIcon;
    }

    public void setSectionIcon(int sectionIcon) {
        this.sectionIcon = sectionIcon;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }
}
