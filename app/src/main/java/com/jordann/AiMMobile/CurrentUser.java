package com.jordann.AiMMobile;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class CurrentUser {

    private String mUsername;
    private SharedPreferences prefs;
    private ArrayList<WorkOrder> mWorkOrders;
    private static CurrentUser sCurrentUser;
    //private static Context sAppContext;



    public SharedPreferences getPrefs() {
        return prefs;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }


    private CurrentUser(/*Context appContext*/){
        //sAppContext = appContext;
        mWorkOrders = new ArrayList<WorkOrder>();
    }

    public static CurrentUser get(/*Context c*/){
        if (sCurrentUser == null) {
            //sCurrentUser = new CurrentUser(c);
            sCurrentUser = new CurrentUser();
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
