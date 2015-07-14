package edu.oregonstate.AiMLiteMobile.Fragments;
/*
import android.app.DialogFragment;*/
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import edu.oregonstate.AiMLiteMobile.Activities.ActionQueueListActivity;
import edu.oregonstate.AiMLiteMobile.Helpers.InputFilterMinMax;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Note;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

public class AddActionDialogFragment extends DialogFragment {
    private static final String TAG = "AddActionDialogFragment";

    private static CurrentUser currentUser;
    private WorkOrder workOrder;
    private double hoursWorked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hoursWorked = 0;
        workOrder = (WorkOrder) getArguments().getSerializable("WorkOrder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_action_add, container, false);

        currentUser = CurrentUser.get(getActivity().getApplicationContext());

        final Dialog actionDialog = getDialog();
        final ScrollView dialogScrollView = ((ScrollView) (v.findViewById(R.id.layout_action_add)));
        final Spinner statusSpinner = (Spinner) v.findViewById(R.id.spinner_updateStatus);
        final Spinner actionSpinner = (Spinner) v.findViewById(R.id.spinner_actionTaken);
        final Spinner timeTypeSpinner = (Spinner) v.findViewById(R.id.spinner_timetype);
        final EditText noteEditText = ((EditText) (v.findViewById(R.id.editText_note)));
        final TextView hoursEditText = (EditText) v.findViewById(R.id.hoursEditText);
        final TextView hoursRequired = (TextView) v.findViewById(R.id.hours_required);
        final TextView actionRequired = (TextView) v.findViewById(R.id.action_required);


        // Populate the time types spinner from time types in work order
        ArrayList<String> spinnerArray = workOrder.getTimeTypes();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_spinner_item_custom, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);
        timeTypeSpinner.setAdapter(spinnerArrayAdapter);

        //TODO hardcoded string here may change
        if (spinnerArray.contains("REG - REGULAR TIME")) { // Default to regular time type
            timeTypeSpinner.setSelection(spinnerArrayAdapter.getPosition("REG - REGULAR TIME"));
        }

        // Autoselect matching status of work order
        if (workOrder.getStatus().equals("ASSIGNED")) {
            statusSpinner.setSelection(0);
        } else if (workOrder.getStatus().equals("WORK IN PROGRESS")) {
            statusSpinner.setSelection(1);
        } else if (workOrder.getStatus().equals("WORK COMPLETE")) {
            statusSpinner.setSelection(2);
        } else {

        }

        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        hoursEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        hoursEditText.setFilters(new InputFilter[]{new InputFilterMinMax(4, 2)});
        hoursEditText.setGravity(Gravity.CENTER);

        dialogScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (!hoursEditText.isInputMethodTarget()) {
                    dialogScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            dialogScrollView.scrollTo(0, dialogScrollView.getBottom());
                        }
                    });
                }
            }
        });


        //Cancel button dismisses the view and hides keyboard
        v.findViewById(R.id.dialogConfirm_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDialog.dismiss();
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }
        });

        v.findViewById(R.id.dialogConfirm_buttonConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Confirm button starts ActionQueue activity, passing it the new action
                if (getActivity().getLocalClassName().equals("Activities.DetailActivity")) {
                    if (actionSpinner.getSelectedItemPosition() != 0 && hoursWorked > 0) { // Make sure an action is selected
                        currentUser.addAction(createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), timeTypeSpinner.getSelectedItem().toString(), noteEditText.getText().toString()));
                        Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    } else {
                        if (actionSpinner.getSelectedItemPosition() == 0)
                            actionRequired.setVisibility(View.VISIBLE);
                        if (hoursWorked <= 0) hoursRequired.setVisibility(View.VISIBLE);
                    }
                }
                //Confirm button saves the edits to action
                else if (getActivity().getLocalClassName().equals("Activities.ActionQueueListActivity")) {
                    currentUser.addAction(createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), timeTypeSpinner.getSelectedItem().toString(), noteEditText.getText().toString()));
                    //((ActionQueueListFragment)getTargetFragment()).onPostActionDialog();
                    actionDialog.dismiss();
                } else {
                    Log.e(TAG, "Using dialog in unsupported activity");
                }
            }
        });

        v.findViewById(R.id.button_addHours).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursWorked += 0.25;
                if (hoursWorked >= 24) {
                    hoursWorked = 24;
                }
                hoursEditText.setText(String.valueOf(hoursWorked));
            }
        });

        v.findViewById(R.id.button_minusHours).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursWorked -= 0.25;
                if (hoursWorked < 0) { // Don't allow negative hours
                    hoursWorked = 0;
                }
                hoursEditText.setText(String.valueOf(hoursWorked));
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);

    }

    private Action createAction(String actionTaken, String status, String hours, String timeType, String noteString) {
        double hoursDouble = 0;
        ArrayList<Note> notes = new ArrayList<>();

        if (!hours.isEmpty()) {
            hoursDouble = Double.parseDouble(hours);
        }
        if (!noteString.isEmpty()) {
            Note note = new Note(noteString, currentUser.getUsername(), new Date(System.currentTimeMillis()));
            notes.add(note);
        }


        Action newAction = new Action(workOrder, actionTaken, status, hoursDouble, timeType, notes);
        //newAction.setTimeType(Action.TimeType.REG_NB); //%% DEBUG %% Default time type

        return newAction;
    }

}