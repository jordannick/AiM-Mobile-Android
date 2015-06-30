package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        currentUser = CurrentUser.get(getApplicationContext());
        workOrder = (WorkOrder)getIntent().getSerializableExtra(WorkOrder.WORK_ORDER_EXTRA);
        currentUser.addRecentlyViewedWorkOrder(workOrder);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getActionBar() != null){
            //toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            getActionBar().setHomeButtonEnabled(true);
            //toolbar.setTitle("Work Order");
            /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });*/
            toolbar.inflateMenu(R.menu.menu_detail);
        }

        populateViews();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DetailActivity onDestroy");
        super.onDestroy();
    }

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

        ((TextView)findViewById(R.id.row_proposal_detail)).setText(workOrder.getProposalPhase());
        ((TextView)findViewById(R.id.descriptionTextView_detail)).setText(workOrder.getDescription());
        ((TextView)findViewById(R.id.actionRow_valueAgo_detail)).setText(workOrder.getDateElements()[3]);
        ((TextView)findViewById(R.id.actionRow_stringAgo)).setText(workOrder.getDateElements()[4]);
        ((TextView)findViewById(R.id.dateCreatedTextView)).setText("Requested: " + workOrder.getDateCreated() + " by " + workOrder.getContactName() + " (" + workOrder.getDepartment() + ")");

        if (!workOrder.getLocationCode().equals("null")){
            ((TextView)findViewById(R.id.detailView_textLocation)).setText(workOrder.getBuilding() + "; Room # " + workOrder.getLocationCode());
        } else {
            ((TextView)findViewById(R.id.detailView_textLocation)).setText(workOrder.getBuilding());
        }
        ((TextView)findViewById(R.id.detailView_textPriority)).setText(workOrder.getPriority());

        ((TextView)findViewById(R.id.detailView_textStatus)).setText(workOrder.getStatus());
        ((TextView)findViewById(R.id.detailView_textAssigned)).setText("None");
        ((TextView)findViewById(R.id.detailView_textFunding)).setText(workOrder.getCraftCode());
        ((TextView)findViewById(R.id.detailView_textCategory)).setText(workOrder.getCategory());
        ((TextView)findViewById(R.id.detailView_textShop)).setText(workOrder.getShop());


        notesAdapter = new NoteAdapter(this, workOrder.getNotes());

        Typeface FONTAWESOME = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        Typeface GUDEA = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Gudea-Regular.otf");
        Typeface GUDEABOLD = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Gudea-Bold.otf");
        ((TextView)findViewById(R.id.descriptionTextView_detail)).setTypeface(GUDEA);
        ((TextView)findViewById(R.id.row_proposal_detail)).setTypeface(GUDEABOLD);
        ((TextView)findViewById(R.id.dateCreatedTextView)).setTypeface(GUDEA);

        ImageView priorityImageView = (ImageView)findViewById(R.id.imageView_priorityIconDetail);
        switch (workOrder.getPriority()){
            case "TIME SENSITIVE":
                priorityImageView.setImageResource(R.drawable.priority_time_sensitive);
                break;
            case "URGENT":
                priorityImageView.setImageResource(R.drawable.priority_urgent);
                break;
            case "EMERGENCY":
                priorityImageView.setImageResource(R.drawable.priority_emergency);
                break;
            default:
                priorityImageView.setVisibility(View.GONE);
        }


        ImageView statusImageView = (ImageView)findViewById(R.id.imageView_statusIcon);
        switch (workOrder.getStatus()){

            case "ASSIGNED":
                statusImageView.setImageResource(R.drawable.status_assigned);
                break;
            case "WORK IN PROGRESS":
                statusImageView.setImageResource(R.drawable.status_work_in_progress);
                break;
            case "WORK COMPLETE":
                statusImageView.setImageResource(R.drawable.status_work_complete);
                break;
            case "ON HOLD":
                statusImageView.setImageResource(R.drawable.status_on_hold);
                break;
        }

        ((TextView)findViewById(R.id.textView_moveSectionIcon)).setTypeface(FONTAWESOME);
        ((TextView)findViewById(R.id.textView_moveSectionIcon)).setText(getString(R.string.icon_moveToBacklog));
        final Animation sectionChangeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_dim);

        findViewById(R.id.layout_moveSection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) findViewById(R.id.textView_moveSectionTitle)).getText().equals("Move to\nBacklog")) {
                    findViewById(R.id.textView_moveSectionIcon).startAnimation(sectionChangeAnim);
                    findViewById(R.id.textView_moveSectionTitle).startAnimation(sectionChangeAnim);
                    ((TextView)findViewById(R.id.textView_moveSectionTitle)).setText("Move to\nDaily");
                    ((TextView)findViewById(R.id.textView_moveSectionIcon)).setText(getString(R.string.icon_moveToDaily));
                } else if (((TextView) findViewById(R.id.textView_moveSectionTitle)).getText().equals("Move to\nDaily")) {
                    findViewById(R.id.textView_moveSectionIcon).startAnimation(sectionChangeAnim);
                    findViewById(R.id.textView_moveSectionTitle).startAnimation(sectionChangeAnim);
                    ((TextView) findViewById(R.id.textView_moveSectionTitle)).setText("Move to\nBacklog");
                    ((TextView) findViewById(R.id.textView_moveSectionIcon)).setText(getString(R.string.icon_moveToBacklog));
                }
            }
        });

        //Set up bottom two buttons
        ((TextView)findViewById(R.id.button_viewNotes_icon)).setText(getString(R.string.icon_list));
        ((TextView)findViewById(R.id.button_viewNotes_icon)).setTypeface(FONTAWESOME);
        ((TextView)findViewById(R.id.button_addAction_icon)).setText(getString(R.string.icon_timeLog));
        ((TextView)findViewById(R.id.button_addAction_icon)).setTypeface(FONTAWESOME);
        ((TextView) findViewById(R.id.button_viewNotes_text)).setText("View Notes (" + notesAdapter.getCount() + ")");

        findViewById(R.id.button_addAction).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.button_viewNotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotesViewPopup();
            }
        });
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
