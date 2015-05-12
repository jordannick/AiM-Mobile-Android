package edu.oregonstate.AiMLiteMobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * Created by sellersk on 8/19/2014.
 */
public class DetailContactFragment extends Fragment {
    private static final String TAG = "DetailContactFragment";

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

        Log.d(TAG, "DetailContact mActivity: " + mWorkOrder);
        ((TextView) mActivity.findViewById(R.id.contact_nameTextView)).setText(mWorkOrder.getContactName());
        ((TextView) mActivity.findViewById(R.id.contact_departmentTextView)).setText(mWorkOrder.getDepartment());

        ImageButton emailButton = (ImageButton) mActivity.findViewById(R.id.contact_button_email);
        ImageButton phoneButton = (ImageButton) mActivity.findViewById(R.id.contact_button_phone);
        ImageButton messageButton = (ImageButton) mActivity.findViewById(R.id.contact_button_message);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail("sellers.kevin@gmail.com");
            }
        });
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber("9712350512");
            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPhoneNumber("9712350512");
            }
        });


        /*

        ((TextView)rootView.findViewById(R.id.contact_department2TextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_typeTextView)).setText();



        ((TextView)rootView.findViewById(R.id.contact_phoneNumTextView)).setText();
        ((TextView)rootView.findViewById(R.id.contact_emailTextView)).setText();
        */
    }

    private void sendEmail(String address){
        Log.d(TAG, "SendEm2ail");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.setType("message/rfc822");
        /*DEBUG - Shows notification if following line is NOT in the if block*/
        startActivity(Intent.createChooser(intent, "Send Mail Using :"));
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {

        }
    }

    private void dialPhoneNumber(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void textPhoneNumber(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
