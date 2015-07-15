package edu.oregonstate.AiMLiteMobile;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 7/14/2015.
 */
public class WorkOrderStatistics {

    private ArrayList<WorkOrder> workOrders;
    private String username;

    public WorkOrderStatistics(ArrayList<WorkOrder> workOrders, String username) {
        this.workOrders = workOrders;
        this.username = username;
        calculateStats();
    }

    public void printStats(){



    }

    private void calculateStats(){




        printStats();
    }


}
