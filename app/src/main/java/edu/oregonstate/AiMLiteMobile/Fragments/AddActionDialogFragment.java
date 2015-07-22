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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Activities.ActionQueueListActivity;
import edu.oregonstate.AiMLiteMobile.Helpers.InputFilterMinMax;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

public class AddActionDialogFragment extends DialogFragment {
    public static final String TAG = "AddActionDialogFragment";

    private static CurrentUser currentUser;
    private WorkOrder workOrder;
    private Action actionToEdit;
    private double hoursWorked;

    private static final double MIN_HOURS = 0;
    private static final double MAX_HOURS = 24;
    private static final double INCR_HOURS = 0.25;

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
        } else if (inViewMode()) {
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

        // Make everything non-interactive for view only
        if (inViewMode()) {
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

        // Restrict input decimals in hours worked box
        hoursEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        hoursEditText.setFilters(new InputFilter[]{new InputFilterMinMax(4, 2)});
        hoursEditText.setGravity(Gravity.CENTER);

        // Populate fields
        if (inEditMode() || inViewMode()) {
            if (actionToEdit != null) {
                hoursEditText.setText(String.valueOf(actionToEdit.getHours()));
                timeTypeSpinner.setSelection(((ArrayAdapter) timeTypeSpinner.getAdapter()).getPosition(actionToEdit.getTimeType()));
                actionSpinner.setSelection(((ArrayAdapter) actionSpinner.getAdapter()).getPosition(actionToEdit.getActionTaken()));
                statusSpinner.setSelection(((ArrayAdapter) statusSpinner.getAdapter()).getPosition(actionToEdit.getUpdatedStatus()));
                noteEditText.setText(actionToEdit.getNote());

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

                if (getActivity().getLocalClassName().equals("Activities.DetailActivity") && isInputValid()) { // Fragment was started from Work Order Details

                    // Create the action and start Action Queue activity

                    currentUser.addAction(createAction(
                            actionSpinner.getSelectedItem().toString(),
                            statusSpinner.getSelectedItem().toString(),
                            hoursEditText.getText().toString(),
                            timeTypeSpinner.getSelectedItem().toString(),
                            noteEditText.getText().toString()));

                    Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                    getActivity().finish();
                    startActivity(intent);

                } else if (getActivity().getLocalClassName().equals("Activities.ActionQueueListActivity")) { // Fragment was started from Action Queue

                    if (isInputValid()) {
                        if (inEditMode()) { // Fragment was started from action list item

                            // Find matching stored action; replace it with a new action with updated values

                            for (Action action : currentUser.getActions()) {
                                if (actionToEdit.getActionId() == action.getActionId()) {

                                    actionToEdit = createAction(
                                            actionSpinner.getSelectedItem().toString(),
                                            statusSpinner.getSelectedItem().toString(),
                                            hoursEditText.getText().toString(),
                                            timeTypeSpinner.getSelectedItem().toString(),
                                            noteEditText.getText().toString());

                                    action.replaceValues(
                                            actionToEdit.getActionTaken(),
                                            actionToEdit.getUpdatedStatus(),
                                            actionToEdit.getHours(),
                                            actionToEdit.getTimeType(),
                                            actionToEdit.getNote());
                                }
                            }

                        } else { // Fragment was started from "Recently Completed" work order

                            currentUser.addAction(createAction(
                                    actionSpinner.getSelectedItem().toString(),
                                    statusSpinner.getSelectedItem().toString(),
                                    hoursEditText.getText().toString(),
                                    timeTypeSpinner.getSelectedItem().toString(),
                                    noteEditText.getText().toString()));

                        }

                        ((ActionQueueListActivity) getActivity()).refreshActions();
                        actionDialog.dismiss();

                    }
                }
            }
        });

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        buttonAddHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(hoursEditText.getWindowToken(), 0);

                try {
                    hoursWorked = Double.valueOf(hoursEditText.getText().toString());
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    hoursWorked = 0;
                }

                hoursWorked = Math.round(hoursWorked * 4) / 4f; // Round to nearest quarter
                hoursWorked += INCR_HOURS;
                if (hoursWorked >= MAX_HOURS) hoursWorked = MAX_HOURS; // Don't allow too many hours
                hoursEditText.setText(String.valueOf(hoursWorked));
            }
        });

        buttonMinusHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(hoursEditText.getWindowToken(), 0);

                try {
                    hoursWorked = Double.valueOf(hoursEditText.getText().toString());
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    hoursWorked = 0;
                }

                hoursWorked = Math.round(hoursWorked * 4) / 4f; // Round to nearest quarter
                hoursWorked -= INCR_HOURS;
                if (hoursWorked < MIN_HOURS) hoursWorked = MIN_HOURS; // Don't allow negative hours
                hoursEditText.setText(String.valueOf(hoursWorked));
            }
        });

    }

    private Boolean inViewMode() {
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

    // Confirm all required fields are filled or within bounds, and give feedback to user
    private Boolean isInputValid() {

        boolean isValid = true;
        actionRequired.setVisibility(View.GONE);
        hoursRequired.setVisibility(View.GONE);
        hoursRequired.setText("*Required");

        try {
            hoursWorked = Double.valueOf(hoursEditText.getText().toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            hoursWorked = 0;
        }

        if (actionSpinner.getSelectedItemPosition() == 0) {
            actionRequired.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (hoursWorked <= MIN_HOURS) {
            hoursRequired.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (hoursWorked > MAX_HOURS) {
            hoursRequired.setVisibility(View.VISIBLE);
            hoursRequired.setText("Too many hours");
            isValid = false;
        }

        return isValid;
    }

    private Action createAction(String actionTaken, String status, String hours, String timeType, String noteString) {
        double hoursDouble = MIN_HOURS;

        if (!hours.isEmpty()) {
            hoursDouble = Double.parseDouble(hours);
        }

        Action newAction = new Action(workOrder, actionTaken, status, hoursDouble, timeType, noteString);
        return newAction;
    }

}