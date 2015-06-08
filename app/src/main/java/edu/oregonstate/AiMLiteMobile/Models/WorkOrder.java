package edu.oregonstate.AiMLiteMobile.Models;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class WorkOrder implements Serializable {
    private static final String TAG = "WorkOrder";
    public static final String WORK_ORDER_EXTRA = "edu.oregonstate.AiMLiteMobile.Models.WorkOrder";

    public final UUID uuid = java.util.UUID.randomUUID();

    String mDescription;
    String mBeginDate;
    String mEndDate;
    String mPriority;
    String mDateCreated;
    String mStatus;
    String mSection;
    int mSectionNum;
    int mPriorityColor;

    ArrayList<Note> mNotes;

    String mContactName = "";
    String mDepartment;

    //Row Display
    String mCategory;
    String mPriorityLetter;
    String mCraftCode;
    String mShop;

    String mMinCraftCode;
    String mBuilding;
    String mProposalPhase;
    String[] mDateElements = new String[5]; //[0] DayOfWeek  [1] MonthDay  [2] Year  [3] ValueDaysAgo [4] StringDaysAgo

    public ArrayList<Note> getNotes() {
        if (mNotes == null) {
            mNotes = new ArrayList<Note>();

            //%% DEBUG %%
            int day = 86400000;
            mNotes.add(new Note("Single line example text", "WILLIAMSONT", new Date(System.currentTimeMillis() - (int) (day * .3))));
            mNotes.add(new Note("Multi line example text.\nMulti line example text.", "PITTSL", new Date(System.currentTimeMillis() - (int) (day * 3.4))));
            mNotes.add(new Note("This is an example description. I ate oatmeal this morning. With some coffee, and a wrap. It was quite good. I'm gonna continue writing, because I need to fill more space. How is your day going? That's great.", "MCGILLD", new Date(System.currentTimeMillis() - (int) (day * 10.8))));
            //%% DEBUG %%
        }
        return mNotes;
    }

    public void setPriority(String priority) {
        mPriority = priority;
        mPriorityLetter = priority.substring(0, 1);
        switch (priority.charAt(0)) {
            case 'U':
                mPriorityColor = R.color.urgent_orange;
                break;
            case 'E':
                mPriorityColor = R.color.emergency_red;
                break;
            case 'T':
                mPriorityColor = R.color.timeSensitive_purple;
                break;
            case 'R':
                mPriorityColor = R.color.routine_green;
                break;
            case 'S':
                mPriorityColor = R.color.scheduled_blue;
                break;
            default:
                Log.d(TAG, "Too bad...");
                break;
        }
    }

    public void setDateElements(String dateElements) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = format.parse(dateElements);
            mDateCreated = date.toString();

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
            Log.d(TAG, "Error: Parsing Date");
            e.printStackTrace();
        }
    }

    //TODO: remove? Is this needed? Can't we just use toUpper. Leaving in for now, cuz scared
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
    public int getPriorityColor() {
        return mPriorityColor;
        //return R.color.red;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category.toUpperCase();
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
        mMinCraftCode = craftCode.toUpperCase();
        mCraftCode = craftCode;
    }

    public String getBuilding() {
        return mBuilding;
    }

    public void setBuilding(String building) {
        mBuilding = building.toUpperCase();
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

    public int getSectionNum() {
        return mSectionNum;
    }

    public void setSectionNum(int mSectionNum) {
        this.mSectionNum = mSectionNum;
    }

    public void setSection(String mSection) {
        this.mSection = mSection;
        int num = -1;
        switch (mSection){
            case "Daily":
                num = 0;
                break;
            case "Backlog":
                num = 1;
                break;
            case "Admin":
                num = 2;
                break;
            case "Recently Completed":
                num = 3;
                break;
        }
        setSectionNum(num);
    }

}
