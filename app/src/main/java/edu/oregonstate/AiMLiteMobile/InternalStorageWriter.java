package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sellersk on 7/7/2015.
 */
public class InternalStorageWriter {
    private static final String TAG = "InternalStorageWriter";


    private Context context;
    private String filename;

    private File file;


    public InternalStorageWriter(Context context, String filename) {
        this.context = context;
        this.filename = filename;
        file = new File(context.getFilesDir(), filename);
    }

    public void writeToFile(String toWrite){
        try {
            byte[] bytes = toWrite.getBytes();
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error opening file for write: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Error writing to file: " + filename);
            e.printStackTrace();
        }

    }

    public void printFileContents(){
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            StringBuilder builder = new StringBuilder();
            int content;
            while((content = inputStream.read()) != -1){
                builder.append((char)content);
            }
            String result = builder.toString();
            Log.d(TAG, "SUCCESS Resulting read: " + result);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error opening file for read: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Error reading file: " + filename);
            e.printStackTrace();
        }
    }


}
