package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by sellersk on 8/19/2014.
 */
public class DetailContactFragment extends Fragment {

    private Activity mActivity;
    private WorkOrder mWorkOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mWorkOrder = ((DetailActivity) mActivity).getWorkOrder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.contact_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ((TextView) mActivity.findViewById(R.id.contact_nameTextView)).setText(mWorkOrder.getContactName());
        ((TextView) mActivity.findViewById(R.id.contact_departmentTextView)).setText(mWorkOrder.getDepartment());



        /*

        ((TextView)rootView.findViewById(R.id.contact_department2TextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_typeTextView)).setText();



        ((TextView)rootView.findViewById(R.id.contact_phoneNumTextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_emailTextView)).setText();
        */
    }

}
