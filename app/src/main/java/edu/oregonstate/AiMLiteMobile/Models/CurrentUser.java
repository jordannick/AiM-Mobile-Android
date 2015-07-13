package edu.oregonstate.AiMLiteMobile.Models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.oregonstate.AiMLiteMobile.Activities.LoginActivity;
import edu.oregonstate.AiMLiteMobile.Adapters.RecyWorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.InternalStorageWriter;

/**
 * Created by jordan_n on 8/13/2014.
 * Maintains the local session of the logged in user.
 */
public class CurrentUser {
    private static final String TAG = "AiM_CurrentUser";
    private static CurrentUser currentUser;
    private static Context appContext;
    private Preferences preferences;

    private String username;
    private String password;
    private String token;
    private Long lastUpdated;
    private ArrayList<WorkOrder> workOrders;
    private ArrayList<Action> actions;
    private ArrayList<Notice> notices;
    private ArrayList<WorkOrder> recentlyViewedWorkOrders = new ArrayList<>();//Recently viewed workOrders to display in timeLog

    private InternalStorageWriter internalStorageWriter;
    private boolean offlineMode = false;

    public final int RECENTLY_VIEWED_MAX = 5;

    private CurrentUser(Context appContext){
        CurrentUser.appContext = appContext;
        workOrders = new ArrayList<>();
        notices = new ArrayList<>();
        actions = new ArrayList<>();
        preferences = new Preferences(appContext);
    }

    public static CurrentUser get(Context c){
        if (currentUser == null) {
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

    public ArrayList<WorkOrder> getWorkOrders(){
        return workOrders;
    }

    public void backupWorkOrders(ArrayList<WorkOrder> arr){
        Log.d(TAG, "interalStorageWriter: " + internalStorageWriter);
        internalStorageWriter.saveWorkOrders(arr);
    }

    public void loadSavedWorkOrders(RecyWorkOrderAdapter adapter){
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
        if(this.username == null){
            internalStorageWriter = new InternalStorageWriter(appContext, username);
        }
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
            if(newSize > RECENTLY_VIEWED_MAX){
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

        public String getLastUpdated(){
            return prefs.getString("last_updated", "");
        }

        public void saveLastUpdated(Long lastUpdated){
            prefsEditor.putLong("last_updated", lastUpdated);
            prefsEditor.apply();
        }

        public boolean isNewerLastUpdated(Long lastUpdated){
            return true;
        }
    }

}
