package edu.oregonstate.AiMLiteMobile;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sellersk on 8/20/2014.
 */
public class Note implements Serializable {

    private static final String TAG = "Note";

    String mNote;
    String mAuthor;
    Date mDate;
    Boolean isNew;

    public Note(String note, String author, Date date) {
        mNote = note;
        mAuthor = author;
        mDate = date;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setNew(){
        isNew = true;
    }

    public boolean isNew(){
        return isNew;
    }
}
