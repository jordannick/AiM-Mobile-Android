package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import edu.oregonstate.AiMLiteMobile.Activities.DetailActivity;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mWorkOrder = ((DetailActivity)mActivity).getWorkOrder();
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

        /**/
        ((TextView)v.findViewById(R.id.buildingTextView)).setText(mWorkOrder.getBuilding());
        ((TextView)v.findViewById(R.id.descriptionTextView)).setText(mWorkOrder.getDescription());
        ((TextView)v.findViewById(R.id.workCodeTextView)).setText(mWorkOrder.getCraftCode());
        ((TextView)v.findViewById(R.id.shopTextView)).setText(mWorkOrder.getShop());
        ((TextView)v.findViewById(R.id.dateCreatedTextView)).setText(mWorkOrder.getDateCreated());
        ((TextView)v.findViewById(R.id.statusTextView)).setText(mWorkOrder.getStatus());
        ((TextView)v.findViewById(R.id.priorityTextView)).setText(mWorkOrder.getPriority());
        ((TextView)v.findViewById(R.id.priorityTextView)).setBackgroundResource(mWorkOrder.getPriorityColor());

        ((CompoundButton)v.findViewById(R.id.sectionSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mWorkOrder.setSection("Backlog");
                } else {
                    mWorkOrder.setSection("Daily");
                }
            }
        });

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

        ((TextView)v.findViewById(R.id.estTextView)).setText(estText);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
