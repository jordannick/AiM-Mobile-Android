package edu.oregonstate.AiMLiteMobile.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sellersk on 2/19/2015.
 */
public class Action implements Serializable {
    private static final String TAG = "Action";
    public static final String EDIT_ACTION_EXTRA = "edu.oregonstate.AiMLiteMobile.Models.Action";

    private WorkOrder mWorkOrder;
    private String actionTakenString;
    private String updatedStatus;
    private int hours;
    private ArrayList<Note> notes;
    private Date dateStamp;

    /* ---------- Variable: TimeType ----------
    *  OTM_NB   Overtime
    *  REG_NB   Regular Time
    *  RST_NB   Student Regular Pay
    *  RWS_NB   Federal Work Study Student Pay   */
    public enum TimeType {
        OTM_NB, REG_NB, RST_NB, RWS_NB
    }

    /* ---------- Variable: ActionTaken ----------
        Also found in Arrays.xml
        ANY CHANGES made here must be ALSO CHANGED FOR Arrays.XML
     */
    public enum ActionTaken {
        NO_ACTION, MATERIAL_REQUEST, TEAM_BONDING, CRITICAL_ACTION, PREVENTIVE_MAINTENANCE, CUSTOM
    }

    private boolean synced;
    private ActionTaken actionTaken;
    private TimeType timeType;

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public WorkOrder getWorkOrder() {
        return mWorkOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        mWorkOrder = workOrder;
    }

    public String getActionTakenString() {
        return actionTakenString;
    }

    public void setActionTakenString(String actionTakenString) {
        this.actionTakenString = actionTakenString;
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


    public ActionTaken getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(ActionTaken actionTaken) {
        this.actionTaken = actionTaken;
    }

    public Action(WorkOrder workOrder, String actionTakenString, String updatedStatus, int hours, ArrayList<Note> notes) {
        this.mWorkOrder = workOrder;
        this.actionTakenString = actionTakenString;
        this.actionTaken = getMatchingEnumForActionTaken(actionTakenString);
        if(updatedStatus != null) { //If not null, the status has been updated.
            this.updatedStatus = updatedStatus;
        }
        this.hours = hours;
        this.notes = notes;
        this.dateStamp = new Date(System.currentTimeMillis());
        this.synced = false;
    }


    /* FUNCTION
            Applies string to ENUM for ActionTaken
     */
    private Action.ActionTaken getMatchingEnumForActionTaken(String actionTaken){
        switch (actionTaken){
            case "No Action":
                return Action.ActionTaken.NO_ACTION;
            case "Material Request":
                return Action.ActionTaken.MATERIAL_REQUEST;
            case "Team Bonding":
                return Action.ActionTaken.TEAM_BONDING;
            case "Critical Action":
                return Action.ActionTaken.CRITICAL_ACTION;
            case "Preventive Maintenance":
                return Action.ActionTaken.PREVENTIVE_MAINTENANCE;
            case "Custom":
                return Action.ActionTaken.CUSTOM;
            default:
                return null;
        }
    }
}
