package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Adapters.NoteAdapter;
import edu.oregonstate.AiMLiteMobile.Adapters.NoticeAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionDialogFragment;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;


/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    private static CurrentUser currentUser;
    public static WorkOrder workOrder;
    private NoteAdapter notesAdapter;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.row_proposal_detail) TextView proposal;
    @Bind(R.id.descriptionTextView_detail) TextView description;
    @Bind(R.id.actionRow_valueAgo_detail) TextView valueAgo;
    @Bind(R.id.actionRow_stringAgo) TextView timeAgo;
    @Bind(R.id.dateCreatedTextView) TextView dateCreated;

    @Bind(R.id.detailView_textLocation) TextView location;
    @Bind(R.id.detailView_textPriority) TextView priority;
    @Bind(R.id.detailView_textStatus) TextView status;
    @Bind(R.id.detailView_textAssigned) TextView assigned;
    @Bind(R.id.detailView_textFunding) TextView funding;
    @Bind(R.id.detailView_textCategory) TextView category;
    @Bind(R.id.detailView_textShop) TextView shop;
    @Bind(R.id.imageView_priorityIconDetail) ImageView priorityIcon;
    @Bind(R.id.imageView_statusIcon) ImageView statusIcon;
    @Bind(R.id.textView_moveSectionIcon) TextView moveSectionTextIcon;
    @Bind(R.id.layout_moveSection) RelativeLayout moveSection;
    @Bind(R.id.textView_moveSectionTitle) TextView moveSectionTitle;
    @Bind(R.id.button_viewNotes_icon) TextView viewNotesIcon;
    @Bind(R.id.button_addAction_icon) TextView addActionIcon;
    @Bind(R.id.button_viewNotes_text) TextView viewNotesText;
    @Bind(R.id.button_addAction) RelativeLayout addAction;
    @Bind(R.id.button_viewNotes) RelativeLayout viewNotes;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        currentUser = CurrentUser.get(getApplicationContext());
        workOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        currentUser.addRecentlyViewedWorkOrder(workOrder);


        //ButterKnife Time
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
        dateCreated.setText("Requested: " + workOrder.getDateCreated() + " by " + workOrder.getContactName() + " (" + workOrder.getDepartment() + ")");


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


        notesAdapter = new NoteAdapter(this, workOrder.getNotes());

        Typeface FONTAWESOME = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        Typeface GUDEA = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Gudea-Regular.otf");
        Typeface GUDEABOLD = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Gudea-Bold.otf");
        description.setTypeface(GUDEA);
        proposal.setTypeface(GUDEABOLD);
        dateCreated.setTypeface(GUDEA);



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

        moveSectionTextIcon.setTypeface(FONTAWESOME);
        moveSectionTextIcon.setText(getString(R.string.icon_moveToBacklog));
        final Animation sectionChangeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_dim);
        AnimationUtils.loadAnimation(this, R.anim.fade_out_dim);

        sectionChangeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toggleSectionTitleViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        moveSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSection.startAnimation(sectionChangeAnim);
/*                if (moveSectionTitle.getText().equals("Move to\nBacklog")) {
                    moveSection.startAnimation(sectionChangeAnim);
                    //moveSectionTextIcon.startAnimation(sectionChangeAnim);
                    //moveSectionTitle.startAnimation(sectionChangeAnim);
                    moveSectionTitle.setText("Move to\nDaily");
                    moveSectionTextIcon.setText(getString(R.string.icon_moveToDaily));
                } else if (moveSectionTitle.getText().equals("Move to\nDaily")) {
                    //moveSectionTextIcon.startAnimation(sectionChangeAnim);
                   // moveSectionTitle.startAnimation(sectionChangeAnim);
                    moveSectionTitle.setText("Move to\nBacklog");
                    moveSectionTextIcon.setText(getString(R.string.icon_moveToBacklog));
                }*/
            }
        });


        //Set up bottom two buttons
        viewNotesIcon.setText(getString(R.string.icon_list));
        viewNotesIcon.setTypeface(FONTAWESOME);
        addActionIcon.setText(getString(R.string.icon_timeLog));
        addActionIcon.setTypeface(FONTAWESOME);
        viewNotesText.setText("View Notes (" + notesAdapter.getCount() + ")");

        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open up entry dialog, passing work order object
                AddActionDialogFragment actionFragment = new AddActionDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("WorkOrder", workOrder);
                actionFragment.setArguments(bundle);
                actionFragment.show(getFragmentManager(), "Diag");
            }
        });

        viewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotesViewPopup();
            }
        });
    }

    private void toggleSectionTitleViews(){
        if (moveSectionTitle.getText().equals("Move to\nBacklog")) {
            moveSectionTitle.setText("Move to\nDaily");
            moveSectionTextIcon.setText(getString(R.string.icon_moveToDaily));
        }else if(moveSectionTitle.getText().equals("Move to\nDaily")) {
            moveSectionTitle.setText("Move to\nBacklog");
            moveSectionTextIcon.setText(getString(R.string.icon_moveToBacklog));
        }
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

    private void createNotesViewPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog_notes_list, null);

        convertView.findViewById(R.id.dialogNotes_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupNotes_listView);
        lv.setSelector(android.R.color.transparent);
        lv.setAdapter(notesAdapter);
        alertDialog.show();
    }

    public void beginActionQueueActivity(){
        Intent i = new Intent(this, ActionQueueListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}
