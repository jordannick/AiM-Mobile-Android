package com.jordann.practice1;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginFragment extends Fragment implements GetWorkOrdersTask.OnTaskCompleted {

    //private GetWorkOrdersTask.OnTaskCompleted listener;

    private LoginFragment myFragment = this;

    private static final String TAG = "LoginFragment";

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;

    private static CurrentUser sCurrentUser;

    private String mBaseUrl = "http://apps-webdev.campusops.oregonstate.edu/robechar/portal/aim/api";
    private String mAPIVersion = "/1.0.0";
    private String mMethod = "/getWorkOrders";
    private String mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sCurrentUser = CurrentUser.get(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_view, parent, false);
        getActivity().setTitle(R.string.login_title);

        mUsernameField = (EditText)v.findViewById(R.id.login_username);
        mPasswordField = (EditText)v.findViewById(R.id.login_password);
        mLoginButton = (Button)v.findViewById(R.id.login_button);




        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "y u no click");
                String url = "http://apps-webdev.campusops.oregonstate.edu/robechar/portal/aim/api/1.0.0/getWorkOrders/CLARKEM";

               // mUsername = mUsernameField.getText().toString();

               // String url = mBaseUrl+mAPIVersion+mMethod+mUsername;

                GetWorkOrdersTask task = new GetWorkOrdersTask(myFragment, url);
                task.execute();

            }
        });







        return v;
    }

    public void onTaskSuccess(ArrayList<WorkOrder> workOrders) {

        Log.d(TAG, "successed the task");

        sCurrentUser.setWorkOrders(workOrders);

        //TODO: this dun work
        Intent i = new Intent(getActivity(), WorkOrderListActivity.class);
        startActivity(i);

    }

    public void onTaskFail(){
        Log.d(TAG, "failed the task");

        mUsernameField.setError("Password and username didn't match");


    }





}


