package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Created by sellersk on 8/11/2015.
 */
public class ChartSpinnerAdapter implements SpinnerAdapter {
    public static final String TAG = "ChartSpinnerAdapter";

    private Activity activity;
    private String[] spinnerArray;

    public ChartSpinnerAdapter(Activity activity, String[] spinnerArray) {
        this.activity = activity;
        this.spinnerArray = spinnerArray;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(R.layout.settings_spinner, parent, false);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.settings_spinner_textView);
/*        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        textView.setText(spinnerArray[position]);

        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return spinnerArray.length;
    }

    @Override
    public Object getItem(int position) {
        return spinnerArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(R.layout.settings_spinner, parent, false);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.settings_spinner_textView);
        textView.setText(spinnerArray[position]);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
