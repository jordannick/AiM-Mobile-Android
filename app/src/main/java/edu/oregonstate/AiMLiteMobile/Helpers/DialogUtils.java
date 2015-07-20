package edu.oregonstate.AiMLiteMobile.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import edu.oregonstate.AiMLiteMobile.Adapters.NoteAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionDialogFragment;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 7/14/2015.
 */
public class DialogUtils {
    public static final String TAG = "DialogUtils";

    private static LayoutInflater layoutInflater;

    private static LayoutInflater getLayoutInflater(Context c){
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return layoutInflater;
    }

    public static void createNotesViewDialog(Context c, NoteAdapter notesAdapter){
        Log.d(TAG, "context = "+c);
        final AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        View convertView = getLayoutInflater(c).inflate(R.layout.dialog_notes_list, null);

        convertView.findViewById(R.id.dialogNotes_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupNotes_listView);

        TextView emptyText = (TextView) convertView.findViewById(android.R.id.empty);
        lv.setEmptyView(emptyText);

        lv.setSelector(android.R.color.transparent);
        lv.setAdapter(notesAdapter);
        alertDialog.show();
    }

    public static void createAddActionDialog(AppCompatActivity activity, Bundle bundle){
        AddActionDialogFragment actionFragment = new AddActionDialogFragment();
        actionFragment.setArguments(bundle);
        actionFragment.show(activity.getSupportFragmentManager(), AddActionDialogFragment.TAG);
    }

    public static void createConfirmDialog(Context c, String message, DialogInterface.OnClickListener confirmListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Confirm");
        builder.setMessage(message);
        builder.setPositiveButton("OK", confirmListener);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
