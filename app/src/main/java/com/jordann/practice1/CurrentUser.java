package com.jordann.practice1;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class CurrentUser {

    private String mUsername;
    private ArrayList<WorkOrder> mWorkOrders;
    private static CurrentUser sCurrentUser;
    private static Context sAppContext;


    private CurrentUser(Context appContext){
        sAppContext = appContext;
        mWorkOrders = new ArrayList<WorkOrder>();
    }

    public static CurrentUser get(Context c){
        if (sCurrentUser == null) {
            sCurrentUser = new CurrentUser(c);
        }
        return sCurrentUser;
    }


    public void addNewWorkOrder(WorkOrder wo){
        mWorkOrders.add(wo);
    }


    public void setWorkOrders(ArrayList<WorkOrder> workOrders) {
        mWorkOrders = workOrders;
    }


    public ArrayList<WorkOrder> getWorkOrders(){
        return mWorkOrders;
    }


    public WorkOrder getWorkOrder(UUID id){
        for (WorkOrder wo : mWorkOrders) {
            if (wo.getId().equals(id))
                return wo;
        }
        return null;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }



}
