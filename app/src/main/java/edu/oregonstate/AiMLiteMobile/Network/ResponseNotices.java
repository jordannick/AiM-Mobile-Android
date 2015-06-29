package edu.oregonstate.AiMLiteMobile.Network;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Notice;

/**
 * Created by sellersk on 6/10/2015.
 */
public class ResponseNotices {

    private ArrayList<Notice> notices;
    private String rawJson;

    public ResponseNotices(ArrayList<Notice> notices, String rawJson) {
        this.notices = notices;
        this.rawJson = rawJson;
    }

    public ArrayList<Notice> getNotices() {
        return notices;
    }

    public String getRawJson() {
        return rawJson;
    }
}
