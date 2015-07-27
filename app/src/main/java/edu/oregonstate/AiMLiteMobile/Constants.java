package edu.oregonstate.AiMLiteMobile;

/**
 * Created by jordan_n on 7/27/2015.
 */
public abstract class Constants {

    /*
    Section titles used for headers and internal comparisons.
    MUST match the fields from API calls.
    */
    public static final String SECTION_DAILY = "Daily Assignments";
    public static final String SECTION_BACKLOG = "Backlog";
    public static final String SECTION_ADMIN = "Admin";
    public static final String SECTION_COMPLETED = "Recently Completed";


    public static final int RECENTLY_VIEWED_MAX = 5;
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 5;
}
