package edu.oregonstate.AiMLiteMobile.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by SellersK on 6/1/2015.
 */
public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.overview_landing, container, false);


        TextView iconDaily = (TextView)v.findViewById(R.id.overviewLanding_daily_icon);
        TextView iconBacklog = (TextView)v.findViewById(R.id.overviewLanding_backlog_icon);
        TextView iconAdmin = (TextView)v.findViewById(R.id.overviewLanding_admin_icon);
        TextView iconRecentlyCompleted = (TextView)v.findViewById(R.id.overviewLanding_recentlyCompleted_icon);

        TextView iconNotices = (TextView)v.findViewById(R.id.overviewLanding_notices_icon);
        TextView iconTimeLog = (TextView)v.findViewById(R.id.overviewLanding_timeLog_icon);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        iconDaily.setTypeface(tf); iconBacklog.setTypeface(tf); iconAdmin.setTypeface(tf); iconRecentlyCompleted.setTypeface(tf);
        iconNotices.setTypeface(tf); iconTimeLog.setTypeface(tf);

        iconDaily.setText(getString(R.string.icon_daily));
        iconBacklog.setText(getString(R.string.icon_backlog));
        iconAdmin.setText(getString(R.string.icon_admin));
        iconRecentlyCompleted.setText(getString(R.string.icon_recentlyCompleted));
        iconNotices.setText(getString(R.string.icon_notices));
        iconTimeLog.setText(getString(R.string.icon_timeLog));

/*        v.findViewById(R.id.button_scrollToDaily).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "pressed the button");
                ((OverviewListActivity) getActivity()).scrollToPosition(8);
            }
        });*/

        return v;
    }
}
