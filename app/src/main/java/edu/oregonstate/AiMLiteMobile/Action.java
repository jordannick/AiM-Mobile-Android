package edu.oregonstate.AiMLiteMobile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sellersk on 2/19/2015.
 */
public class Action implements Serializable {
    private static final String TAG = "Action";

    public static final String EDIT_ACTION_EXTRA = "edu.oregonstate.AiMLiteMobile.Action";

    private WorkOrder mWorkOrder;

    private String actionTaken;
    private String updatedStatus;
    private int hours;
    private ArrayList<Note> notes;
    private Date dateStamp;

    public static String action_MaterialRequest = "Material Request";
    public static String status_WorkComplete = "Work Complete";


    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        mWorkOrder = workOrder;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getUpdatedStatus() {
        return updatedStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public Date getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(Date dateStamp) {
        this.dateStamp = dateStamp;
    }

    public Action(WorkOrder workOrder, String actionTaken, String updatedStatus, int hours, ArrayList<Note> notes) {
        this.mWorkOrder = workOrder;
        this.actionTaken = actionTaken;
        this.updatedStatus = updatedStatus;
        this.hours = hours;
        this.notes = notes;
        this.dateStamp = new Date(System.currentTimeMillis());
    }

}
