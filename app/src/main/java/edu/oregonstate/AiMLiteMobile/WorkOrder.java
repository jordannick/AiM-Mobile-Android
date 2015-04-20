package edu.oregonstate.AiMLiteMobile;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class WorkOrder implements Serializable {

    private static final String TAG = "WorkOrder";

    public static final String WORK_ORDER_EXTRA = "edu.oregonstate.AiMLiteMobile.WorkOrder";

    String mDescription;
    String mBeginDate;
    String mEndDate;
    String mPriority;
    String mDateCreated;
    String mStatus;
    String mSection;
    int mPriorityColor;


    ArrayList<Note> mNotes;

    String mContactName;
    String mDepartment;

    //Row Display
    String mCategory;
    String mPriorityLetter;
    String mCraftCode;
    String mShop;

    String mMinCraftCode;
    String mBuilding;
    String mProposalPhase;
    String[] mDateElements = new String[4]; //[0] DayOfWeek  [1] MonthDay  [2] Year  [3] DaysAgo

    //Utility Functions
    private String capitalizeStr(String input) {
        StringBuilder sb = new StringBuilder();
        input = input.substring(input.indexOf("_") + 1, input.length());
        input = input.toLowerCase();
        String[] words = input.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = String.format("%s%s", words[i].substring(0, 1).toUpperCase(), words[i].substring(1, words[i].length()));
            sb.append(words[i]);
            if (i != words.length - 1)
                sb.append(" ");
        }
        return sb.toString();
    }

    public ArrayList<Note> getNotes() {
        if (mNotes == null) {
            mNotes = new ArrayList<Note>();
            int day = 86400000;

            mNotes.add(new Note("Single line example text", "WILLIAMSONT", new Date(System.currentTimeMillis() - (int) (day * .3))));


            mNotes.add(new Note("Multi line example text.\nMulti line example text.", "PITTSL", new Date(System.currentTimeMillis() - (int) (day * 3.4))));


            mNotes.add(new Note("Very long example text of something wrong with campus that must be addressed promptly otherwise all students fail.", "MCGILLD", new Date(System.currentTimeMillis() - (int) (day * 10.8))));

        }
        return mNotes;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getShop() {
        return mShop;
    }

    public void setShop(String shop) {
        mShop = shop;
    }

    public void setNotes(ArrayList<Note> notes) {
        mNotes = notes;
    }

    public String getContactName() {
        return mContactName;
    }

    public void setContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(String mDepartment) {
        this.mDepartment = mDepartment;
    }

    public String getMinCraftCode() {
        return mMinCraftCode;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getBeginDate() {
        return mBeginDate;
    }

    public void setBeginDate(String beginDate) {
        mBeginDate = beginDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public String getPriority() {
        return mPriority;
    }

    public String getSection() {
        return mSection;
    }

    public void setSection(String mSection) {
        this.mSection = mSection;
    }

    public void setPriority(String priority) {
        mPriority = priority;
        mPriorityLetter = priority.substring(0, 1);


        //mPriorityColor = new Color(1, 1,1);

        switch (priority.charAt(0)) {
            case 'U':
                mPriorityColor = R.color.urgent_orange;
                break;
            case 'E':
                mPriorityColor = R.color.emergency_red;
                break;
            case 'T':
                mPriorityColor = R.color.timeSensitive_yellow;
                break;
            case 'R':
                mPriorityColor = R.color.routine_green;
                break;
            case 'S':
                mPriorityColor = R.color.scheduled_blue;
                break;
            default:
                Log.d("TAG1", "Too bad...");
                break;
        }
    }

    public int getPriorityColor() {
        return mPriorityColor;
        //return R.color.red;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = capitalizeStr(category);
    }

    public String getPriorityLetter() {
        return mPriorityLetter;
    }

    public void setPriorityLetter(String priorityLetter) {
        mPriorityLetter = priorityLetter;
    }

    public String getCraftCode() {
        return mCraftCode;
    }

    public void setCraftCode(String craftCode) {
        mMinCraftCode = capitalizeStr(craftCode);
        mCraftCode = craftCode;
    }

    public String getBuilding() {
        return mBuilding;
    }

    public void setBuilding(String building) {
        mBuilding = capitalizeStr(building);
    }

    public String getProposalPhase() {
        return mProposalPhase;
    }

    public void setProposalPhase(String proposalPhase) {
        mProposalPhase = proposalPhase;
    }

    public String getDateCreated() {
        return mDateCreated;
    }

    public String[] getDateElements() {
        return mDateElements;
    }

    public void setDateElements(String dateElements) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dateElements);
            mDateCreated = date.toString();

            format = new SimpleDateFormat("EE");
            mDateElements[0] = format.format(date);
            format = new SimpleDateFormat("MM/dd");
            mDateElements[1] = format.format(date);
            format = new SimpleDateFormat("yyyy");
            mDateElements[2] = format.format(date);

            Date currentDate = new Date();

            float secondsInHour = 60 * 60;
            float secondsInDay = 60 * 60 * 24;
            long millisInSecond = 1000;

            long interval = (currentDate.getTime() - date.getTime()) / millisInSecond;
            if (interval < secondsInHour) {
                if (interval < 60)
                    mDateElements[3] = "1 min ago";
                else
                    mDateElements[3] = String.format("%.0f mins ago", (interval / 60.0));
            } else if (interval < secondsInDay) {
                if (interval < secondsInHour * 2)
                    mDateElements[3] = "1 hour ago";
                else
                    mDateElements[3] = String.format("%.0f hours ago", interval / secondsInHour);
            } else {
                if (interval < secondsInDay * 2)
                    mDateElements[3] = "1 day ago";
                else
                    mDateElements[3] = String.format("%.0f days ago", interval / secondsInDay);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error: Parsing Date");
            e.printStackTrace();
        }
    }


}
