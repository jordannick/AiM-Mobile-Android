package edu.oregonstate.AiMLiteMobile.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jordan_n on 8/13/2014.
 * Maintains the local session of the logged in user.
 */
public class CurrentUser {
    private static final String TAG = "CurrentUser";

    private String mUsername;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ArrayList<WorkOrder> mWorkOrders;
    private ArrayList<Action> mActions;

    private static CurrentUser sCurrentUser;
    private static Context sAppContext;

    public String URLGetAll;
    public String URLGetLastUpdated;
    public String URLGetNotices;
    public String URLLogin;

    //Recently viewed workOrders to display in timeLog
    private ArrayList<WorkOrder> recentlyViewedWorkOrders = new ArrayList<>();
    public final int recentlyViewedMax = 5;

    private String urlBase = "http://api-test.facilities.oregonstate.edu";
    private String urlAPIVersion = "1.0";
    private String urlObject = "WorkOrder";

    private String lastUpdated = "Never";

    private String mToken;

    public void prepareLogout(){
        sCurrentUser.getPrefsEditor().putBoolean("autologin", false);
        sCurrentUser.getPrefsEditor().apply();
    }

    public ArrayList<Action> getUnsyncedActions(){
        ArrayList<Action> unsyncedActions = new ArrayList<>();
        for (int i = 0; i < mActions.size(); i++) {
            if(!mActions.get(i).isSynced()){
                unsyncedActions.add(mActions.get(i));
            }
        }
        Log.d(TAG, "UnsyncedActions built with " + unsyncedActions.size() + " action(s).");
        return unsyncedActions;
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
            work_order_data: string
            notice_data: string
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
    }

    public static CurrentUser get(Context c){
        if (sCurrentUser == null) {
            sCurrentUser = new CurrentUser(c);
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

    public void addNewWorkOrder(WorkOrder wo){
        mWorkOrders.add(wo);
    }

    public void setWorkOrders(ArrayList<WorkOrder> newWorkOrders) {
        //Want to keep same list reference, so just clearing it here and add repopulating
        mWorkOrders.clear();
        //logWorkOrderStatus("PRE", newWorkOrders);
        Collections.sort(newWorkOrders, new Comparator<WorkOrder>() {
            @Override
            public int compare(WorkOrder lhs, WorkOrder rhs) {
                if (lhs.getSectionNum() == rhs.getSectionNum()) {
                    return 0;
                } else if (lhs.getSectionNum() < rhs.getSectionNum()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        //logWorkOrderStatus("POST", newWorkOrders);
        mWorkOrders.addAll(newWorkOrders);
    }

    private void logWorkOrderStatus(String str, ArrayList<WorkOrder> workOrders){
        Log.d(TAG, "BEGIN " + str);
        for (int i = 0; i < workOrders.size(); i++) {
            Log.d(TAG, i + " : sec: " + workOrders.get(i).getSectionNum() + " , " + workOrders.get(i).getBeginDate());

        }
        Log.d(TAG, "END " + str);
    }

    public ArrayList<WorkOrder> getWorkOrders(){
        return mWorkOrders;
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

    public void buildLoginUrl(String username){
        URLLogin = urlBase + '/' + urlAPIVersion + "/User/login" + '/' + username;
    }

    public String getURLLogin(){
        Log.d(TAG, "returning loginURL: " + URLLogin);
        return URLLogin;
    }

    public String getBaseURL(){
        return urlBase+'/'+urlAPIVersion + '/' + urlObject;
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public Action getAction(int position) {
        return mActions.get(position);
    }

    public void addAction(Action action) {
        mActions.add(0, action);
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }


    public void addRecentlyViewedWorkOrder(WorkOrder workOrder){
        //Add workOrder to top of arrayList, removing oldest if newSize > 5

/*
        if(recentlyViewedWorkOrders.size() > 0){
            if (recentlyViewedWorkOrders.get(0).getProposalPhase().equals(workOrder.getProposalPhase())) {
                Log.d(TAG, "Strings equal" +recentlyViewedWorkOrders.get(0).getProposalPhase() + " == " + workOrder.getProposalPhase());
            }else{
                Log.d(TAG, "Strings NOT equal" + recentlyViewedWorkOrders.get(0).getProposalPhase() + " != " + workOrder.getProposalPhase());
            }
        }*/
/*
        String test1 = "Hello";
        String test2 = "Hello";*/

//        if(test1.equals(test2)){
//            Log.d(TAG, "Strings equal");
//        }else{
//            Log.d(TAG, "Strings NOT equal");
//        }
//
//        if(test1.equals("Hello")){
//            Log.d(TAG, "Strings equal");
//        }else{
//            Log.d(TAG, "Strings equal");
//        }



        int index = findIndexWorkOrder(recentlyViewedWorkOrders, workOrder);
        if (index == -1) {
            int curSize = recentlyViewedWorkOrders.size();
            int newSize = curSize + 1;
            if(newSize > recentlyViewedMax){
                //Remove oldest workOrder in arrayList
                recentlyViewedWorkOrders.remove(curSize-1);
            }
            //Add new workOrder to beginning of arrayList
            recentlyViewedWorkOrders.add(0, workOrder);

            //Debug messages
            Log.d(TAG, "workOrder: " + workOrder.getProposalPhase() + " added. New size: " + recentlyViewedWorkOrders.size());
            for (int i = 0; i < recentlyViewedWorkOrders.size(); i++) {
                Log.d(TAG, "-- " + i + " : " + recentlyViewedWorkOrders.get(i));
            }
        }else{
            //WorkOrder already exists, move to front.
            Log.d(TAG, "PRE size : " + recentlyViewedWorkOrders.size());
            recentlyViewedWorkOrders.remove(index);
            Log.d(TAG, "POST size : " + recentlyViewedWorkOrders.size());
            recentlyViewedWorkOrders.add(0, workOrder);
        }
    }

    private int findIndexWorkOrder(ArrayList<WorkOrder> arrayList, WorkOrder workOrder){
        for (int i = 0; i < arrayList.size(); i++) {
            Log.d(TAG, "comparing " + arrayList.get(i).getProposalPhase() + " with " + workOrder.getProposalPhase());
            if (arrayList.get(i).getProposalPhase().equals(workOrder.getProposalPhase())) {
                Log.d(TAG, "compare true");
                return i;
            }
        }
        Log.d(TAG, "compare false");
        return -1;
    }

    public ArrayList<WorkOrder> getRecentlyViewedWorkOrders(){
        return  recentlyViewedWorkOrders;
    }
}
