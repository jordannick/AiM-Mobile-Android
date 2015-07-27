package edu.oregonstate.AiMLiteMobile.Models;

import java.io.Serializable;
import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 7/8/2015.
 */
public class WorkOrderListDataBean implements Serializable {
    private ArrayList<WorkOrder> workOrders;

    public WorkOrderListDataBean(ArrayList<WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    public ArrayList<WorkOrder> getWorkOrders() {
        return workOrders;
    }
}
