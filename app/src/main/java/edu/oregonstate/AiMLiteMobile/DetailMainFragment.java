package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class DetailMainFragment extends Fragment{

    public static String TAG = "DetailMainFragment";

    private Activity mActivity;
    private WorkOrder mWorkOrder;

    private Callbacks mCallbacks;

    private View v;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        //void onWorkOrderUpdated();
    }

    //Useful for later if implement swipe to change work order while in detail view
    public static DetailMainFragment newInstance(WorkOrder workOrder) {
        Bundle args = new Bundle();
        args.putSerializable(WorkOrder.WORK_ORDER_EXTRA, workOrder);
        DetailMainFragment fragment = new DetailMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
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

        getActivity().setTitle(mWorkOrder.getProposalPhase());

        RelativeLayout topSection = (RelativeLayout)v.findViewById(R.id.detail_top_section);
        View topSectionLine = v.findViewById(R.id.detail_top_section_line);
        TextView topSectionPriorityTextView = (TextView)v.findViewById(R.id.detail_top_section_priority);
        topSectionPriorityTextView.setText(mWorkOrder.getPriority());
        switch (mWorkOrder.getPriorityColor()){
            case R.color.routine_green:
                topSection.setBackgroundResource(R.drawable.light_green_tile_bg);
                topSectionLine.setBackgroundColor(getResources().getColor(R.color.routine_green));
                topSectionPriorityTextView.setBackgroundColor(getResources().getColor(R.color.routine_green));
                break;
            case R.color.urgent_orange:
                topSection.setBackgroundResource(R.drawable.light_orange_tile_bg);
                topSectionLine.setBackgroundColor(getResources().getColor(R.color.urgent_orange));
                topSectionPriorityTextView.setBackgroundColor(getResources().getColor(R.color.urgent_orange));
                break;
            case R.color.scheduled_blue:
                topSection.setBackgroundResource(R.drawable.light_blue_tile_bg);
                topSectionLine.setBackgroundColor(getResources().getColor(R.color.scheduled_blue));
                topSectionPriorityTextView.setBackgroundColor(getResources().getColor(R.color.scheduled_blue));
                break;
            case R.color.timeSensitive_yellow:
                topSection.setBackgroundResource(R.drawable.light_yellow_tile_bg);
                topSectionLine.setBackgroundColor(getResources().getColor(R.color.timeSensitive_yellow));
                topSectionPriorityTextView.setBackgroundColor(getResources().getColor(R.color.timeSensitive_yellow));
                break;
        }

        ((TextView)v.findViewById(R.id.idTextView)).setText(mWorkOrder.getProposalPhase());
        ((TextView)v.findViewById(R.id.buildingTextView)).setText(mWorkOrder.getBuilding());
        ((TextView)v.findViewById(R.id.descriptionTextView)).setText(mWorkOrder.getDescription());
        //((TextView)v.findViewById(R.id.priorityTextView)).setText(mWorkOrder.getPriority());
        //((TextView)v.findViewById(R.id.priorityTextView)).setTextColor(getResources().getColor(mWorkOrder.getPriorityColor()));
        //mWorkOrder.getPriorityColor()
        ((TextView)v.findViewById(R.id.workCodeTextView)).setText(mWorkOrder.getCraftCode());
        ((TextView)v.findViewById(R.id.shopTextView)).setText(mWorkOrder.getShop());
        ((TextView)v.findViewById(R.id.dateCreatedTextView)).setText(mWorkOrder.getDateCreated());
        ((TextView)v.findViewById(R.id.statusTextView)).setText(mWorkOrder.getStatus());


        mWorkOrder.getBeginDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat minimalDate = new SimpleDateFormat("MM/dd");
        SimpleDateFormat minimalDateYear = new SimpleDateFormat("MM/dd/yy");
        String estText = "";
        try {
            Calendar beginCal = new GregorianCalendar();
            Calendar endCal = new GregorianCalendar();
            Date beginDate = sdf.parse(mWorkOrder.getBeginDate());
            Date endDate = sdf.parse(mWorkOrder.getEndDate());
            beginCal.setTime(beginDate);
            endCal.setTime(endDate);


            if(beginCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)){
                estText = String.format("%s - %s", minimalDateYear.format(beginDate), minimalDateYear.format(endDate));

            }else{
                estText = String.format("%s - %s", minimalDate.format(beginDate), minimalDate.format(endDate));
            }

        }catch (Exception e){
            Log.e(TAG, "Calendar formatting error");
        }

        ((TextView)v.findViewById(R.id.estTextView)).setText(estText);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


}
