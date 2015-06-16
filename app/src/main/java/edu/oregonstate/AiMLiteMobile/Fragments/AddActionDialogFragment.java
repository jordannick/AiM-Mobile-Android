package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

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

    private static CurrentUser sCurrentUser;
    private WorkOrder workOrder;
    private String dialogTitle;
    private int hoursForText;

    public AddActionDialogFragment(){
        Bundle bundle = getArguments();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hoursForText = 0;
        workOrder = (WorkOrder) getArguments().getSerializable("WorkOrder");
        dialogTitle = getArguments().getString("Title");

    }



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_action_add, container, false);

        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());

        final Dialog actionDialog = getDialog();
        final ScrollView dialogScrollView = ((ScrollView)(v.findViewById(R.id.layout_action_add)));

        final Spinner statusSpinner =  (Spinner)v.findViewById(R.id.spinner_updateStatus);
        final Spinner actionSpinner = (Spinner)v.findViewById(R.id.spinner_actionTaken);
        final EditText noteEditText =  ((EditText)(v.findViewById(R.id.editText_note)));
        final TextView hoursEditText = (TextView)v.findViewById(R.id.hoursEditText);

        TextView title = (TextView)v.findViewById(R.id.dialogNewAction_title);
        title.setText(dialogTitle);


        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        hoursEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        hoursEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 24), new InputFilter.LengthFilter(1)});
        hoursEditText.setGravity(Gravity.CENTER);

        //Scroll to bottom when dialog shifts on New Note press
        dialogScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                dialogScrollView.scrollTo(0, dialogScrollView.getBottom());
            }
        });

        //Scroll to bottom when any note text is entered
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogScrollView.scrollTo(0, dialogScrollView.getBottom());
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
                    if (actionSpinner.getSelectedItemPosition() != 0) { // Make sure an action is selected
                        sCurrentUser.addAction(createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), noteEditText.getText().toString()));
                        Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    } else {
                        SnackbarManager.show(Snackbar.with(getActivity()).text("Action required to submit").duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
                    }
                }
                //Confirm button saves the edits to action
                else if (getActivity().getLocalClassName().equals("Activities.ActionQueueListActivity")) {
                    sCurrentUser.addAction(createAction(actionSpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString(), hoursEditText.getText().toString(), noteEditText.getText().toString()));
                    ((ActionQueueListFragment)getTargetFragment()).onPostActionDialog();
                    actionDialog.dismiss();
                }
                else {
                    Log.e(TAG, "Using dialog in unsupported activity");
                }
            }
        });


        v.findViewById(R.id.button_addHours).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursForText += 1;
                hoursEditText.setText(String.valueOf(hoursForText));
            }
        });

        v.findViewById(R.id.button_minusHours).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursForText -= 1;
                hoursEditText.setText(String.valueOf(hoursForText));
            }
        });



        return v;
    }

    private Action createAction(String actionTaken, String status, String hours, String noteString){
        int hoursInt = 0;
        ArrayList<Note> notes = new ArrayList<>();

        if (!hours.isEmpty()){
            hoursInt = Integer.parseInt(hours);
        }
        if (!noteString.isEmpty()){
            Note note = new Note(noteString, sCurrentUser.getUsername(), new Date(System.currentTimeMillis()));
            notes.add(note);
        }

        Action newAction = new Action(workOrder, actionTaken, status, hoursInt, notes);
        newAction.setTimeType(Action.TimeType.REG_NB); //%% DEBUG %% Default time type

        return newAction;
    }



}