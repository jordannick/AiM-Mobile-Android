package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Activities.ActionQueueListActivity;
import edu.oregonstate.AiMLiteMobile.Helpers.InputFilterMinMax;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Note;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

public class AddActionDialogFragment extends DialogFragment {
    public static final String TAG = "AddActionDialogFragment";

    private static CurrentUser currentUser;
    private WorkOrder workOrder;
    private Action actionToEdit;
    private double hoursWorked;

    @Bind(R.id.layout_action_add)
    ScrollView dialogScrollView;
    @Bind(R.id.spinner_updateStatus)
    Spinner statusSpinner;
    @Bind(R.id.spinner_actionTaken)
    Spinner actionSpinner;
    @Bind(R.id.spinner_timetype)
    Spinner timeTypeSpinner;
    @Bind(R.id.editText_note)
    EditText noteEditText;
    @Bind(R.id.hoursEditText)
    TextView hoursEditText;
    @Bind(R.id.hours_required)
    TextView hoursRequired;
    @Bind(R.id.action_required)
    TextView actionRequired;
    @Bind(R.id.dialogConfirm_buttonCancel)
    Button buttonCancel;
    @Bind(R.id.dialogConfirm_buttonConfirm)
    Button buttonConfirm;
    @Bind(R.id.button_addHours)
    Button buttonAddHours;
    @Bind(R.id.button_minusHours)
    Button buttonMinusHours;
    @Bind(R.id.dialogNewAction_title)
    TextView dialogTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_action_add, container, false);
        currentUser = CurrentUser.get(getActivity().getApplicationContext());
        ButterKnife.bind(this, v);

        Dialog actionDialog = getDialog();
        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        workOrder = (WorkOrder) getArguments().getSerializable("WorkOrder");
        if (inEditMode()) {
            actionToEdit = (Action) getArguments().getSerializable("ActionToEdit");
            hoursWorked = actionToEdit.getHours();
            dialogTitle.setText("Edit Entry");
        } else if (inViewMode()){
            actionToEdit = (Action) getArguments().getSerializable("ActionToEdit");
            hoursWorked = actionToEdit.getHours();
            dialogTitle.setText("View Submitted Entry");
        } else {
            hoursWorked = 0;
        }

        // Always scroll to bottom when layout changes, to keep Cancel/Confirm visible
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

        populateViews();
        setClickListeners(actionDialog);

        if (inViewMode()){
            buttonMinusHours.setVisibility(View.INVISIBLE);
            buttonAddHours.setVisibility(View.INVISIBLE);
            buttonConfirm.setVisibility(View.GONE);
            hoursEditText.setEnabled(false);
            timeTypeSpinner.setEnabled(false);
            actionSpinner.setEnabled(false);
            statusSpinner.setEnabled(false);
            noteEditText.setEnabled(false);
        }

        return v;
    }

    private void populateViews() {
        // Populate the time types spinner from time types in work order
        ArrayList<String> spinnerArray = workOrder.getTimeTypes();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.simple_spinner_item_custom, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);
        timeTypeSpinner.setAdapter(spinnerArrayAdapter);

        // Restrict input in hours worked box
        hoursEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        hoursEditText.setFilters(new InputFilter[]{new InputFilterMinMax(4, 2)});
        hoursEditText.setGravity(Gravity.CENTER);

        // Populate inputs
        if (inEditMode() || inViewMode()) {
            if (actionToEdit != null) {
                hoursEditText.setText(String.valueOf(actionToEdit.getHours()));
                timeTypeSpinner.setSelection(((ArrayAdapter) timeTypeSpinner.getAdapter()).getPosition(actionToEdit.getTimeType()));
                actionSpinner.setSelection(((ArrayAdapter) actionSpinner.getAdapter()).getPosition(actionToEdit.getActionTaken()));
                statusSpinner.setSelection(((ArrayAdapter) statusSpinner.getAdapter()).getPosition(actionToEdit.getUpdatedStatus()));
                if (actionToEdit.getNotes().size() > 0) {
                    noteEditText.setText(actionToEdit.getNotes().get(0).getNote());
                }
            }
        } else {
            //TODO hardcoded string here may change
            if (spinnerArray.contains("REG - REGULAR TIME")) { // Default to regular time type
                timeTypeSpinner.setSelection(spinnerArrayAdapter.getPosition("REG - REGULAR TIME"));
            }
            statusSpinner.setSelection(((ArrayAdapter) actionSpinner.getAdapter()).getPosition(workOrder.getStatus()));
        }
    }

    private void setClickListeners(final Dialog actionDialog) {
        //Cancel button dismisses the view and hides the keyboard
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                actionDialog.dismiss();
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getLocalClassName().equals("Activities.DetailActivity")) { // Fragment appeared from Details
                    if (isInputValid()) {
                        // Create the action and start Action Queue activity
                        currentUser.addAction(createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), timeTypeSpinner.getSelectedItem().toString(), noteEditText.getText().toString()));
                        Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().finish();
                        startActivity(intent);
                    } else {
                        // "Required" text appears for specific fields
                        if (actionSpinner.getSelectedItemPosition() == 0)
                            actionRequired.setVisibility(View.VISIBLE);
                        if (hoursWorked <= 0) hoursRequired.setVisibility(View.VISIBLE);
                    }
                } else if (getActivity().getLocalClassName().equals("Activities.ActionQueueListActivity")) { // Fragment appeared from Action Queue
                    if (inEditMode() && isInputValid()) {
                        for (Action action : currentUser.getActions()) {
                            if (actionToEdit.getActionId() == action.getActionId()) {
                                // Save the new values
                                actionToEdit = createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), timeTypeSpinner.getSelectedItem().toString(), noteEditText.getText().toString());

                                action.replaceValues(
                                        actionToEdit.getActionTaken(),
                                        actionToEdit.getUpdatedStatus(),
                                        actionToEdit.getHours(),
                                        actionToEdit.getTimeType(),
                                        actionToEdit.getNotes());
                            }

                            Log.d(TAG, "actionToEdit = "+actionToEdit+" ; action replaced = "+action);
                        }
                        getActivity().getSupportFragmentManager().popBackStack();
                        ((ActionQueueListActivity) getActivity()).refreshActions();
                        actionDialog.dismiss();
                    } else {
                        Log.d(TAG, "clicked. from actionqueuelistactivty");
                        if (isInputValid()) {
                            Log.d(TAG, "input is valid.");
                            currentUser.addAction(createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), timeTypeSpinner.getSelectedItem().toString(), noteEditText.getText().toString()));
                            getActivity().getSupportFragmentManager().popBackStack();
                            ((ActionQueueListActivity) getActivity()).refreshActions();
                            actionDialog.dismiss();
                        }
                    }


                    //actionDialog.dismiss();
                } else {
                    Log.e(TAG, "Using dialog in unsupported activity");
                }
            }
        });

        buttonAddHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursWorked += 0.25;
                if (hoursWorked >= 24) {
                    hoursWorked = 24;
                }
                hoursEditText.setText(String.valueOf(hoursWorked));
            }
        });

        buttonMinusHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursWorked -= 0.25;
                if (hoursWorked < 0) { // Don't allow negative hours
                    hoursWorked = 0;
                }
                hoursEditText.setText(String.valueOf(hoursWorked));
            }
        });

    }

    private Boolean inViewMode(){
        if (getArguments().getBoolean("ViewMode", false)) {
            return true;
        }
        return false;
    }

    private Boolean inEditMode() {
        if (getArguments().getBoolean("EditMode", false)) {
            return true;
        }
        return false;
    }

    private Boolean isInputValid() {
        return (actionSpinner.getSelectedItemPosition() != 0 && hoursWorked > 0);
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
        Log.d(TAG, "action created = "+newAction);
        return newAction;
    }

}