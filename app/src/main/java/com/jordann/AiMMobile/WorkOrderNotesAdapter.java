package com.jordann.AiMMobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sellersk on 8/20/2014.
 */
public class WorkOrderNotesAdapter extends ArrayAdapter<WorkOrderNote> {

    private final Context mContext;
    private ArrayList<WorkOrderNote> mNotes;

    public WorkOrderNotesAdapter(Context c, ArrayList<WorkOrderNote> notes){
        super(c, 0, notes);
        mNotes = notes;
        mContext = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_note, parent, false);
        }

        WorkOrderNote note = mNotes.get(position);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = format.format(note.getDate());

        //Name
                ((TextView) convertView.findViewById(R.id.note_nameTextView)).setText(note.getAuthor());
        //Date
        ((TextView)convertView.findViewById(R.id.note_dateTextView)).setText(formattedDate);
        //Note
        ((TextView)convertView.findViewById(R.id.note_noteTextView)).setText(note.getNote());

        return convertView;
    }
}
