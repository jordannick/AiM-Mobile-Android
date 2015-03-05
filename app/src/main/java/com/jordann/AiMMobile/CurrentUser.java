package com.jordann.AiMMobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Date;
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
    private long lastRefresh;

    public String URLGetAll;
    public String URLGetLastUpdated;
    public String URLGetNotices;

    private String urlBase = "http://api-test.facilities.oregonstate.edu";
    private String urlObject = "WorkOrder";
    private String urlAPI = "1.0";





    //Action Queue variables
    private ArrayList<Action> mActions;


    //TODO: remove test
    private void setTestListData(int num){
        for(int i = 0; i < num; i++){
            //mActions.add(new Action(UUID.randomUUID()));
        }
    }


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
            prefs = sAppContext.getSharedPreferences("com.jordann.AiMMobile", Context.MODE_PRIVATE);
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
        lastRefresh = 0;
        //TODO: remove test
        setTestListData(10);
        Log.d(TAG, "CurrentUser constructor");
        buildUrls();
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

    private void buildUrls(){
        URLGetAll = urlBase + '/' + urlAPI + '/' + urlObject + "/getAll/";
        URLGetNotices = urlBase + '/' + urlAPI + '/' + urlObject + "/getNotices/";
        URLGetLastUpdated =  urlBase + '/' + urlAPI + '/' + urlObject + "/getLastUpdated/";

        /*temp*/
        URLGetAll = "http://portal.campusops.oregonstate.edu/aim/api/1.0.0/getWorkOrders/";
    }

    public void getCurrentRefresh(){
        lastRefresh = System.currentTimeMillis();
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

    public void appendUsernameURLs(){
        URLGetAll += mUsername;
        URLGetNotices += mUsername;
        URLGetLastUpdated += mUsername;
    }


    public ArrayList<Action> getActions() {
        return mActions;
    }

    public void addAction(Action action) {
        mActions.add(action);
    }

    public boolean isRefreshNeeded(){
        return System.currentTimeMillis() > lastRefresh;
    }
}
