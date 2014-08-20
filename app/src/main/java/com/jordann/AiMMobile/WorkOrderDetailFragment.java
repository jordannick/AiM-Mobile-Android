package com.jordann.AiMMobile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by jordan_n on 8/13/2014.
 */
public class WorkOrderDetailFragment extends Fragment{

    public static String TAG = "WorkOrderDetailFragment";

    public static final String WORK_ORDER_ID =
            "com.jordann.practice1.workorder_id";

    private WorkOrder mWorkOrder;

    private Callbacks mCallbacks;

    private View v;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onWorkOrderUpdated(WorkOrder wo);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static WorkOrderDetailFragment newInstance(UUID workOrderId) {
        Bundle args = new Bundle();
        args.putSerializable(WORK_ORDER_ID, workOrderId);
        WorkOrderDetailFragment fragment = new WorkOrderDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        // TODO: implement singleton work order list
       // mWorkOrder = CrimeLab.get(getActivity()).getCrime(crimeId); p.193



    }
    */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UUID workOrderId = ((WorkOrderDetailActivity)getActivity()).workOrderId;//Not currently used
        mWorkOrder = ((WorkOrderDetailActivity)getActivity()).mWorkOrder;

        //TODO add phase to proposal
        getActivity().setTitle(mWorkOrder.getProposalPhase());

        ((TextView)v.findViewById(R.id.buildingTextView)).setText(mWorkOrder.getBuilding());
        ((TextView)v.findViewById(R.id.descriptionTextView)).setText(mWorkOrder.getDescription());
        ((View)v.findViewById(R.id.leftSideBar)).setBackgroundResource(mWorkOrder.getPriorityColor());
        ((View)v.findViewById(R.id.rightSideBar)).setBackgroundResource(mWorkOrder.getPriorityColor());
        ((TextView)v.findViewById(R.id.priorityTextView)).setText(mWorkOrder.getPriority());
        ((TextView)v.findViewById(R.id.workCodeTextView)).setText(mWorkOrder.getCraftCode());

        //TODO: format begin+end date
        ((TextView)v.findViewById(R.id.estTextView)).setText(mWorkOrder.getBeginDate());





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_view, parent, false);
        this.v = v;
        return v;
    }


}
