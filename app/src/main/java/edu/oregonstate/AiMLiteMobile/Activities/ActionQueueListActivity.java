package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Adapters.ActionAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionDialogFragment;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 2/19/2015.
 */
public class ActionQueueListActivity extends AppCompatActivity{
    private static final String TAG = "ActionQueueActivity";
    private static CurrentUser currentUser;
    private ArrayList<Action> actions;
    private ActionAdapter actionQueueAdapter;
    private RelativeLayout recentlyViewedBarLayout;

    public static final int TIMELOG_FRAGMENT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        setTitle(R.string.action_queue_activity_title);
        currentUser = CurrentUser.get(getApplicationContext());
        actions = currentUser.getActions();
        actionQueueAdapter = new ActionAdapter(this, actions);

        populateViews();
    }

    private void populateViews(){
        recentlyViewedBarLayout = (RelativeLayout)findViewById(R.id.actionList_recentlyViewedBarLayout);

        final ArrayList<WorkOrder> recentWorkOrders  = currentUser.getRecentlyViewedWorkOrders();

        //TextView recentBarL = (TextView)findViewById(R.id.actionList_recentBarIconL);
        TextView recentBarR = (TextView)findViewById(R.id.actionList_recentBarIconR);
        TextView recentBarL = (TextView)findViewById(R.id.actionList_recentBarIconL);
        TextView recentBarR2 = (TextView)findViewById(R.id.actionList_recentBarIconR2);
        TextView recentBarL2 = (TextView)findViewById(R.id.actionList_recentBarIconL2);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");
        recentBarR.setTypeface(tf); recentBarL.setTypeface(tf);
        recentBarR2.setTypeface(tf); recentBarL2.setTypeface(tf);
        recentBarL.setText(R.string.icon_recentBarExpand);
        recentBarR.setText(R.string.icon_recentBarExpand);
        recentBarR2.setText(R.string.icon_recentBarCollapse);
        recentBarL2.setText(R.string.icon_recentBarCollapse);

        final LinearLayout recentlyViewedLayout = (LinearLayout)findViewById(R.id.actionList_recentlyViewedLayout);
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

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.actionList_relativeLayout);
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
                if(recentWorkOrders.size() > 0) {
                    toggleRecentlyViewed(false);
                    recentlyViewedLayout.setVisibility(View.VISIBLE);
                }else{
                    //Show snackbar alert of "no recents"
                    SnackbarManager.show(Snackbar.with(getApplicationContext()).text("No Recently Viewed").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));

                }
            }
        });
    }


    private View createRecentRowView(final WorkOrder workOrder){
        LayoutInflater inflater = getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_recent, null);
        TextView workOrderNum = (TextView)rowView.findViewById(R.id.timeLog_recentWorkOrderNum);
        TextView description = (TextView)rowView.findViewById(R.id.timeLog_recentDescription);

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

    private void toggleRecentlyViewed(boolean onlyHide){
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        LinearLayout layout = (LinearLayout) findViewById(R.id.actionList_recentlyViewedLayout);
        if(layout.getVisibility() == View.VISIBLE){
            layout.setVisibility(View.GONE);
            layout.startAnimation(slideDown);
            recentlyViewedBarLayout.startAnimation(slideUp);
        }else if(!onlyHide){
            layout.setVisibility(View.VISIBLE);
            layout.startAnimation(slideUp);
            recentlyViewedBarLayout.startAnimation(slideDown);
        }

    }

    private void createTimeEntryDialog(WorkOrder workOrder){
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
    public void syncActions(){
        //TODO: implement retrofit addAction
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_queue, menu);
        return true;
    }

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void createNoticesViewPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog_notes_list, null);

        convertView.findViewById(R.id.dialogNotes_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        NoticeAdapter noticesAdapter = new NoticeAdapter(this, currentUser.getNotices());
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupNotes_listView);
        lv.setSelector(android.R.color.transparent);
        lv.setAdapter(noticesAdapter);
        alertDialog.show();
    }
}
