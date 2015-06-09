package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.app.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


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

        ((TextView)v.findViewById(R.id.row_proposal_detail)).setText(mWorkOrder.getProposalPhase());
        ((TextView)v.findViewById(R.id.actionRow_valueAgo)).setText(mWorkOrder.getDateElements()[3]);
        ((TextView)v.findViewById(R.id.actionRow_stringAgo)).setText(mWorkOrder.getDateElements()[4]);


        ((TextView)v.findViewById(R.id.detailView_textLocation)).setText(mWorkOrder.getBuilding() + "; Room # " + mWorkOrder.getLocationCode());
        ((TextView)v.findViewById(R.id.descriptionTextView_detail)).setText(mWorkOrder.getDescription());
        ((TextView)v.findViewById(R.id.detailView_textFunding)).setText(mWorkOrder.getCraftCode());
        ((TextView)v.findViewById(R.id.detailView_textShop)).setText(mWorkOrder.getShop());
         ((TextView)v.findViewById(R.id.dateCreatedTextView)).setText("Requested: " + mWorkOrder.getDateCreated() + " by " + mWorkOrder.getContactName() + " (" + mWorkOrder.getDepartment() + ")");
        ((TextView)v.findViewById(R.id.detailView_textStatus)).setText(mWorkOrder.getStatus());
        ((TextView)v.findViewById(R.id.detailView_textPriority)).setText(mWorkOrder.getPriority());


        //TODO get perfect font
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


        v.findViewById(R.id.button_addAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddActionDialogFragment actionFragment = new AddActionDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("WorkOrder", mWorkOrder);
                actionFragment.setArguments(bundle);

                actionFragment.show(getFragmentManager(), "Diag");
            }
        });


       /* mWorkOrder.getBeginDate();
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
        }*/

       // ((TextView)v.findViewById(R.id.estTextView)).setText(estText);


    }

}
