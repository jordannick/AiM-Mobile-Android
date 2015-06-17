package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import edu.oregonstate.AiMLiteMobile.Activities.DetailActivity;
import edu.oregonstate.AiMLiteMobile.Adapters.NoteAdapter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailMainFragment extends Fragment{
    public static String TAG = "DetailMainFragment";

    private Activity mActivity;
    private WorkOrder mWorkOrder;
    private NoteAdapter notesAdapter;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mWorkOrder = ((DetailActivity)mActivity).getWorkOrder();

        //Add current workOrder to recentlyViewed in CurrentUser
        CurrentUser currentUser = CurrentUser.get(getActivity().getApplicationContext());
        currentUser.addRecentlyViewedWorkOrder(mWorkOrder);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_view, parent, false);
        this.v = v;
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Work Order");


        ((TextView)v.findViewById(R.id.row_proposal_detail)).setText(mWorkOrder.getProposalPhase());

        ((TextView)v.findViewById(R.id.descriptionTextView_detail)).setText(mWorkOrder.getDescription());
        ((TextView)v.findViewById(R.id.actionRow_valueAgo_detail)).setText(mWorkOrder.getDateElements()[3]);
        ((TextView)v.findViewById(R.id.actionRow_stringAgo)).setText(mWorkOrder.getDateElements()[4]);

        ((TextView)v.findViewById(R.id.dateCreatedTextView)).setText("Requested: " + mWorkOrder.getDateCreated() + " by " + mWorkOrder.getContactName() + " (" + mWorkOrder.getDepartment() + ")");

        if (!mWorkOrder.getLocationCode().equals("null")){
            ((TextView)v.findViewById(R.id.detailView_textLocation)).setText(mWorkOrder.getBuilding() + "; Room # " + mWorkOrder.getLocationCode());
        } else {
            ((TextView)v.findViewById(R.id.detailView_textLocation)).setText(mWorkOrder.getBuilding());
        }
        ((TextView)v.findViewById(R.id.detailView_textPriority)).setText(mWorkOrder.getPriority());

        ((TextView)v.findViewById(R.id.detailView_textStatus)).setText(mWorkOrder.getStatus());
        ((TextView)v.findViewById(R.id.detailView_textAssigned)).setText("JOHN DOE, DARTH VADER");
        ((TextView)v.findViewById(R.id.detailView_textFunding)).setText(mWorkOrder.getCraftCode());
        ((TextView)v.findViewById(R.id.detailView_textCategory)).setText(mWorkOrder.getCategory());
        ((TextView)v.findViewById(R.id.detailView_textShop)).setText(mWorkOrder.getShop());


        notesAdapter = new NoteAdapter(mActivity, mWorkOrder.getNotes());

        Typeface FONTAWESOME = Typeface.createFromAsset(mActivity.getApplicationContext().getAssets(), "fonts/FontAwesome.otf");
        Typeface GUDEA = Typeface.createFromAsset(mActivity.getApplicationContext().getAssets(), "fonts/Gudea-Regular.otf");
        Typeface GUDEABOLD = Typeface.createFromAsset(mActivity.getApplicationContext().getAssets(), "fonts/Gudea-Bold.otf");
        ((TextView)v.findViewById(R.id.descriptionTextView_detail)).setTypeface(GUDEA);
        ((TextView)v.findViewById(R.id.row_proposal_detail)).setTypeface(GUDEABOLD);
        ((TextView)v.findViewById(R.id.dateCreatedTextView)).setTypeface(GUDEA);

        ImageView priorityImageView = (ImageView)v.findViewById(R.id.imageView_priorityIconDetail);
        switch (mWorkOrder.getPriority()){
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


        ImageView statusImageView = (ImageView)v.findViewById(R.id.imageView_statusIcon);
        switch (mWorkOrder.getStatus()){

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

        ((TextView)v.findViewById(R.id.textView_moveSectionIcon)).setTypeface(FONTAWESOME);
        ((TextView)v.findViewById(R.id.textView_moveSectionIcon)).setText(getString(R.string.icon_moveToBacklog));
        final Animation sectionChangeAnim = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.fade_out_dim);

        v.findViewById(R.id.layout_moveSection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) v.findViewById(R.id.textView_moveSectionTitle)).getText().equals("Move to\nBacklog")) {


                    ((TextView) v.findViewById(R.id.textView_moveSectionIcon)).startAnimation(sectionChangeAnim);
                    ((TextView) v.findViewById(R.id.textView_moveSectionTitle)).startAnimation(sectionChangeAnim);
                    ((TextView) v.findViewById(R.id.textView_moveSectionTitle)).setText("Move to\nDaily");
                    ((TextView) v.findViewById(R.id.textView_moveSectionIcon)).setText(getString(R.string.icon_moveToDaily));




                } else if (((TextView) v.findViewById(R.id.textView_moveSectionTitle)).getText().equals("Move to\nDaily")) {
                    ((TextView) v.findViewById(R.id.textView_moveSectionIcon)).startAnimation(sectionChangeAnim);
                    ((TextView) v.findViewById(R.id.textView_moveSectionTitle)).startAnimation(sectionChangeAnim);
                    ((TextView) v.findViewById(R.id.textView_moveSectionTitle)).setText("Move to\nBacklog");
                    ((TextView) v.findViewById(R.id.textView_moveSectionIcon)).setText(getString(R.string.icon_moveToBacklog));

                }
            }
        });


        //Set up bottom two buttons
        ((TextView)v.findViewById(R.id.button_viewNotes_icon)).setText(getString(R.string.icon_list));
        ((TextView)v.findViewById(R.id.button_viewNotes_icon)).setTypeface(FONTAWESOME);
        ((TextView)v.findViewById(R.id.button_addAction_icon)).setText(getString(R.string.icon_timeLog));
        ((TextView)v.findViewById(R.id.button_addAction_icon)).setTypeface(FONTAWESOME);
        ((TextView) v.findViewById(R.id.button_viewNotes_text)).setText("View Notes (" + notesAdapter.getCount() + ")");

        v.findViewById(R.id.button_addAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open up entry dialog, passing work order object
                AddActionDialogFragment actionFragment = new AddActionDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("WorkOrder", mWorkOrder);
                actionFragment.setArguments(bundle);
                actionFragment.show(getFragmentManager(), "Diag");
            }
        });


        v.findViewById(R.id.button_viewNotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotesViewPopup();
            }
        });

    }

    private void createNotesViewPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.popup_notes_list, null);

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

}
