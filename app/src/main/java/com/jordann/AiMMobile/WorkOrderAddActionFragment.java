package com.jordann.AiMMobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sellersk on 2/17/2015.
 */
public class WorkOrderAddActionFragment extends Fragment {
    public static final String TAG = "WorkOrderActionNewFragment";

    public static final String WORK_ORDER_ID =
            "com.jordann.practice1.workorder_id";

    private View v;

    private int HOURS_MIN = 0;
    private int HOURS_MAX = 8;


    private UUID workOrderId;
    private WorkOrder mWorkOrder;
    private Context mContext;
    private Activity mActivity;
    private CurrentUser currentUser;

    private int hoursEntered = -1;
    private Spinner spinner_ActionTaken;
    private CheckBox checkBox_updateStatus;
    private Spinner spinner_updateStatus;
    private TextView textView_hours;
    private Button button_addNote;

    private ArrayList<WorkOrderNote> newActionNotes;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        //mCallbacks = (Callbacks)this;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static WorkOrderAddActionFragment newInstance(/*UUID workOrderId*/){
        Log.d(TAG, "newInstance");
        //Bundle args = new Bundle();
        //args.putSerializable(WORK_ORDER_ID, workOrderId);
        WorkOrderAddActionFragment fragment = new WorkOrderAddActionFragment();
        //fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        mActivity = getActivity();
        currentUser = CurrentUser.get(mContext);

       mActivity.findViewById(R.id.spinner_updateStatus).setEnabled(false);
       spinner_ActionTaken = (Spinner)mActivity.findViewById(R.id.spinner_actionTaken);
       checkBox_updateStatus = (CheckBox)mActivity.findViewById(R.id.checkBox_updateStatus);
       spinner_updateStatus = (Spinner)mActivity.findViewById(R.id.spinner_updateStatus);
       textView_hours = (TextView)mActivity.findViewById(R.id.hoursText);
       button_addNote = (Button)mActivity.findViewById(R.id.button_addNote);

       newActionNotes = new ArrayList<WorkOrderNote>();

       createHoursEntryDialog();
       createNoteEntryDialog();

    }

    private void createHoursEntryDialog(){

        Button setHoursButton = (Button)getActivity().findViewById(R.id.button_setHours);
        setHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder enterHoursAlert = new AlertDialog.Builder(mActivity);
                enterHoursAlert.setTitle("Enter hours:");
                enterHoursAlert.setMessage("Enter hours:");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setFilters(new InputFilter[]{ new InputFilterMinMax(HOURS_MIN, HOURS_MAX)});
                input.setWidth(50);


                enterHoursAlert.setView(input);

                enterHoursAlert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        textView_hours.setText(input.getText());
                        hoursEntered = Integer.valueOf(input.getText().toString());
                    }
                });

                enterHoursAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                AlertDialog alert = enterHoursAlert.create();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alert.show();

            }
        });

    }

    private void createNoteEntryDialog() {
        button_addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder enterNoteAlert = new AlertDialog.Builder(mActivity);
                enterNoteAlert.setTitle("Enter note:");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                input.setHorizontallyScrolling(false);
                //input.setWidth(50);
                input.setLines(6);
                input.setMinLines(6);
                input.setGravity(Gravity.TOP | Gravity.LEFT);
                input.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
                enterNoteAlert.setView(input);

                enterNoteAlert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Check for null input
                        String noteString = input.getText().toString();
                        if(!noteString.equals("")){
                            //Create noteObject and add to notesArray
                            WorkOrderNote newNote = new WorkOrderNote(noteString, currentUser.getUsername(), new Date(System.currentTimeMillis()));
                            newActionNotes.add(newNote);

                            //Display short toast to notify that note has been saved
                            String toastText = "New note added";
                            Toast toast = Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

                enterNoteAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                AlertDialog alert = enterNoteAlert.create();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alert.show();

            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.action_add, parent, false);
        this.v = v;

        WorkOrderAddActionActivity activity = (WorkOrderAddActionActivity)getActivity();
        WorkOrder workOrder = activity.getWorkOrder();


        return v;
    }

    //Get all values from form. Create Action object. Add to CurrentUser.Actions
    //Displays error or success
    private void validateAction(){
        int hours = -1; //-1 signifies no hours entered
        String newStatus = null;

        //STATUS
        if(checkBox_updateStatus.isChecked()){
            //Assign newStatus the value of selected Status from spinner
            newStatus = spinner_updateStatus.getSelectedItem().toString();
        }// else newStatus stays null

        //HOURS
        if(hoursEntered != -1){
            hours = hoursEntered;
            Log.d(TAG, "HOURS : " + hours);
        }

        //ACTION
        String actionTaken = spinner_ActionTaken.getSelectedItem().toString();
        Log.d(TAG, "ActionTaken : " + actionTaken);

        //NOTE  -- handled in notes dialog. new notes added to newActionNotes arrayList

        WorkOrderAddActionActivity activity = (WorkOrderAddActionActivity)mActivity;


        Action newAction = new Action(activity.getWorkOrder(), actionTaken, newStatus, hours, newActionNotes);
        currentUser.addAction(newAction);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected in Frag");
        switch (item.getItemId()){
            case R.id.action_queue:
                //TODO: form validation
                validateAction();
                if(true) { //If all fields filled
                    Log.d(TAG, "'action to queue' button touched");

                    //Create Action object from form fields
                    //Add new Action object to CurrentUser.Actions
                    //Return to QueueListFragment and update to show added Action
                    Intent i = new Intent(getActivity(), WorkOrderActionQueueListActivity.class);
                    startActivity(i);

                    String toastText = "New action saved to queue";
                    Toast toast = Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT);
                    toast.show();

                    getActivity().finish();
                }
                return true;
            case android.R.id.home:
                //Handled by parent Activity
                return false;
        }

        //return super.onOptionsItemSelected(item);
        return false;
    }
}
