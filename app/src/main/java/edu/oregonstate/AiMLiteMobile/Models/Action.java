package edu.oregonstate.AiMLiteMobile.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sellersk on 2/19/2015.
 */
public class Action implements Serializable {
    private static final String TAG = "AiM_Action";

    private UUID actionId;
    private WorkOrder mWorkOrder;
    private String actionTaken;
    private String updatedStatus;
    private double hours;
    private ArrayList<Note> notes;
    private String note;
    private Date dateStamp;
    private String timeType;
    private boolean submitted;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getActionId() {
        return actionId;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

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

    // Used for editing an existing action object
    public void replaceValues(String actionTakenString, String updatedStatus, double hours, String timeType, String note) {
        this.actionTaken = actionTakenString;
        this.updatedStatus = updatedStatus;
        this.hours = hours;
        this.note = note;
        this.timeType = timeType;
    }

    public Action(WorkOrder workOrder, String actionTakenString, String updatedStatus, double hours, String timeType, String note) {
        this.actionId = UUID.randomUUID();
        this.mWorkOrder = workOrder;
        this.actionTaken = actionTakenString;
        this.updatedStatus = updatedStatus;
        this.hours = hours;
        this.note = note;
        this.dateStamp = new Date(System.currentTimeMillis());
        this.submitted = false;
        this.timeType = timeType;
    }


}
