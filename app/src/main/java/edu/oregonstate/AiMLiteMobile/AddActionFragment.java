package edu.oregonstate.AiMLiteMobile;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sellersk on 2/17/2015.
 */
public class AddActionFragment extends Fragment {
    public static final String TAG = "AddActionFragment";

    private boolean editMode; //False = add new action from scratch //True = edit existing action

    private Activity mActivity;
    private Context mContext;
    private static CurrentUser sCurrentUser;
    private static WorkOrder mWorkOrder;
    private static Action mActionToEdit;

    private static TextView workOrderIdText;
    private static TextView workOrderLocationText;
    private static Spinner spinner_ActionTaken;
    private static TextView textView_hours;
    private static int hoursEntered = -1;
    private static CheckBox checkBox_updateStatus;
    private static Spinner spinner_updateStatus;
    private static Button button_addNote;
    private static ListView notesListView;
    private static NoteAdapter notesAdapter;
    private static ArrayList<Note> newActionNotes;

    private AlertDialog.Builder editOrDeleteDialog;

    private int originalHours;

    private int HOURS_MIN = 0;
    private int HOURS_MAX = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        sCurrentUser = CurrentUser.get(mContext);

        Bundle bundle = getArguments();
        editMode = bundle.getBoolean("editMode");

        if (editMode){
            mActionToEdit = ((AddActionActivity) mActivity).getAction();
            if (mActionToEdit != null) mWorkOrder = mActionToEdit.getWorkOrder();
        } else {
            mWorkOrder = ((AddActionActivity) mActivity).getWorkOrder();
        }

        if (savedInstanceState != null){
            mWorkOrder = (WorkOrder) savedInstanceState.getSerializable("WorkOrder");
            mActionToEdit = (Action) savedInstanceState.getSerializable("Action");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.action_add, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get activity again for after rotation change
        mActivity = getActivity();

        workOrderIdText = (TextView)mActivity.findViewById(R.id.workOrderIdText);
        workOrderLocationText = (TextView)mActivity.findViewById(R.id.workOrderLocationText);
        spinner_ActionTaken = (Spinner)mActivity.findViewById(R.id.spinner_actionTaken);
        textView_hours = (TextView)mActivity.findViewById(R.id.hoursText);
        checkBox_updateStatus = (CheckBox)mActivity.findViewById(R.id.checkBox_updateStatus);
        spinner_updateStatus = (Spinner)mActivity.findViewById(R.id.spinner_updateStatus);
        button_addNote = (Button)mActivity.findViewById(R.id.button_addNote);

        workOrderIdText.setText(mWorkOrder.getProposalPhase());
        workOrderLocationText.setText(mWorkOrder.getBuilding());

        String defaultStatus = "";

        if (editMode){
            ArrayAdapter actionTakenAdapter = (ArrayAdapter) spinner_ActionTaken.getAdapter();
            int defaultActionTakenSpinnerPosition = actionTakenAdapter.getPosition(mActionToEdit.getActionTakenString());
            spinner_ActionTaken.setSelection(defaultActionTakenSpinnerPosition);

            spinner_updateStatus.setEnabled(true);
            spinner_updateStatus.setClickable(true);
            checkBox_updateStatus.setChecked(true);

            defaultStatus = mActionToEdit.getUpdatedStatus(); //Sets the status spinner to the current status of the work order

            newActionNotes = mActionToEdit.getNotes();//Associate new notes with notes list view

            hoursEntered = mActionToEdit.getHours();
            originalHours = hoursEntered;
        } else {
            spinner_updateStatus.setEnabled(false);

            defaultStatus = mWorkOrder.getStatus(); //Sets the status spinner to the current status of the work order

            if (newActionNotes == null) newActionNotes = new ArrayList<Note>();//Associate new notes with notes list view
        }


        ArrayAdapter statusAdapter = (ArrayAdapter) spinner_updateStatus.getAdapter();
        int defaultStatusSpinnerPosition = statusAdapter.getPosition(defaultStatus);
        spinner_updateStatus.setSelection(defaultStatusSpinnerPosition);

        notesAdapter = new NoteAdapter(getActivity(), newActionNotes);
        notesListView = (ListView)getActivity().findViewById(R.id.notesListView);
        notesListView.setEmptyView(getActivity().findViewById(R.id.notesListViewEmpty));
        notesListView.setAdapter(notesAdapter);


        //Check for long click on items in ListView
        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "LongClick detected on notesListView. View: " + view + ", i: " + i + ", l: " + l);
                int clickedNoteIndex = i;

                //Grab clicked Note obj
                Note clickedNote = newActionNotes.get(i);

                Log.d(TAG, "Clicked Note: " + clickedNote.getNote());

                //Show dialog to prompt user to Delete or Edit the Note
                //PARAMS: clickedNoteIndex
                createNoteLongClickDialog(clickedNoteIndex);
                //Respond to User input
                return true;
            }
        });


        if (hoursEntered == -1){
            textView_hours.setText("-");
        } else {
            textView_hours.setText(String.valueOf(hoursEntered));
        }

        createStatusCheckboxHandler(defaultStatusSpinnerPosition);
        createHoursEntryDialog();
        //createNoteEntryDialog(); //REMOVED since the click handler has been moved below, instead of inside the function.
            //This allows the function to also be called when editing an existing note.


        button_addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNoteEntryDialog(null);
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("WorkOrder", mWorkOrder);
        outState.putSerializable("Action", mActionToEdit);
        super.onSaveInstanceState(outState);
    }

    //Creates dialog for editing/deleting notes if none exists. Otherwise, uses previously built.
    //PARAMS: clickedNoteIndex: index of clicked Note within newActionNotes
    private void createNoteLongClickDialog(final int clickedNoteIndex){
        if(editOrDeleteDialog == null){
            Log.d(TAG, "Creating new dialog");
            editOrDeleteDialog = new AlertDialog.Builder(mActivity);
            editOrDeleteDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Remove the note and update list
                    newActionNotes.remove(clickedNoteIndex);
                    notesAdapter.notifyDataSetChanged();
                    Toast.makeText(mContext, "Note Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            editOrDeleteDialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    createNoteEntryDialog(newActionNotes.get(clickedNoteIndex));
                    Toast.makeText(mContext, "Note Edit", Toast.LENGTH_SHORT).show();
                }
            });
            editOrDeleteDialog.show();
        }else{
            editOrDeleteDialog.show();
        }
    }


    private void createHoursEntryDialog(){
        Button setHoursButton = (Button)getActivity().findViewById(R.id.button_setHours);
        setHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder enterHoursAlert = new AlertDialog.Builder(mActivity);
                enterHoursAlert.setTitle("Enter hours:");
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
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                AlertDialog alert = enterHoursAlert.create();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alert.show();
            }
        });

        Button clearHoursButton = (Button)getActivity().findViewById(R.id.button_clearHours);
        clearHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_hours.setText("-");
                hoursEntered = -1;
            }
        });
    }


    //PARAMS: toBeEditedText: set if 'editing an existing note'
    private void createNoteEntryDialog(final Note toBeEditedNote) {
        final AlertDialog.Builder enterNoteAlert = new AlertDialog.Builder(mActivity);
        enterNoteAlert.setTitle("Enter note:");

        //Populate input if editing
        final EditText input = new EditText(getActivity());
        if(toBeEditedNote != null){
            input.setText(toBeEditedNote.getNote());
        }


        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHorizontallyScrolling(false);
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
                if (!noteString.equals("")) {
                    if(toBeEditedNote != null){
                        //Update existing note
                        toBeEditedNote.setNote(noteString);
                        Toast.makeText(mContext, "Note updated", Toast.LENGTH_SHORT).show();
                    }else{
                        //Create noteObject and add to notesArray
                        Note newNote = new Note(noteString, sCurrentUser.getUsername(), new Date(System.currentTimeMillis()));
                        newActionNotes.add(0, newNote);
                        Toast.makeText(mContext, "Note added", Toast.LENGTH_SHORT).show();
                    }
                    notesAdapter.notifyDataSetChanged();

                    if (notesListView.getVisibility() == View.INVISIBLE) {
                        notesListView.setVisibility(View.VISIBLE);
                    }
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

        //Sets cursor to end of input for editing when in EDIT mode
        if(toBeEditedNote != null) {
            input.setSelection(toBeEditedNote.getNote().length(), toBeEditedNote.getNote().length());
        }
    }


    public void createStatusCheckboxHandler(final int defaultStatusSpinnerPosition){
        checkBox_updateStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                getActivity().findViewById(R.id.spinner_updateStatus).setClickable(checked);
                getActivity().findViewById(R.id.spinner_updateStatus).setEnabled(checked);

                //Reset to default if user is not changing it
                if (!checked) {
                    spinner_updateStatus.setSelection(defaultStatusSpinnerPosition);
                }
            }
        });
    }


    private void createConfirmDialog(){
        final AlertDialog.Builder confirmAddActionDialog = new AlertDialog.Builder(mActivity);

        if (editMode){
            confirmAddActionDialog.setTitle("Save action changes");

            confirmAddActionDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Create Action object from form fields
                    //Add new Action object to CurrentUser.Actions
                    //Return to QueueListFragment and update to show added Action

                    String toastText = "Action Saved";
                    Toast toast = Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT);
                    toast.show();

                    getActivity().finish();
                }
            });
        } else {
            confirmAddActionDialog.setTitle("Add new action");

            confirmAddActionDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Create Action object from form fields
                    //Add new Action object to CurrentUser.Actions
                    //Return to QueueListFragment and update to show added Action
                    Intent intent = new Intent(getActivity(), ActionQueueListActivity.class);
                    startActivity(intent);

                    String toastText = "Action Added";
                    Toast toast = Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT);
                    toast.show();

                    getActivity().finish();
                }
            });
        }

        confirmAddActionDialog.setMessage("Confirm?");

        confirmAddActionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alert = confirmAddActionDialog.create();
        alert.show();
    }


    private void saveEdits(){
        String newStatus = mActionToEdit.getUpdatedStatus();

        //STATUS
        if(checkBox_updateStatus.isChecked()){
            //Assign newStatus the value of selected Status from spinner
            newStatus = spinner_updateStatus.getSelectedItem().toString();
        }// else newStatus stays null

        //ACTION
        String actionTaken = spinner_ActionTaken.getSelectedItem().toString();

        //NOTE  -- handled in notes dialog. new notes added to newActionNotes arrayList

        //TODO: don't edit immediately, allow confirm first
        mActionToEdit.setActionTakenString(actionTaken);
        mActionToEdit.setHours(hoursEntered);
        mActionToEdit.setUpdatedStatus(newStatus);
        mActionToEdit.setNotes(newActionNotes);
        mActionToEdit.setDateStamp(new Date(System.currentTimeMillis()));

    }


    //Get all values from form. Create Action object. Add to CurrentUser.Actions
    //Displays error or success
    private void validateAction(){
        int hours = -1; //-1 signifies no hours entered
        String selectedStatus = spinner_updateStatus.getSelectedItem().toString();
        String currentStatus = mWorkOrder.getStatus();
        String newStatus = null;

        //STATUS
        if(!selectedStatus.equals(currentStatus)){ //If selectedStatus is different than current
            newStatus = selectedStatus;
            Log.d(TAG, "New status set.");
        } //else newStatus stays null

        //HOURS
        if(hoursEntered != -1){
            hours = hoursEntered;
        }

        //ACTION
        String actionTaken = spinner_ActionTaken.getSelectedItem().toString();
        //NOTE  -- handled in notes dialog. new notes added to newActionNotes arrayList




        //TODO: don't add action immediately, allow confirming first
        Log.i(TAG, "Adding new Action ( " + actionTaken + " ) for Work Order " + mWorkOrder.getProposalPhase() + " to Queue");
        Action newAction = new Action(mWorkOrder, actionTaken, newStatus, hours, newActionNotes);
        newAction.setTimeType(Action.TimeType.REG_NB); //%% DEBUG %% Default time type
        sCurrentUser.addAction(newAction);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_queue:

                if (editMode){
                    saveEdits();
                } else {
                    validateAction();
                }

                //TODO: finish form validation
                //All fields checked to be correct at this point
                createConfirmDialog();

                return true;
            case android.R.id.home:
                getActivity().finish();
                return false;
        }
        return false;
    }
}
