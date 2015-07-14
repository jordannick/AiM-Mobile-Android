package edu.oregonstate.AiMLiteMobile.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sellersk on 2/19/2015.
 */
public class Action implements Serializable {
    private static final String TAG = "AiM_Action";
    public static final String EDIT_ACTION_EXTRA = "edu.oregonstate.AiMLiteMobile.Models.Action";

    private WorkOrder mWorkOrder;
    private String actionTaken;
    private String updatedStatus;
    private double hours;
    private ArrayList<Note> notes;
    private Date dateStamp;
    private String timeType;
    private boolean submitted;

    /* ---------- Variable: TimeType ----------
    *  OTM_NB   Overtime
    *  REG_NB   Regular Time
    *  RST_NB   Student Regular Pay
    *  RWS_NB   Federal Work Study Student Pay   */
    /*public enum TimeType {
        OTM_NB, REG_NB, RST_NB, RWS_NB
    }*/

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    private boolean synced;
    //private TimeType timeType;

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

   /* public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }*/

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        mWorkOrder = workOrder;
    }

    public String getUpdatedStatus() {
        return updatedStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
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


    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public Action(WorkOrder workOrder, String actionTakenString, String updatedStatus, double hours, String timeType, ArrayList<Note> notes) {
        this.mWorkOrder = workOrder;
        this.actionTaken = actionTakenString;
        if(updatedStatus != null) { //If not null, the status has been updated.
            this.updatedStatus = updatedStatus;
        }
        this.hours = hours;
        this.notes = notes;
        this.dateStamp = new Date(System.currentTimeMillis());
        this.synced = false;
        this.submitted = false;
        this.timeType = timeType;
    }


}
