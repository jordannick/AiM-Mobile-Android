package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

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

    private String urlBase = "http://api-test.facilities.oregonstate.edu";
    private String urlAPIVersion = "1.0";
    private String urlObject = "WorkOrder";

    private String mCookies;


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
        mWorkOrders.addAll(newWorkOrders);
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

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public Action getAction(int position) {
        return mActions.get(position);
    }

    public void addAction(Action action) {
        mActions.add(action);
    }


    public String getCookies() {
        return mCookies;
    }

    public void setCookies(String mCookies) {
        this.mCookies = mCookies;
    }
}
