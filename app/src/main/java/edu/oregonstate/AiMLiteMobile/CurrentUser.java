package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class CurrentUser {
    private static final String TAG = "CurrentUser";
    private String mUsername;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ArrayList<WorkOrder> mWorkOrders;

    private static CurrentUser sCurrentUser;
    private static Context sAppContext;
   // private long lastRefresh;

    public String URLGetAll;
    public String URLGetLastUpdated;
    public String URLGetNotices;

    private String urlBase = "http://api-test.facilities.oregonstate.edu";
    private String urlAPIVersion = "1.0";
    private String urlObject = "WorkOrder";

    //Action Queue variables
    private ArrayList<Action> mActions;
    private Action currentActionToEdit;


    public SharedPreferences.Editor getPrefsEditor() {
        if (prefsEditor == null) {
            prefsEditor = getPrefs().edit();
        }
        return prefsEditor;
    }

    /* SharedPreferences object contents:
            autologin: bool
            username: string
            password: string
            jsondata: string
    */
    public SharedPreferences getPrefs() {
        if (prefs == null) {
            prefs = sAppContext.getSharedPreferences("edu.oregonstate.AiMLiteMobile", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    private CurrentUser(Context appContext){
        sAppContext = appContext;
        mWorkOrders = new ArrayList<WorkOrder>();
        mActions = new ArrayList<Action>();
        //lastRefresh = 0;
    }

    public static CurrentUser get(Context c){
        if (sCurrentUser == null) {
            sCurrentUser = new CurrentUser(c);
            //sCurrentUser = new CurrentUser();
        }
        return sCurrentUser;
    }

    public String getURLGetAll() {
        return URLGetAll;
    }

    public String getURLGetLastUpdated() {
        return URLGetLastUpdated;
    }

    public String getURLGetNotices() {
        return URLGetNotices;
    }

    /*
    public void getCurrentRefresh(){
        lastRefresh = System.currentTimeMillis();
    }
*/

    public void addNewWorkOrder(WorkOrder wo){
        mWorkOrders.add(wo);
    }

    public void setWorkOrders(ArrayList<WorkOrder> newWorkOrders) {
        //mWorkOrders = workOrders;
        //Want to keep same reference to list, so just clear it and add anew
        mWorkOrders.clear();
        mWorkOrders.addAll(newWorkOrders);
    }

    public ArrayList<WorkOrder> getWorkOrders(){
        return mWorkOrders;
    }

    public WorkOrder getWorkOrder(/*UUID id*/String proposalPhase){
        for (WorkOrder wo : mWorkOrders) {
            /*if (wo.getId().equals(id)) {
                return wo;
            }*/
            if (wo.getProposalPhase().equals(proposalPhase)){
                return wo;
            }
        }
        return null;
    }

    public String getUsername() {
        return mUsername;

    }

    public void setUsername(String username) {
        mUsername = username;

    }

    public void buildUrlsWithUsername(){
        URLGetAll = urlBase + '/' + urlAPIVersion + '/' + urlObject + "/getAll/" + mUsername;
        URLGetNotices = urlBase + '/' + urlAPIVersion + '/' + urlObject + "/getNotices/" + mUsername;
        URLGetLastUpdated =  urlBase + '/' + urlAPIVersion + '/' + urlObject + "/getLastUpdated/" + mUsername;
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public Action getAction(int position) {
        return mActions.get(position);
    }

    public void addAction(Action action) {
        mActions.add(action);
    }

}
