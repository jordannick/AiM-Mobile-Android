package edu.oregonstate.AiMLiteMobile.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.NoteAdapter;
import edu.oregonstate.AiMLiteMobile.Constants;
import edu.oregonstate.AiMLiteMobile.Helpers.DialogUtils;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Helpers.NotificationManager;
import edu.oregonstate.AiMLiteMobile.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "AiM_DetailActivity";

    private static CurrentUser currentUser;
    public static WorkOrder workOrder;
    private NoteAdapter notesAdapter;
    private static NotificationManager notificationManager;
    private AppCompatActivity self;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.row_proposal_detail) TextView proposal;
    @Bind(R.id.descriptionTextView_detail) TextView description;
    @Bind(R.id.actionRow_valueAgo_detail) TextView valueAgo;
    @Bind(R.id.actionRow_stringAgo) TextView timeAgo;
    //@Bind(R.id.dateCreatedTextView) TextView dateCreated;
    @Bind(R.id.detailView_textRequestor) TextView requestor;
    @Bind(R.id.detailView_requestedDepartment) TextView requestorDepartment;
    @Bind(R.id.detailView_requestedTime) TextView requestorTime;
    @Bind(R.id.detailView_textLocation) TextView location;
    @Bind(R.id.detailView_textPriority) TextView priority;
    @Bind(R.id.detailView_textStatus) TextView status;
    @Bind(R.id.detailView_textAssigned) TextView assigned;
    @Bind(R.id.detailView_textFunding) TextView funding;
    @Bind(R.id.detailView_textCategory) TextView category;
    @Bind(R.id.detailView_textShop) TextView shop;

    @Bind(R.id.imageView_priorityIconDetail) ImageView priorityIcon;
    @Bind(R.id.imageView_statusIcon) ImageView statusIcon;

    @Bind(R.id.button_moveSection_icon) TextView moveSectionTextIcon;
    @Bind(R.id.button_moveSection) RelativeLayout moveSection;
    @Bind(R.id.button_moveSection_text) TextView moveSectionTitle;
    @Bind(R.id.button_viewNotes_icon) TextView viewNotesIcon;
    @Bind(R.id.button_addAction_icon) TextView addActionIcon;
    @Bind(R.id.button_viewNotes_text) TextView viewNotesText;
    @Bind(R.id.button_addAction) RelativeLayout addAction;
    @Bind(R.id.button_viewNotes) RelativeLayout viewNotes;

    @Bind(R.id.detail_activity_layout) DrawerLayout drawerLayout;
    @Bind(R.id.right_drawer) RecyclerView recyclerViewDrawerNotification;
    @Bind(R.id.button_moveSection_loading) ProgressBar moveSectionLoading;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        currentUser = CurrentUser.get(getApplicationContext());
        workOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        currentUser.addRecentlyViewedWorkOrder(workOrder);

        self = this;
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        notificationManager = NotificationManager.get(this, recyclerViewDrawerNotification);
        populateViews();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DetailActivity onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        //this.menu = menu;
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
            case R.id.action_queue:
                beginActionQueueActivity();
                break;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.log_out:
                currentUser.logoutUser(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }


    private void populateViews(){
        setTitle("Work Order");


        proposal.setText(workOrder.getProposalPhase());
        description.setText(workOrder.getDescription());
        valueAgo.setText(workOrder.getDateElements()[3]);
        timeAgo.setText(workOrder.getDateElements()[4]);
       // dateCreated.setText("Requested: " + workOrder.getDateCreated() + " by " + workOrder.getContactName() + " (" + workOrder.getDepartment() + ")");
        requestor.setText(workOrder.getContactName());
        requestorDepartment.setText(workOrder.getDepartment());
        requestorTime.setText(workOrder.getDateCreated());


        if (!workOrder.getLocationCode().equals("null")){
            location.setText(workOrder.getBuilding() + "; Room # " + workOrder.getLocationCode());
        } else {
            location.setText(workOrder.getBuilding());
        }
        priority.setText(workOrder.getPriority());

        status.setText(workOrder.getStatus());
        assigned.setText("None");
        funding.setText(workOrder.getCraftCode());
        category.setText(workOrder.getCategory());
        shop.setText(workOrder.getShop());


        Typeface FONTAWESOME = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        Typeface GUDEA = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Gudea-Regular.otf");
        Typeface GUDEABOLD = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Gudea-Bold.otf");
        description.setTypeface(GUDEA);
        proposal.setTypeface(GUDEABOLD);
        //dateCreated.setTypeface(GUDEA);



        switch (workOrder.getPriority()){
            case "TIME SENSITIVE":
                priorityIcon.setImageResource(R.drawable.priority_time_sensitive);
                break;
            case "URGENT":
                priorityIcon.setImageResource(R.drawable.priority_urgent);
                break;
            case "EMERGENCY":
                priorityIcon.setImageResource(R.drawable.priority_emergency);
                break;
            default:
                priorityIcon.setVisibility(View.GONE);
        }


        switch (workOrder.getStatus()){

            case "ASSIGNED":
                statusIcon.setImageResource(R.drawable.status_assigned);
                break;
            case "WORK IN PROGRESS":
                statusIcon.setImageResource(R.drawable.status_work_in_progress);
                break;
            case "WORK COMPLETE":
                statusIcon.setImageResource(R.drawable.status_work_complete);
                break;
            case "ON HOLD":
                statusIcon.setImageResource(R.drawable.status_on_hold);
                break;
        }


        initMoveSection();

        notesAdapter = new NoteAdapter(this, workOrder.getNotes());

        //Set up bottom buttons
        viewNotesIcon.setText(getString(R.string.icon_list));
        viewNotesIcon.setTypeface(FONTAWESOME);
        addActionIcon.setText(getString(R.string.icon_timeLog));
        addActionIcon.setTypeface(FONTAWESOME);
        viewNotesText.setText("Notes (" + notesAdapter.getCount() + ")");

        if (!(workOrder.getSection().equals(Constants.SECTION_DAILY) || workOrder.getSection().equals(Constants.SECTION_BACKLOG))){
            moveSection.setVisibility(View.GONE);
        }

        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "details wo: "+workOrder);
                Bundle bundle = new Bundle();
                bundle.putSerializable("WorkOrder", workOrder);
                bundle.putString("Title", "New Action");
                DialogUtils.createAddActionDialog(self, bundle);
            }
        });

        viewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // createNotesViewDialog();
                DialogUtils.createNotesViewDialog(self, notesAdapter);
            }
        });

        //Class.forName("edu.oregonstate.AiMLiteMobile.Activities.ActionQueueListActivity")
    }

    private void initMoveSection(){
        Typeface FONTAWESOME = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        moveSectionTextIcon.setTypeface(FONTAWESOME);
        if (workOrder.getSection().equals(Constants.SECTION_DAILY)){
            moveSectionTextIcon.setText(getString(R.string.icon_moveToBacklog));
            moveSectionTitle.setText("To Backlog");
        } else if (workOrder.getSection().equals(Constants.SECTION_BACKLOG)){
            moveSectionTextIcon.setText(getString(R.string.icon_moveToDaily));
            moveSectionTitle.setText("To Daily");
        }

        final Animation sectionChangeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_dim);
        AnimationUtils.loadAnimation(this, R.anim.fade_out_dim);

        sectionChangeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //toggleSectionTitleViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        moveSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.createConfirmDialog(self, "Move Section?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestUpdateSection();
                    }
                });
            }
        });

    }

    private void requestUpdateSection(){
        // moveSectionTextIcon.startAnimation(sectionChangeAnim);
        //moveSectionTitle.startAnimation(sectionChangeAnim);

        String newSection = "";

        if (workOrder.getSection().equals(Constants.SECTION_DAILY)) {
            newSection = "Backlog";
        } else if (workOrder.getSection().equals(Constants.SECTION_BACKLOG)) {
            newSection = "Daily Assignment";
        }

        final String newSectionFinal = newSection;

        moveSectionLoading.setVisibility(View.VISIBLE);
        moveSectionTextIcon.setVisibility(View.INVISIBLE);
        moveSectionTitle.setVisibility(View.INVISIBLE);

        ApiManager.getService().updateSection(currentUser.getUsername(), workOrder.getProposalPhase(), newSection, new Date(System.currentTimeMillis()).toString(), currentUser.getToken(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "updateSection Success");
                toggleSectionTitleViews();

                //TODO: is work order object same/different than one in currentUser list?
                for (WorkOrder workOrderIter : currentUser.getWorkOrders()){
                    if (workOrderIter.getProposalPhase().equals(workOrder.getProposalPhase())){
                        workOrderIter.setSection(newSectionFinal);
                        workOrder = workOrderIter;
                    }
                }

                moveSectionLoading.setVisibility(View.INVISIBLE);
                moveSectionTextIcon.setVisibility(View.VISIBLE);
                moveSectionTitle.setVisibility(View.VISIBLE);
                SnackbarManager.show(Snackbar.with(self).text("Work Order Section Changed").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "updateSection Fail: " + error.getMessage());
                moveSectionLoading.setVisibility(View.INVISIBLE);
                moveSectionTextIcon.setVisibility(View.VISIBLE);
                moveSectionTitle.setVisibility(View.VISIBLE);
                SnackbarManager.show(Snackbar.with(self).text("Move Section Failed").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
            }


        });

    }

    private void toggleSectionTitleViews(){
        if (moveSectionTitle.getText().equals("To Backlog")) {
            moveSectionTitle.setText("To Daily");
            moveSectionTextIcon.setText(getString(R.string.icon_moveToDaily));
        }else if(moveSectionTitle.getText().equals("To Daily")) {
            moveSectionTitle.setText("To Backlog");
            moveSectionTextIcon.setText(getString(R.string.icon_moveToBacklog));
        }
    }


    public void beginActionQueueActivity(){
        Intent i = new Intent(this, ActionQueueListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}
