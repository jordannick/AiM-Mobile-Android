package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sellersk on 8/20/2014.
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    private final static String TAG = "NoteAdapter";

    private final Context mContext;
    private ArrayList<Note> mNotes;

    public NoteAdapter(Context c, ArrayList<Note> notes){
        super(c, 0, notes);
        mContext = c;
        mNotes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_note, parent, false);
        }

        Note note = mNotes.get(position);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String formattedDate = format.format(note.getDate());

        //Populate the layout items with the note data
        ((TextView) convertView.findViewById(R.id.note_nameTextView)).setText(note.getAuthor());

        ((TextView)convertView.findViewById(R.id.note_dateTextView)).setText(formattedDate);

        ((TextView)convertView.findViewById(R.id.note_noteTextView)).setText(note.getNote());

        return convertView;
    }
}
