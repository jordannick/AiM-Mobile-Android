package edu.oregonstate.AiMLiteMobile.Models;

/**
 * Created by SellersK on 6/1/2015.
 */
public class WorkOrderListItem {

    public static int numTypes = 2;

    public WorkOrderListItem(Type type, String sectionTitle, WorkOrder workOrder) {
        this.type = type;
        this.sectionTitle = sectionTitle;
        this.workOrder = workOrder;
    }

    private Type type;
    public enum Type{
        SECTION, ITEM
    }

    private String sectionTitle;
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
}
