package edu.oregonstate.AiMLiteMobile.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.oregonstate.AiMLiteMobile.Helpers.InternalStorageWriter;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 7/21/2015.
 */
public class ViewSavedFilesDialog extends DialogFragment {

    private String[] fileNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileNames = InternalStorageWriter.getSavedFiles(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_saved_files, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LinearLayout linearLayout = (LinearLayout)v.findViewById(R.id.container);

        for (int i = 0; i < fileNames.length; i++) {
            TextView textView = new TextView(getActivity());
            textView.setText(fileNames[i]);
            linearLayout.addView(textView);
        }

        return v;
    }

}
