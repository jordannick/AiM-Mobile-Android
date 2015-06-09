package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.app.Fragment;

import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import edu.oregonstate.AiMLiteMobile.Activities.DetailActivity;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailMainFragment extends Fragment{
    public static String TAG = "DetailMainFragment";

    private Activity mActivity;
    private WorkOrder mWorkOrder;
    private View v;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

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
        View v = inflater.inflate(R.layout.detail_view_test, parent, false);
        this.v = v;
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Work Order");

        /**/
//        ((TextView)v.findViewById(R.id.idTextView)).setText(mWorkOrder.getProposalPhase());
        ((TextView)v.findViewById(R.id.detailView_textLocation)).setText(mWorkOrder.getBuilding());
        ((TextView)v.findViewById(R.id.descriptionTextView_detail)).setText(mWorkOrder.getDescription());
        ((TextView)v.findViewById(R.id.detailView_textFunding)).setText(mWorkOrder.getCraftCode());
        ((TextView)v.findViewById(R.id.detailView_textShop)).setText(mWorkOrder.getShop());
//        ((TextView)v.findViewById(R.id.dateCreatedTextView)).setText(mWorkOrder.getDateCreated());
        ((TextView)v.findViewById(R.id.detailView_textStatus)).setText(mWorkOrder.getStatus());
        ((TextView)v.findViewById(R.id.detailView_textPriority)).setText(mWorkOrder.getPriority());



        //TODO get perfect font
        Typeface GUDEA = Typeface.createFromAsset(mActivity.getApplicationContext().getAssets(), "fonts/Gudea-Regular.otf");
        Typeface GUDEABOLD = Typeface.createFromAsset(mActivity.getApplicationContext().getAssets(), "fonts/Gudea-Bold.otf");
        ((TextView)v.findViewById(R.id.descriptionTextView_detail)).setTypeface(GUDEA);
        ((TextView)v.findViewById(R.id.row_proposal_detail)).setTypeface(GUDEABOLD);


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



        // TODO: re put in
        // ImageView statusImageView = (ImageView)v.findViewById(R.id.imageView_statusIcon);
/*        switch (mWorkOrder.getStatus()){
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
        }*/
        // end to do

        // ------------------------------------------------------------------------------------

       // ((LinearLayout)v.findViewById(R.id.layout_status)).addView(statusImageView);

        //((TextView)v.findViewById(R.id.priorityTextView)).setBackgroundResource(mWorkOrder.getPriorityColor());

        //TODO - I think the below changing the INSTANCE of the work order, rather than work order object displayed in overview list
        /*if (mWorkOrder.getSection().equals("Backlog"))((CompoundButton)v.findViewById(R.id.sectionSwitch)).setChecked(true);
        ((CompoundButton)v.findViewById(R.id.sectionSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWorkOrder.setSection("Backlog");
                    //SnackbarManager.show(Snackbar.with(getActivity()).text("Moved to backlog").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));

                } else {
                    mWorkOrder.setSection("Daily");
                    //SnackbarManager.show(Snackbar.with(getActivity()).text("Moved to daily").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
                }
            }
        });*/

        mWorkOrder.getBeginDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        SimpleDateFormat minimalDate = new SimpleDateFormat("MM/dd", Locale.US);
        SimpleDateFormat minimalDateYear = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String estText = "";
        try {
            Calendar beginCal = new GregorianCalendar();
            Calendar endCal = new GregorianCalendar();
            Date beginDate = null;
            Date endDate = null;
            if (mWorkOrder.getBeginDate() != null){
                beginDate = sdf.parse(mWorkOrder.getBeginDate());
            }
            if (mWorkOrder.getEndDate() != null) {
                endDate = sdf.parse(mWorkOrder.getEndDate());
            }
            beginCal.setTime(beginDate);
            endCal.setTime(endDate);

            if(beginCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)){
                estText = String.format("%s - %s", minimalDateYear.format(beginDate), minimalDateYear.format(endDate));

            }else{
                estText = String.format("%s - %s", minimalDate.format(beginDate), minimalDate.format(endDate));
            }

        }catch (Exception e){
            Log.e(TAG, "Calendar formatting error: " + e);
        }

       // ((TextView)v.findViewById(R.id.estTextView)).setText(estText);


        v.findViewById(R.id.button_addAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddActionDialogFragment actionFragment = new AddActionDialogFragment();
                actionFragment.show(getFragmentManager(), "Diag");
            }
        });



    }




}
