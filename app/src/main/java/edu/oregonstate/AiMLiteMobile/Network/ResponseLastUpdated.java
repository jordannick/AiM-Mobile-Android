package edu.oregonstate.AiMLiteMobile.Network;

import java.util.ArrayList;
import java.util.Date;

import edu.oregonstate.AiMLiteMobile.Models.Notice;

/**
 * Created by sellersk on 6/10/2015.
 */
public class ResponseLastUpdated {
    private static final String TAG = "AiM_ResponseLastUpdated";

    private String dateString;
    private Date date;



    public ResponseLastUpdated(String value, Date date) {
        this.dateString = value;
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public Date getDate() {
        return date;
    }
}
