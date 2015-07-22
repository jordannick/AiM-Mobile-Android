package edu.oregonstate.AiMLiteMobile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 6/15/2015.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {
    private final static String TAG = "AiM_RecyActionAdapter";

    private ArrayList<Action> actions;
    public Callbacks mCallbacks;
    private Context context;

    public interface Callbacks {
        void onActionSelected(Action action);
    }

    public ActionAdapter(ArrayList<Action> actions, Activity activity) {
        mCallbacks = (Callbacks) activity;
        context = activity;
        this.actions = actions;
    }

    public void refreshActions(ArrayList<Action> actions){
        this.actions = actions;
        notifyDataSetChanged();
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_action, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onViewRecycled(ActionViewHolder holder) {
        holder.submitText.setText("Ready to Submit");
        //holder.submitText.setTextColor(context.getResources().getColor(android.R.color.tertiary_text_dark));
        holder.submitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.submit_up));
        //holder.submittedOverlay.setAlpha(0);
        holder.notesCount.setText("No note");
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, int position) {
        final Action action = actions.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onActionSelected(action);
            }
        });

        //Populate the layout items with the action data
//        holder.workOrderId.setText(action.getWorkOrder().getProposalPhase());

        holder.actionTaken.setText(action.getActionTaken());

        if (!action.getNote().equals("")) {
            holder.notesCount.setText(action.getNote());
        }

        if (action.getUpdatedStatus() != null && !action.getUpdatedStatus().equals(action.getWorkOrder().getStatus())){
           holder.oldStatus.setText(action.getWorkOrder().getStatus());
           holder.newStatus.setText(action.getUpdatedStatus());
        } else {
            holder.oldStatus.setVisibility(View.GONE);
            holder.actionChangedArrow.setVisibility(View.GONE);
            holder.newStatus.setText(action.getWorkOrder().getStatus());
        }


       holder.timeSince.setText(prettyPrintDate(action.getDateStamp()));

        if (action.getHours() > 0) {
           holder.hours.setText(String.valueOf(action.getHours()));
        } else {
            holder.hours.setText("0");
        }
        int i = action.getTimeType().indexOf(' ');
        String type = "";
        if (i != -1) {
            type = action.getTimeType().substring(0, i);
        }
        holder.hoursType.setText(type);

        //holder.notesCount.setText(String.valueOf(action.getNotes().size() + " Notes"));

        if (action.isSubmitted()){
            holder.submitText.setText("Submitted!");
            holder.submitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.submit_checked_grey));
            holder.submitText.setTextColor(context.getResources().getColor(android.R.color.tertiary_text_dark));

            //holder.submittedOverlay.setVisibility(View.VISIBLE);
            //holder.submittedOverlay.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public static class ActionViewHolder extends RecyclerView.ViewHolder {

        protected TextView workOrderId;
        protected TextView actionTaken;
        protected TextView oldStatus;
        protected TextView newStatus;
        protected ImageView actionChangedArrow;
        protected TextView timeSince;
        protected TextView hours;
        protected TextView notesCount;
        protected TextView hoursType;
        //protected RelativeLayout submittedOverlay;

        protected TextView submitText;
        protected ImageView submitImage;


        public ActionViewHolder(View v) {
            super(v);
            //workOrderId = (TextView) v.findViewById(R.id.action_work_order_id);
            actionTaken = (TextView) v.findViewById(R.id.action_taken);
            oldStatus = (TextView) v.findViewById(R.id.action_oldStatus);
            newStatus = (TextView) v.findViewById(R.id.action_newStatus);
            actionChangedArrow = (ImageView) v.findViewById(R.id.action_changed_arrow);
            timeSince = (TextView) v.findViewById(R.id.action_timeSince);
            hours = (TextView) v.findViewById(R.id.action_hours);
            notesCount = (TextView) v.findViewById(R.id.action_note);
            //submittedOverlay = (RelativeLayout) v.findViewById(R.id.action_submitted_overlay);

            submitText = (TextView) v.findViewById(R.id.submit_text);
            submitImage = (ImageView) v.findViewById(R.id.submit_image);
            hoursType = (TextView) v.findViewById(R.id.hours_type);
        }
    }


    private String prettyPrintDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return simpleDateFormat.format(date);
    }


}
