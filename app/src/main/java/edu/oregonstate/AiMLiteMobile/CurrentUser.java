package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    public String URLLogin;

    private String urlBase = "http://api-test.facilities.oregonstate.edu";
    private String urlAPIVersion = "1.0";
    private String urlObject = "WorkOrder";

    private String mCookies;

    private String lastUpdated = "Never";

    private String mToken;

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

    public void buildLoginUrl(String username){
        URLLogin = urlBase + '/' + urlAPIVersion + "/User/login" + '/' + username;
    }

    public String getURLLogin(){
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
        mActions.add(action);
    }


    public String getCookies() {
        return mCookies;
    }

    public void setCookies(String mCookies) {
        this.mCookies = mCookies;
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
}
