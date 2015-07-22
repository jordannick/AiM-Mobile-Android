package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 7/7/2015.
 */
public class InternalStorageWriter {
    private static final String TAG = "InternalStorageWriter";


    private Context context;
    private String filename_workOrders;




    public InternalStorageWriter(Context context, String username) {
        this.context = context;
        filename_workOrders = username.toLowerCase() + "_workOrders";
    }

    /* Saves a new WorkOrderListDataBean object to internal storage, overwriting an existing one.
    *  @param workOrders: input arrayList to be saved
    */
    public void saveWorkOrders(ArrayList<WorkOrder> workOrders){
        Log.d(TAG, "Saving workOrders for " + filename_workOrders);
        try {
            WorkOrderListDataBean dataBean = new WorkOrderListDataBean(workOrders);
            FileOutputStream fos = context.openFileOutput(filename_workOrders, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(dataBean);
            os.flush();
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<WorkOrder> retrieveWorkOrders() {
        ArrayList<WorkOrder> workOrders = null;
        try {
            FileInputStream fis = context.openFileInput(filename_workOrders);
            ObjectInputStream is = new ObjectInputStream(fis);
            WorkOrderListDataBean dataBean = (WorkOrderListDataBean)is.readObject();
            workOrders = dataBean.getWorkOrders();
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workOrders;
    }

    public static boolean hasSavedData(Context context, String username){
        ArrayList<WorkOrder> workOrders = null;
        String filename = username.toLowerCase() + "_workOrders";


        try {


            FileInputStream fis = context.openFileInput(filename);
            //ObjectInputStream is = new ObjectInputStream(fis);   //Unneeded for availability check
            if (fis.available() > 0){
                return true;
            }
            //is.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "hasSavedData FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e){
            Log.e(TAG, "hasSavedData IOException");
            e.printStackTrace();
        }
        return false;
    }

    public static void logSavedFiles(Context context){
        String[] fileNames = context.getFilesDir().list();
        for (int i = 0; i < fileNames.length; i++) {
            String filename = fileNames[i];
            Log.d(TAG, "File #" + (i+1) + " : " + filename);
        }
    }

    public static String[] getSavedFiles(Context context){
        return context.getFilesDir().list();
    }


}
