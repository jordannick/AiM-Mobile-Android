package edu.oregonstate.AiMLiteMobile.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.oregonstate.AiMLiteMobile.Models.Note;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 8/20/2014.
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    private final static String TAG = "AiM_NoteAdapter";

    private final Context mContext;
    private ArrayList<Note> mNotes;
    private Typeface GUDEA;
    private Typeface GUDEABOLD;
    private Typeface GUDEAITALIC;

    public NoteAdapter(Context c, ArrayList<Note> notes){
        super(c, 0, notes);
        mContext = c;
        mNotes = notes;

        GUDEA = Typeface.createFromAsset(mContext.getAssets(), "fonts/Gudea-Regular.otf");
        GUDEABOLD = Typeface.createFromAsset(mContext.getAssets(), "fonts/Gudea-Bold.otf");
        GUDEAITALIC = Typeface.createFromAsset(mContext.getAssets(), "fonts/Gudea-Italic.otf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(TAG, "NoteAdapter getView start");
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_note, parent, false);
        }

        Note note = mNotes.get(position);
       // SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.US); //Expecting this format
        SimpleDateFormat format = new SimpleDateFormat("MMM d',' y h:mma", Locale.US); //Want to display this format


        String formattedDate = format.format(note.getDate());


        formattedDate = formattedDate.replace("AM", "am").replace("PM","pm");

        //Populate the layout items with the note data
        ((TextView) convertView.findViewById(R.id.note_name_textView)).setText(note.getAuthor());
        ((TextView)convertView.findViewById(R.id.note_date_textView)).setText("Added " + formattedDate);
        ((TextView)convertView.findViewById(R.id.note_note_textView)).setText(note.getNote());

        ((TextView)convertView.findViewById(R.id.note_name_textView)).setTypeface(GUDEABOLD);
        ((TextView)convertView.findViewById(R.id.note_date_textView)).setTypeface(GUDEAITALIC);
        ((TextView)convertView.findViewById(R.id.note_note_textView)).setTypeface(GUDEA);
       // Log.d(TAG, "NoteAdapter getView end");
        return convertView;
    }
}
