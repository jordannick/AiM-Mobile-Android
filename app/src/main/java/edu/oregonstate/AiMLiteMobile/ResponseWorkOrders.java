package edu.oregonstate.AiMLiteMobile;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 6/10/2015.
 */
public class ResponseWorkOrders {

    private ArrayList<WorkOrder> workOrders;
    private String rawJson;

    public ResponseWorkOrders(ArrayList<WorkOrder> workOrders, String rawJson) {
        this.workOrders = workOrders;
        this.rawJson = rawJson;
    }

    public ArrayList<WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public String getRawJson() {
        return rawJson;
    }
}
