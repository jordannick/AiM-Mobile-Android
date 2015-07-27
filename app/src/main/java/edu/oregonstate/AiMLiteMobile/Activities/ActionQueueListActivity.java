package edu.oregonstate.AiMLiteMobile.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.w3c.dom.Text;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.RecentAdapter;
import edu.oregonstate.AiMLiteMobile.Helpers.DialogUtils;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.NotificationManager;
import edu.oregonstate.AiMLiteMobile.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionQueueListActivity extends AppCompatActivity implements ActionAdapter.Callbacks, RecentAdapter.Callbacks {
    private static final String TAG = "AiM_ActionQueueActivity";
    private static CurrentUser currentUser;
    private static NotificationManager notificationManager;
    private LinearLayoutManager linearLayoutManager;
    private ActionAdapter actionAdapter;
    private AppCompatActivity self;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    /*@Bind(R.id.actionList_recentBarIconR)
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
    RelativeLayout recentlyViewedBarLayout;*/
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.right_drawer)
    RecyclerView recyclerViewDrawerNotification;
    @Bind(R.id.button_submitAll)
    RelativeLayout submitAllActions;
    @Bind(R.id.button_submitAll_icon)
    TextView submitAllIcon;
    @Bind(R.id.button_recent_icon)
    TextView recentlyViewedIcon;
    @Bind(R.id.button_recent)
    RelativeLayout recentlyViewedButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;

        setContentView(R.layout.activity_action);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = CurrentUser.get(getApplicationContext());
        notificationManager = NotificationManager.get(this, recyclerViewDrawerNotification);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        actionAdapter = new ActionAdapter(currentUser.getActions(), this);
        recyclerView.setAdapter(actionAdapter);

        actionAdapter.refreshActions(currentUser.getActions());

        populateViews();
        setClickListeners();

    }

    private void submittingItem(int selectedItemPosition) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
        ((ImageView) viewHolder.itemView.findViewById(R.id.submit_image)).setVisibility(View.GONE);
        ((ProgressBar) viewHolder.itemView.findViewById(R.id.submit_progressBar)).setVisibility(View.VISIBLE);
        ((TextView) viewHolder.itemView.findViewById(R.id.submit_text)).setText("Submitting...");
    }

    private void completedItem(int selectedItemPosition) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
        ((ProgressBar) viewHolder.itemView.findViewById(R.id.submit_progressBar)).setVisibility(View.GONE);
        ((ImageView) viewHolder.itemView.findViewById(R.id.submit_image)).setVisibility(View.VISIBLE);
        ((ImageView) viewHolder.itemView.findViewById(R.id.submit_image)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.submit_checked_grey));
        ((TextView) viewHolder.itemView.findViewById(R.id.submit_text)).setText("Submitted!");
        ((TextView) viewHolder.itemView.findViewById(R.id.submit_text)).setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
    }

    private void failedItem(int selectedItemPosition) {
        //TODO - on failure, need to restore submit icon and text
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
    }

    private void populateViews() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");

        recentlyViewedIcon.setText(getString(R.string.icon_recent));
        recentlyViewedIcon.setTypeface(tf);

        submitAllIcon.setText(getString(R.string.icon_submit));
        submitAllIcon.setTypeface(tf);

/*
        recentBarR.setTypeface(tf);
        recentBarL.setTypeface(tf);
        recentBarR2.setTypeface(tf);
        recentBarL2.setTypeface(tf);
        recentBarL.setText(R.string.icon_recentBarExpand);
        recentBarR.setText(R.string.icon_recentBarExpand);
        recentBarR2.setText(R.string.icon_recentBarCollapse);
        recentBarL2.setText(R.string.icon_recentBarCollapse);

        for (int i = 0; i < currentUser.getRecentlyViewedWorkOrders().size(); i++) {
            WorkOrder workOrder = currentUser.getRecentlyViewedWorkOrders().get(i);
            recentlyViewedLayout.addView(createRecentRowView(workOrder));
        }*/

    }

    private void setClickListeners() {

        submitAllActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAllActions.setClickable(false);
                syncActions();
            }
        });

        final RecentAdapter recentAdapter = new RecentAdapter(currentUser.getRecentlyViewedWorkOrders(), this);

        recentlyViewedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.createRecentlyViewedDialog(self, recentAdapter);
            }
        });

      /*  recentlyViewedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { toggleRecentlyViewed(false);
            }
        });

        recentlyViewedBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getRecentlyViewedWorkOrders().size() > 0) {
                    toggleRecentlyViewed(false);
                    recentlyViewedLayout.setVisibility(View.VISIBLE);
                } else {
                    SnackbarManager.show(Snackbar.with(self).text("No Recently Viewed").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
                }
            }
        });*/

    }


   /* private View createRecentRowView(final WorkOrder workOrder) {

        LayoutInflater inflater = getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_recent, null);

        TextView workOrderNum = (TextView) rowView.findViewById(R.id.timeLog_recentWorkOrderNum);
        TextView description = (TextView) rowView.findViewById(R.id.timeLog_recentDescription);

        workOrderNum.setText(workOrder.getProposalPhase());
        description.setText(workOrder.getDescription());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("WorkOrder", workOrder);
                bundle.putString("Title", "New Action");
                DialogUtils.createAddActionDialog(self, bundle);
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

    }*/

    // Start the HTTP POST calls to submit actions from queue
    private void syncActions() {
        int submitCount = 0;
        for (Action action : currentUser.getActions()) {
            if (!action.isSubmitted()) {
                Log.d(TAG, "Submitting Action " + currentUser.getActions().indexOf(action) + "...");
                submitAction(action);
            } else {
                Log.d(TAG, "Action " + currentUser.getActions().indexOf(action) + ": already submitted");
                submitCount++;
            }
        }
        submitAllActions.setClickable(true);
        if (submitCount >= currentUser.getActions().size())
            SnackbarManager.show(Snackbar.with(self).text("No more actions to submit").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
    }

    private void submitAction(final Action action) {
        String timeStamp = new Date(System.currentTimeMillis()).toString();
        Log.d(TAG, "submitAction timestamp = " + timeStamp);

        submittingItem(currentUser.getActions().indexOf(action));
        action.setSubmitted(true);

        ApiManager.getService().addTime(currentUser.getUsername(), String.valueOf(action.getHours()), action.getWorkOrder().getProposalPhase(), action.getTimeType(), timeStamp, currentUser.getToken(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "Action addTime Success");
                completedItem(currentUser.getActions().indexOf(action));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Action addTime Fail: " + error.getMessage());
                failedItem(currentUser.getActions().indexOf(action));
                action.setSubmitted(false);
            }
        });

        ApiManager.getService().addActionTaken(currentUser.getUsername(), action.getActionTaken(), action.getWorkOrder().getProposalPhase(), timeStamp, currentUser.getToken(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "Action addActionTaken Success");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Action addActionTaken Fail: " + error.getMessage());
            }
        });

        if (!action.getNote().equals("")) {
            ApiManager.getService().addNote(currentUser.getUsername(), action.getNote(), action.getWorkOrder().getProposalPhase(), timeStamp, currentUser.getToken(), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d(TAG, "Action addNote Success");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Action addNote Fail: " + error.getMessage());
                }
            });
        }

        if (!action.getUpdatedStatus().equals(action.getWorkOrder().getStatus())) { // Action status has changed from work order status
            ApiManager.getService().updateStatus(currentUser.getUsername(), action.getWorkOrder().getProposalPhase(), action.getUpdatedStatus(), timeStamp, currentUser.getToken(), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d(TAG, "Action updateStatus Success");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Action updateStatus Fail: " + error.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);
        View menu_notification = menu.findItem(R.id.menu_notification).getActionView();
        menu_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.openDrawer(drawerLayout);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActionSelected(Action action) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("WorkOrder", action.getWorkOrder());
        bundle.putString("Title", "Edit Action");
        if (action.isSubmitted()) {
            bundle.putBoolean("ViewMode", true);
        } else {
            bundle.putBoolean("EditMode", true);
        }
        bundle.putSerializable("ActionToEdit", action);
        DialogUtils.createAddActionDialog(this, bundle);
    }

    @Override
    public void onRecentSelected(WorkOrder workOrder){
        Bundle bundle = new Bundle();
        bundle.putSerializable("WorkOrder", workOrder);
        bundle.putString("Title", "New Action");
        DialogUtils.createAddActionDialog(self, bundle);
    }

    public void refreshActions() {
        actionAdapter.notifyDataSetChanged();
    }
}
