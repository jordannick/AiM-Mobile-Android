package edu.oregonstate.AiMLiteMobile.Activities;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.ChartSpinnerAdapter;
import edu.oregonstate.AiMLiteMobile.Fragments.AddActionDialogFragment;
import edu.oregonstate.AiMLiteMobile.Fragments.BarChartDialogFragment;
import edu.oregonstate.AiMLiteMobile.Fragments.ViewSavedFilesDialog;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.PieChartObj;
import edu.oregonstate.AiMLiteMobile.R;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by sellersk on 7/14/2015.
 */
public class SettingsActivity extends AppCompatActivity{
    public static final String TAG = "SettingsActivity";

    private boolean emailFieldVisible = false;

    private TransitionManager transitionManager;
    private Scene scene0;
    private Scene scene1;
    private Scene scene2;

    @Bind(R.id.settings_toolbar) Toolbar toolbar;
    @Bind(R.id.settings_scene_root) RelativeLayout sceneRoot;
    @Bind(R.id.settings_feedback_button) Button feedbackButton;
    @Bind(R.id.settings_files_button) Button filesButton;
    @Bind(R.id.settings_notification_button) Button notificationButton;
    @Bind(R.id.settings_pie_chart) PieChart pieChart;
    @Bind(R.id.settings_chart_spinner) Spinner chartSpinner;

    private String[] spinnerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // AppCompat //
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // PieChart // TEST
        PieChartObj pieChartObj = new PieChartObj(this, pieChart, CurrentUser.get(this).getWorkOrders());


        final FragmentManager fragmentManager = getSupportFragmentManager();
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");

        feedbackButton.setTypeface(tf);
        feedbackButton.setText(R.string.icon_chart);


        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarChartDialogFragment dialogFragment = new BarChartDialogFragment();
                dialogFragment.show(fragmentManager, "BarChart");
            }
        });

        filesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSavedFilesDialog filesDialog = new ViewSavedFilesDialog();
                filesDialog.show(fragmentManager, "SavedFiles");
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issueNotification();
            }
        });

        spinnerArray = new String[]{"Priority", "Building", "Creator"};
        ChartSpinnerAdapter chartSpinnerAdapter = new ChartSpinnerAdapter(this, spinnerArray);
        chartSpinner.setAdapter(chartSpinnerAdapter);
        chartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Configure chart based on item selected.
                switch (spinnerArray[position]){
                    case "test":
                        Log.d(TAG, "test1 CLICKED");
                        break;
                    case "test2":
                        Log.d(TAG, "test2 CLICKED");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }




    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void issueNotification(){
        final int notificationID = 4;

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("New Notice")
                .setContentText("This is a test notification.");

        Intent resultIntent = new Intent(this, OverviewListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(OverviewListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notifBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notifBuilder.build());
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;
            case R.id.homeAsUp:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}