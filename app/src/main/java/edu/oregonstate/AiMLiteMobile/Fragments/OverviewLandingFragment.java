package edu.oregonstate.AiMLiteMobile.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.oregonstate.AiMLiteMobile.Activities.OverviewListActivity;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by SellersK on 6/1/2015.
 */
public class OverviewLandingFragment extends Fragment {
    private static final String TAG = "OverviewLandingFragment";
    private static CurrentUser sCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.overview_landing, container, false);

        TextView iconUser = (TextView)v.findViewById(R.id.icon_user);
        TextView usernameText = (TextView)v.findViewById(R.id.textView_username);

        TextView iconDaily = (TextView)v.findViewById(R.id.overviewLanding_daily_icon);
        TextView iconBacklog = (TextView)v.findViewById(R.id.overviewLanding_backlog_icon);
        TextView iconAdmin = (TextView)v.findViewById(R.id.overviewLanding_admin_icon);
        TextView iconRecentlyCompleted = (TextView)v.findViewById(R.id.overviewLanding_recentlyCompleted_icon);

        TextView iconNotices = (TextView)v.findViewById(R.id.overviewLanding_notices_icon);
        TextView iconTimeLog = (TextView)v.findViewById(R.id.overviewLanding_timeLog_icon);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        iconUser.setTypeface(tf);
        iconDaily.setTypeface(tf); iconBacklog.setTypeface(tf); iconAdmin.setTypeface(tf); iconRecentlyCompleted.setTypeface(tf);
        iconNotices.setTypeface(tf); iconTimeLog.setTypeface(tf);

        usernameText.setText(sCurrentUser.getUsername());
        iconUser.setText(getString(R.string.icon_user));
        iconDaily.setText(getString(R.string.icon_daily));
        iconBacklog.setText(getString(R.string.icon_backlog));
        iconAdmin.setText(getString(R.string.icon_admin));
        iconRecentlyCompleted.setText(getString(R.string.icon_recentlyCompleted));
        iconNotices.setText(getString(R.string.icon_notices));
        iconTimeLog.setText(getString(R.string.icon_timeLog));


        CardView cardDaily = (CardView)v.findViewById(R.id.overviewLanding_daily_card);
        CardView cardBacklog = (CardView)v.findViewById(R.id.overviewLanding_backlog_card);
        CardView cardAdmin = (CardView)v.findViewById(R.id.overviewLanding_admin_card);
        CardView cardRecentlyCompleted = (CardView)v.findViewById(R.id.overviewLanding_recentlyCompleted_card);
        CardView cardNotices = (CardView)v.findViewById(R.id.overviewLanding_notices_card);





/*        v.findViewById(R.id.button_scrollToDaily).setOnClickListener(new View.OnClickListener() {

        v.findViewById(R.id.card_scrollToDaily).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OverviewListActivity) getActivity()).scrollToSection("Daily");
            }
        });
        v.findViewById(R.id.card_scrollToBacklog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OverviewListActivity) getActivity()).scrollToSection("Backlog");
            }
        });
        v.findViewById(R.id.card_scrollToAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OverviewListActivity) getActivity()).scrollToSection("Admin");
            }
        });
        v.findViewById(R.id.card_scrollToCompleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OverviewListActivity) getActivity()).scrollToSection("Completed");
            }
        });

        v.findViewById(R.id.card_timeLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OverviewListActivity) getActivity()).beginActionQueueAcitivity();
            }
        });
        */
        //TODO actual counts
     //   ((TextView)v.findViewById(R.id.daily_count)).setText(((OverviewListActivity) getActivity()).getDailyCount());
      //  ((TextView)v.findViewById(R.id.backlog_count)).setText(((OverviewListActivity)getActivity()).getBacklogCount());

        return v;
    }
}
