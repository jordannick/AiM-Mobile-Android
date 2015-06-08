package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;

import edu.oregonstate.AiMLiteMobile.Activities.ActionQueueListActivity;
import edu.oregonstate.AiMLiteMobile.R;
public class AddActionDialogFragment extends DialogFragment {
    private static final String TAG = "AddActionDialogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        /*LinearLayout linLayout= new LinearLayout(getActivity());
        Button b = new Button(getActivity());
        b.setText("Hello Button");
        linLayout.addView(b);
        return linLayout;*/

        final Dialog myDialog=getDialog();
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //myDialog.setTitle("Time Entry");
        View v = inflater.inflate(R.layout.dialog_action_add, container, false);
        final ScrollView dialogScrollView = ((ScrollView) (v.findViewById(R.id.layout_action_add)));
        ((EditText)(v.findViewById(R.id.editText_note))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialogScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        // This method works but animates the scrolling
                        // which looks weird on first load
                        // scroll_view.fullScroll(View.FOCUS_DOWN);

                        // This method works even better because there are no animations.
                        dialogScrollView.scrollTo(0, dialogScrollView.getBottom());
                    }
                });

            }
        });

        ((EditText)(v.findViewById(R.id.editText_note))).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "onfocus changed listener BEFORE: " + dialogScrollView.getScrollY());

                    v.performClick();
                    
                    dialogScrollView.scrollTo(0, dialogScrollView.getBottom());
                    Log.d(TAG, "onfocus changed listener AFTER: "+ dialogScrollView.getScrollY());
                }
            }
        });
        ((EditText)(v.findViewById(R.id.editText_note))).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "before: "+s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "on: "+s);
                String string = s.toString();
                //if (string.length() > 0 && string.charAt(string.length() - 1) == '\n') {
                    // do stuff
                    dialogScrollView.scrollTo(0, dialogScrollView.getBottom());
               // }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "after: "+s);
            }
        });

        v.findViewById(R.id.dialogConfirm_buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        v.findViewById(R.id.dialogConfirm_buttonConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

                if (getActivity().getLocalClassName().equals("Activities.DetailActivity")) {
                    //close details activity, send action to queue list
                    Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                } else if (getActivity().getLocalClassName().equals("Activities.ActionQueueListActivity")) {
                    //Just save action in queue list
                } else {
                    Log.e(TAG, "using dialog in unsupported activity");
                }


            }
        });

        return v;
    }
}