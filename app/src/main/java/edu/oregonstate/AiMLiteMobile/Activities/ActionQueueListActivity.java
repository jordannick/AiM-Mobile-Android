package edu.oregonstate.AiMLiteMobile.Activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.RecyActionAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.RecyWorkOrderAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionDialogFragment;
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
public class ActionQueueListActivity extends AppCompatActivity{
    private static final String TAG = "AiM_ActionQueueActivity";
    private static CurrentUser currentUser;
    private static NotificationManager notificationManager;
    private Menu menu;
    private ArrayList<Action> actions;
    //private ActionAdapter actionQueueAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyActionAdapter recyActionAdapter;

    public static final int TIMELOG_FRAGMENT = 1;

    private Context self;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
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

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.right_drawer)
    RecyclerView recyclerViewDrawerNotification;

    @Bind(R.id.button_submitAll)
    Button submitAllActions;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_action);
        ButterKnife.bind(this); //BIND OUR LAYOUTS! WOO BUTTER
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        self = this;

        currentUser = CurrentUser.get(getApplicationContext());
        notificationManager = NotificationManager.get(this, recyclerViewDrawerNotification);

       // actions = currentUser.getActions();
        //actionQueueAdapter = new ActionAdapter(this, actions);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyActionAdapter = new RecyActionAdapter(currentUser.getActions(), this);
        recyclerView.setAdapter(recyActionAdapter);

        submitAllActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d(TAG, "Actions #: "+currentUser.getActions().size());
                syncActions();
            }
        });

        recyActionAdapter.refreshActions(currentUser.getActions());



        populateViews();
    }

    private void shiftItem(int selectedItemPosition){
        //int selectedItemPosition = recyclerView.getChildAdapterPosition(v);
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
        //viewHolder.itemView.setTranslationX(192);
        Animation cardShiftAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shift_card_right);
        cardShiftAnim.setFillEnabled(true);
        cardShiftAnim.setFillAfter(true);
        AnimationUtils.loadAnimation(this, R.anim.shift_card_right);
        viewHolder.itemView.startAnimation(cardShiftAnim);

      //  viewHolder.itemView.add
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
        /*relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LAYOUT ON CLICK");
                toggleRecentlyViewed(true);
            }
        });*/

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
        //actionFragment.show(getFragmentManager(), "Diag");
        actionFragment.show(getSupportFragmentManager(), "Diag");
    }

    //Start the HTTP POSTs to submit actions from queue.
    //Remove from queue upon successful POST
    private void syncActions() {
        //TODO: implement retrofit addAction
        for (Action action : currentUser.getActions()){
            int index = currentUser.getActions().indexOf(action);
            shiftItem(index);
            if (!action.isSubmitted()){
                Log.d(TAG, "Submitting Action "+currentUser.getActions().indexOf(action)+"...");
                submitAction(action);
            } else {
                Log.d(TAG, "Action "+currentUser.getActions().indexOf(action)+": already submitted");
            }
        }
    }

    private void submitAction(final Action action){
        String timeStamp = new Date(System.currentTimeMillis()).toString();
        Log.d(TAG, "submitAction timestamp = " + timeStamp);

        if (action.getHours() > 0) {
            ApiManager.getService().addTime(currentUser.getUsername(), String.valueOf(action.getHours()), action.getWorkOrder().getProposalPhase(), action.getTimeType(), timeStamp, currentUser.getToken(), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d(TAG, "Action addTime Success");
                    action.setSubmitted(true);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Action addTime Fail: " + error.getMessage());
                }
            });
        }

        ApiManager.getService().addActionTaken(currentUser.getUsername(), action.getActionTaken(), action.getWorkOrder().getProposalPhase(), timeStamp, currentUser.getToken(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "Action addActionTaken Success");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Action addActionTaken Fail: "+error.getMessage() );
            }
        });

        if (action.getNotes().size() > 0) {
            ApiManager.getService().addNote(currentUser.getUsername(), action.getNotes().get(0).getNote(), action.getWorkOrder().getProposalPhase(), timeStamp, currentUser.getToken(), new Callback<String>() {
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

        if (!action.getUpdatedStatus().equals(action.getWorkOrder().getStatus())) {
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
