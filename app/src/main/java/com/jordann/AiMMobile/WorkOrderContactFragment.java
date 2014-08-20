package com.jordann.AiMMobile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sellersk on 8/19/2014.
 */
public class WorkOrderContactFragment extends Fragment {

    WorkOrder mWorkOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.contact_view, container, false);
        mWorkOrder = ((WorkOrderDetailActivity)getActivity()).mWorkOrder;



        /*
        ((TextView)rootView.findViewById(R.id.contact_departmentTextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_department2TextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_typeTextView)).setText();


        ((TextView)rootView.findViewById(R.id.contact_nameTextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_phoneNumTextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_emailTextView)).setText();
        */




        return rootView;
    }
}
