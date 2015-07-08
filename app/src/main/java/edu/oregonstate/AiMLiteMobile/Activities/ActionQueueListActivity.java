package edu.oregonstate.AiMLiteMobile.Activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionDialogFragment;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.NotificationManager;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionQueueListActivity extends AppCompatActivity{
    private static final String TAG = "AiM_ActionQueueActivity";
    private static CurrentUser currentUser;
    private static NotificationManager notificationManager;
    private Menu menu;
    private ArrayList<Action> actions;
    private ActionAdapter actionQueueAdapter;

    public static final int TIMELOG_FRAGMENT = 1;

    private Context self;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.actionList_recentBarIconR)
    TextView recentBarR;
    @Bind(R.id.actionList_recentBarIconL)
    TextView recentBarL;
    @Bind(R.id.actionList_recentBarIconR2)
    TextView recentBarR2;
    @Bind(R.id.actionList_recentBarIconL2)
    TextView recentBarL2;
    @Bind(R.id.actionList_recentlyViewedLayout)
    LinearLayout recentlyViewedLayout;
    @Bind(R.id.actionList_recentlyViewedBarLayout)
    RelativeLayout recentlyViewedBarLayout;
    @Bind(R.id.actionList_relativeLayout)
    RelativeLayout relativeLayout;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.right_drawer)
    RecyclerView recyclerViewDrawerNotification;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_action);
        ButterKnife.bind(this); //BIND OUR LAYOUTS! WOO BUTTER
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        self = this;

        currentUser = CurrentUser.get(getApplicationContext());
        notificationManager = NotificationManager.get(this, recyclerViewDrawerNotification);

        actions = currentUser.getActions();

        actionQueueAdapter = new ActionAdapter(this, actions);

        populateViews();
    }

    private void populateViews() {
        final ArrayList<WorkOrder> recentWorkOrders = currentUser.getRecentlyViewedWorkOrders();

        //TextView recentBarR = (TextView)findViewById(R.id.actionList_recentBarIconR);
        //TextView recentBarL = (TextView)findViewById(R.id.actionList_recentBarIconL);
        //TextView recentBarR2 = (TextView)findViewById(R.id.actionList_recentBarIconR2);
        //TextView recentBarL2 = (TextView)findViewById(R.id.actionList_recentBarIconL2);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");
        recentBarR.setTypeface(tf);
        recentBarL.setTypeface(tf);
        recentBarR2.setTypeface(tf);
        recentBarL2.setTypeface(tf);
        recentBarL.setText(R.string.icon_recentBarExpand);
        recentBarR.setText(R.string.icon_recentBarExpand);
        recentBarR2.setText(R.string.icon_recentBarCollapse);
        recentBarL2.setText(R.string.icon_recentBarCollapse);

        //final LinearLayout recentlyViewedLayout = (LinearLayout)findViewById(R.id.actionList_recentlyViewedLayout);
        recentlyViewedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecentlyViewed(false);
            }
        });

        int i;
        for (i = 0; i < recentWorkOrders.size(); i++) {
            WorkOrder workOrder = recentWorkOrders.get(i);
            recentlyViewedLayout.addView(createRecentRowView(workOrder));
        }
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LAYOUT ON CLICK");
                toggleRecentlyViewed(true);
            }
        });

        recentlyViewedBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recentWorkOrders.size() > 0) {
                    toggleRecentlyViewed(false);
                    recentlyViewedLayout.setVisibility(View.VISIBLE);
                } else {
                    //Show snackbar alert of "no recents"
                    SnackbarManager.show(Snackbar.with(self).text("No Recently Viewed").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));

                }
            }
        });
    }


    private View createRecentRowView(final WorkOrder workOrder) {
        LayoutInflater inflater = getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_recent, null);

        TextView workOrderNum = (TextView) rowView.findViewById(R.id.timeLog_recentWorkOrderNum);
        TextView description = (TextView) rowView.findViewById(R.id.timeLog_recentDescription);

        workOrderNum.setText(workOrder.getProposalPhase());
        description.setText(workOrder.getDescription());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimeEntryDialog(workOrder);
            }
        });

        return rowView;
    }

    private void toggleRecentlyViewed(boolean onlyHide) {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        if (recentlyViewedLayout.getVisibility() == View.VISIBLE) {
            recentlyViewedLayout.setVisibility(View.GONE);
            recentlyViewedLayout.startAnimation(slideDown);
            recentlyViewedBarLayout.startAnimation(slideUp);
        } else if (!onlyHide) {
            recentlyViewedLayout.setVisibility(View.VISIBLE);
            recentlyViewedLayout.startAnimation(slideUp);
            recentlyViewedBarLayout.startAnimation(slideDown);
        }

    }

    private void createTimeEntryDialog(WorkOrder workOrder) {
        AddActionDialogFragment actionFragment = new AddActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("WorkOrder", workOrder);
        bundle.putString("Title", "New From Recent");
        actionFragment.setArguments(bundle);
        //actionFragment.setTargetFragment(this, TIMELOG_FRAGMENT);
        actionFragment.show(getFragmentManager(), "Diag");

    }

    //Start the HTTP POSTs to submit actions from queue.
    //Remove from queue upon successful POST
    public void syncActions() {
        //TODO: implement retrofit addAction
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);

        this.menu = menu;
        View menu_notification = menu.findItem(R.id.menu_notification).getActionView();
        menu_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createNoticesViewPopup();
                //drawerLayout.openDrawer(GravityCompat.END);
                notificationManager.openDrawer(drawerLayout);
            }
        });

        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_notification:
                createNoticesViewPopup();
                break;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
           case R.id.action_sync:
                syncActions();
                break;
            case R.id.log_out:
                currentUser.logoutUser(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "MenuItem clicked! " + item);
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "aa On Stop ActionQueue");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "aa On Desroy ActionQueue");

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "aa On Save ActionQueue");
        super.onSaveInstanceState(outState);
    }
}
