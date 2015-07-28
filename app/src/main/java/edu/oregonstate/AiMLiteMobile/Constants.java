package edu.oregonstate.AiMLiteMobile;

/**
 * Created by jordan_n on 7/27/2015.
 */
public abstract class Constants {

    /*
    Section titles used for headers and internal comparisons.
    Should match the fields from API calls.
    */
    public static final String SECTION_DAILY = "Daily Assignments";
    public static final String SECTION_BACKLOG = "Backlog";
    public static final String SECTION_ADMIN = "Admin";
    public static final String SECTION_COMPLETED = "Recently Completed";

    /*
    Status names used for display in dropdowns and details
     */
    public static final String STATUS_ASSIGNED = "Assigned";
    public static final String STATUS_IN_PROGRESS = "Work in Progress";
    public static final String STATUS_COMPLETE = "Work Complete";
    public static final String STATUS_ON_HOLD = "On Hold";

    /*
    The maximum recently viewed work orders that are saved and displayed
     */
    public static final int RECENTLY_VIEWED_MAX = 5;

    /*
    Amount of time after which a data refresh is needed
     */
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 5;

    /*
    Bounds for time entry, and increment amount for plus/minus buttons
     */
    public static final double MIN_HOURS = 0;
    public static final double MAX_HOURS = 24;
    public static final double INCR_HOURS = 0.25;
}
