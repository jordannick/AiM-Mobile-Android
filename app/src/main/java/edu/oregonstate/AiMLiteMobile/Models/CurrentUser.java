package edu.oregonstate.AiMLiteMobile.Models;

import android.app.Activity;
import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.oregonstate.AiMLiteMobile.Activities.LoginActivity;
import edu.oregonstate.AiMLiteMobile.Adapters.WorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.Constants;
import edu.oregonstate.AiMLiteMobile.Helpers.InternalStorageWriter;

/**
 * Created by jordan_n on 8/13/2014.
 * Maintains the local session of the logged in user.
 */
public class CurrentUser {
    private static final String TAG = "AiM_CurrentUser";
    private static CurrentUser currentUser;
    private static Context appContext;
    private static Preferences preferences;

    private static String username;
    private static String password;
    private static String token;
    private static Long lastUpdated;
    private static Long lastRefreshed;
    private static ArrayList<WorkOrder> workOrders;
    private static ArrayList<Action> actions;
    private static ArrayList<Notice> notices;
    private static ArrayList<WorkOrder> recentlyViewedWorkOrders = new ArrayList<>();//Recently viewed workOrders to display in timeLog

    private int forceRefreshMin = 1;
    private int forceRefreshInterval = 1000 * 60 * forceRefreshMin;

    private InternalStorageWriter internalStorageWriter;
    private boolean offlineMode = false;

    /*public final int RECENTLY_VIEWED_MAX = 5;
    private final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 5;*/

    private CurrentUser(Context appContext){
        CurrentUser.appContext = appContext;
        workOrders = new ArrayList<>();
        notices = new ArrayList<>();
        actions = new ArrayList<>();
        preferences = new Preferences(appContext);
    }

    public static CurrentUser get(Context c){
        Log.d(TAG, "CurrentUser Get " + c);
        if (currentUser == null) {
            Log.d(TAG, "NEW CurrentUser Get "+ c);
            currentUser = new CurrentUser(c);
        }
        return currentUser;
    }

    /* We want the new activity to be the only one, so clear all other activities.
     * Autologin to false prevents instantaneous re-login.
     */
    public Intent createLogoutIntent(Context c){
        Intent intent = new Intent(c,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("autologin", false);
        return intent;
    }


    public boolean isRefreshIntervalExceeded(){
        if(lastRefreshed == null) return false;
        return (System.currentTimeMillis() > lastRefreshed + forceRefreshInterval);
    }

    private void clearAll(){
        workOrders.clear();
        actions.clear();
        notices.clear();
        recentlyViewedWorkOrders.clear();
        lastUpdated = null;
    }

    /* Creates a new AlertDialog, displays it.
            Prompts the user to confirm Logout or Cancel. */
    public void logoutUser(final Activity activity){
        AlertDialog logoutDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Are you done?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clearAll();
                activity.startActivity(createLogoutIntent(activity));
                activity.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        logoutDialog = builder.create();


        logoutDialog.show();
        //logoutDialog.getWindow().setLayout(600, 400);


    }

    public void forceLogout(Activity activity){
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("autologin", true);
        clearAll();
        activity.startActivity(intent);
        activity.finish();
    }

    public ArrayList<WorkOrder> getWorkOrders(){
        return workOrders;
    }

    public void backupWorkOrders(ArrayList<WorkOrder> arr){
        Log.d(TAG, "interalStorageWriter: " + internalStorageWriter);
        internalStorageWriter.saveWorkOrders(arr);
    }

    public void loadSavedWorkOrders(WorkOrderAdapter adapter){
        ArrayList<WorkOrder> workOrders = internalStorageWriter.retrieveWorkOrders();
        this.workOrders = workOrders;
        adapter.refreshWorkOrders(workOrders);
    }




    public void setWorkOrders(ArrayList<WorkOrder> newWorkOrders) {
        //Want to keep same list reference, so clear and repopulate
        workOrders.clear();
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
        workOrders.addAll(newWorkOrders);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        internalStorageWriter = new InternalStorageWriter(appContext, username);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String mPassword) {
        this.password = mPassword;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public Action getAction(int position) {
        return actions.get(position);
    }

    public void addAction(Action action) {
        actions.add(0, action);
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isUpdateNeeded(Long lastUpdated){
        if(this.lastUpdated == null) this.lastUpdated = 0L;
        if(lastUpdated > this.lastUpdated){
            this.lastUpdated = lastUpdated;
            return true;
        }else{
            return false;
        }
    }

    public boolean isUpdateExpired(Long currentTime){
        if(currentTime > (lastUpdated+ Constants.EXPIRATION_TIME)){
            this.lastUpdated = currentTime;
            return true;
        }else{
            return false;
        }
    }

    public Long getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(Long lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    public String getFormattedRefreshAgoString(){
        if(lastRefreshed == null) return "Unavailable";
        int divisor = 1000*60;
        int diff = (int)((System.currentTimeMillis() - lastRefreshed)/divisor); //Minutes

        int minutesInDay = 60*24;
        int minutesInHour = 60;
        if(diff < 1) return "Updated seconds ago";
        if(diff < 60){
            if(diff == 1) return "Updated " + diff + " minute ago";
            return "Updated " + diff + " minutes ago";
        }else if(diff >= minutesInDay){
            diff /= minutesInDay;
            if(diff == 1) return "Updated " + diff + " day ago";
            return "Updated " + diff + " days ago";
        }else { //Hours
            diff /= minutesInHour;
            if(diff == 1) return "Updated " + diff + " hour ago";
            return "Updated " + diff + " hours ago";
        }
    }

/*    public void testGetFormattedRefreshAgoString(int iterations, int dayMax, int hourMax, int minuteMax){
        String DTAG = "DEBUG_FORMAT";
        int i = iterations;
        Random rand = new Random();
        long msInDay = 1000 * 60 * 60 * 24;
        long msInHour = 1000 * 60 * 60;
        long msInMinute = 1000 * 60;
        while (i > 0){
            Long time = System.currentTimeMillis();
            int dayRemove = rand.nextInt(dayMax);
            int hourRemove = rand.nextInt(hourMax);
            int minuteRemove = rand.nextInt(minuteMax);
            time -= dayRemove*msInDay + hourRemove*msInHour + minuteRemove*msInMinute;
            Log.d(DTAG, "dayRemove: " + dayRemove + ", hourRemove: " + hourRemove + ", minuteRemove: " + minuteRemove + " ::: " + getFormattedRefreshAgoString(time));
            i--;
        }
    }*/

    public String getToken() {
        return token;
    }

    public void setToken(String newToken) {
        token = newToken;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public ArrayList<WorkOrder> getRecentlyViewedWorkOrders(){
        return recentlyViewedWorkOrders;
    }

    public void addRecentlyViewedWorkOrder(WorkOrder workOrder){
        //Add workOrder to top of arrayList, removing oldest if newSize > RECENTLY_VIEWED_MAX
        int index = -1;
        Log.d(TAG, "Work order for addRecently: " + workOrder.getProposalPhase());
        for (int i = 0; i < recentlyViewedWorkOrders.size(); i++) {
            if (recentlyViewedWorkOrders.get(i).getProposalPhase().equals(workOrder.getProposalPhase())) {
                index = i;
            }
        }

        if (index == -1) {
            int curSize = recentlyViewedWorkOrders.size();
            int newSize = curSize + 1;
            if(newSize > Constants.RECENTLY_VIEWED_MAX){
                //Remove oldest workOrder in arrayList
                recentlyViewedWorkOrders.remove(curSize-1);
            }
            //Add new workOrder to beginning of arrayList
            recentlyViewedWorkOrders.add(0, workOrder);
        } else {
            //WorkOrder already exists, move to front.
            recentlyViewedWorkOrders.remove(index);
            recentlyViewedWorkOrders.add(0, workOrder);
        }
    }

    public ArrayList<Notice> getNotices() {
        return notices;
    }

    public void setNotices(ArrayList<Notice> mNotices) {
        this.notices = mNotices;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public class Preferences {
        /* SharedPreferences object contents:
                    autologin: bool
                    username: string
                    password: string
                    work_order_data: string
                    notice_data: string
                    last_updated: long
            */
        private SharedPreferences prefs;
        private SharedPreferences.Editor prefsEditor;

        public Preferences(Context c) {
            prefs = c.getSharedPreferences("edu.oregonstate.AiMLiteMobile", Context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        public boolean getAutoLogin(){
            return prefs.getBoolean("autologin", false);
        }

        public void saveAutoLogin(String username, String password) {
            prefsEditor.putString("username", username);
            prefsEditor.putString("password", password);
            prefsEditor.putBoolean("autologin", true);
            prefsEditor.apply();
        }

        public String getUsername(){
            return prefs.getString("username", "");
        }

        public String getPassword(){
            return prefs.getString("password", "");
        }

        public void saveUserCredentials(String username, String password){
            prefsEditor.putString("username", username);
            prefsEditor.putString("password", password);
            prefsEditor.apply();
        }

        public void saveWorkOrders(String rawJson){
            prefsEditor.putString("work_order_data", rawJson);
            prefsEditor.apply();
        }

        public Long getLastUpdated(){
            return prefs.getLong(username.toLowerCase() + "_last_updated", 0);
        }

        public void saveLastUpdated(Long lastUpdated){
            String key = username.toLowerCase() + "_last_updated";
            prefsEditor.putLong(key, lastUpdated);
            prefsEditor.apply();
        }

        public boolean isNewerLastUpdated(Long lastUpdated){
            return true;
        }
    }

}
