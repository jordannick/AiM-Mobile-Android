package edu.oregonstate.AiMLiteMobile.Helpers;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 7/1/2015.
 */
public class NotificationManager {
    private static CurrentUser currentUser;
    private static NotificationManager notificationManager;
    private NoticeAdapter noticesAdapter;
    private Context context;

    private NotificationManager(Context c) {
        context = c;
        currentUser = CurrentUser.get(c);
        noticesAdapter = new NoticeAdapter(currentUser.getNotices(), context);
    }

    public static NotificationManager get(Context c, RecyclerView recyclerViewDrawerNotification) {
        if (notificationManager == null) {
            notificationManager = new NotificationManager(c);
        }
        notificationManager.initRecyclerView(c, recyclerViewDrawerNotification);
        return notificationManager;
    }

    //Set RecyclerView Adapter and LayoutManager for each activity that calls 'get'
    public void initRecyclerView(Context c, RecyclerView recyclerViewDrawerNotification) {
        LinearLayoutManager linearLayoutManagerDrawer = new LinearLayoutManager(c);
        recyclerViewDrawerNotification.setLayoutManager(linearLayoutManagerDrawer);
        recyclerViewDrawerNotification.setAdapter(noticesAdapter);
    }

    public void refreshNotices(ArrayList<Notice> notices) {
        currentUser.setNotices(notices);
        noticesAdapter.refreshNotices(notices);
    }

    public void openDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    // Requires menu to contain a notices icon called menu_notification
    public void updateNoticeCount(Menu menu) {
        View menu_notification = menu.findItem(R.id.menu_notification).getActionView();
        TextView noticeBox = (TextView) menu_notification.findViewById(R.id.notification_box);
        noticeBox.setText(String.valueOf(currentUser.getNotices().size()));
    }


}
