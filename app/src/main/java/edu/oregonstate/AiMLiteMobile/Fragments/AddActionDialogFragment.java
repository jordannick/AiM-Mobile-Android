package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import android.widget.TextView;

import edu.oregonstate.AiMLiteMobile.Activities.ActionQueueListActivity;
import edu.oregonstate.AiMLiteMobile.Helpers.InputFilterMinMax;
import edu.oregonstate.AiMLiteMobile.R;

public class AddActionDialogFragment extends DialogFragment {
    private static final String TAG = "AddActionDialogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_action_add, container, false);

        final Dialog actionDialog = getDialog();
        final ScrollView dialogScrollView = ((ScrollView)(v.findViewById(R.id.layout_action_add)));
        EditText noteEditText =  ((EditText)(v.findViewById(R.id.editText_note)));
        TextView hoursEditText = (TextView)v.findViewById(R.id.hoursEditText);

        hoursEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        //hoursEditText.setPadding(8, 16, 8, 16);
        hoursEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 24), new InputFilter.LengthFilter(1)});
        hoursEditText.setGravity(Gravity.CENTER);

        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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
                actionDialog.dismiss();

                //Confirm button starts ActionQueue activity, passing it the new action
                if (getActivity().getLocalClassName().equals("Activities.DetailActivity")) {
                    Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                    getActivity().finish();
                    //TODO pass new action
                    startActivity(intent);
                }

                //Confirm button saves the edits to action
                else if (getActivity().getLocalClassName().equals("Activities.ActionQueueListActivity")) {
                    //Just save action in queue list
                }

                else {
                    Log.e(TAG, "Using dialog in unsupported activity");
                }
            }
        });

        return v;
    }





}