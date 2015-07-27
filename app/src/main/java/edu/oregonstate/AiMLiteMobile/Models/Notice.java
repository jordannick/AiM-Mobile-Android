package edu.oregonstate.AiMLiteMobile.Models;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jordan_n on 6/15/2015.
 */
public class Notice implements Serializable {

    private static final String TAG = "AiM_Notice";

    String id;
    String type;
    String editClerk;
    String description;
    Date modified;
    String[] mDateElements = new String[5]; //[0] DayOfWeek  [1] MonthDay  [2] Year  [3] ValueDaysAgo [4] StringDaysAgo

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEditClerk() {
        return editClerk;
    }

    public void setEditClerk(String editClerk) {
        this.editClerk = editClerk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setDateElements(String dateElements) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        try {
            Log.d(TAG, "dateElements: " + dateElements);

            Date date = format.parse(dateElements);

            format = new SimpleDateFormat("EE", Locale.US);
            mDateElements[0] = format.format(date);
            format = new SimpleDateFormat("MM/dd", Locale.US);
            mDateElements[1] = format.format(date);
            format = new SimpleDateFormat("yyyy", Locale.US);
            mDateElements[2] = format.format(date);

            Date currentDate = new Date();

            float secondsInHour = 60 * 60;
            float secondsInDay = 60 * 60 * 24;
            long millisInSecond = 1000;

            long interval = (currentDate.getTime() - date.getTime()) / millisInSecond;
            if (interval < secondsInHour) {
                if (interval < 60) {
                    mDateElements[3] = "1 min ago";
                    mDateElements[4] = "1 min ago";
                }
                else {
                    mDateElements[3] = String.format("%.0f", (interval / 60.0));
                    mDateElements[4] = "mins ago";
                }
            } else if (interval < secondsInDay) {
                if (interval < secondsInHour * 2) {
                    mDateElements[3] = "1";
                    mDateElements[4] = "hour ago";
                }
                else {
                    mDateElements[3] = String.format("%.0f", interval / secondsInHour);
                    mDateElements[4] = "hours ago";
                }
            } else {
                if (interval < secondsInDay * 2) {
                    mDateElements[3] = "1";
                    mDateElements[4] = "day ago";
                }
                else {
                    mDateElements[3] = String.format("%.0f", interval / secondsInDay);
                    mDateElements[4] = "days ago";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: Parsing Date - " + e);
            e.printStackTrace();
        }
    }


    public String[] getDateElements() {
        return mDateElements;
    }
}
