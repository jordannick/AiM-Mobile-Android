package edu.oregonstate.AiMLiteMobile;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

/**
 * Created by sellersk on 8/5/2015.
 */
public class PieChartObj implements OnChartValueSelectedListener{
    public static final String TAG = "PieChartObj";


    private PieChart pieChart;
    private ArrayList<WorkOrder> workOrders;

    private ArrayList<Integer> colors;
    private Context context;

    public PieChartObj(Context context, PieChart pieChart, ArrayList<WorkOrder> workOrders) {
        this.context = context;
        this.pieChart = pieChart;
        this.workOrders = workOrders;
        changeWorkOrders();

        setupChart();



    }

    private void changeWorkOrders(){

        Random random = new Random();
        for (int i = 0; i < workOrders.size(); i++) {
            WorkOrder wo = workOrders.get(i);
            switch (random.nextInt(5)){
                case 0:
                    wo.setPriority("SCHEDULED");
                    break;
                case 1:
                    wo.setPriority("ROUTINE");
                    break;
                case 2:
                    wo.setPriority("SCHEDULED");
                    break;
                case 3:
                    wo.setPriority("URGENT");
                    break;
                case 4:
                    wo.setPriority("EMERGENCY");
                    break;
                case 5:
                    wo.setPriority("TIME SENSITIVE");
                    break;
            }
        }
    }

    private void setupChart(){
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Test Description");
        pieChart.setDrawHoleEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        //tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        //pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);

        pieChart.setTransparentCircleColor(Color.WHITE);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);
        pieChart.setDrawSliceText(false);
        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);

        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.setCenterText("");


        setData(countWorkOrderPriority());

        pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        // pieChart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();

        //l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        /*l.setXEntrySpace(10f);
        l.setYEntrySpace(3f);
        l.setXOffset(-30f);
        l.setYOffset(0f);*/
        l.setTypeface(Typeface.DEFAULT);
        l.setFormSize(10f);


    }



    private void setData(Map<String, Integer> map){
        WorkOrder workOrder = workOrders.get(0);
        countWorkOrderPriority();
        countWorkOrderBuilding();
        countWorkOrderEditClerk();

        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            yVals.add(new Entry(entry.getValue(), i));
            xVals.add(entry.getKey());
        }

        PieDataSet dataSet = new PieDataSet(yVals, "");
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(24f);
        data.setValueTextColor(R.color.Material_font_blacker);
        data.setValueTypeface(Typeface.DEFAULT);
        dataSet.setColors(colors);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT);
        pieChart.setData(data);
        pieChart.invalidate();
    }


    private Map<String, Integer> countWorkOrderEditClerk(){
        Map<String, Integer> editClerkCountMap = new HashMap<>();


        for (int i = 0; i < workOrders.size(); i++) {
            WorkOrder wo = workOrders.get(0);
            if(!editClerkCountMap.containsKey(wo.getEditClerk())){
                //Add new editClerk entry
                editClerkCountMap.put(wo.getEditClerk(), 1);
            }else{
                int count = editClerkCountMap.get(wo.getEditClerk());
                editClerkCountMap.remove(wo.getEditClerk());
                editClerkCountMap.put(wo.getEditClerk(), ++count);
            }
        }

        for (Map.Entry<String, Integer> entry: editClerkCountMap.entrySet()){
            Log.d(TAG, "Entry: " + entry.getKey() + " : " + entry.getValue());
        }

        return editClerkCountMap;
    }


    private Map<String, Integer> countWorkOrderPriority(){
        colors = new ArrayList<>();
        Map<String, Integer> priorityCountMap= new HashMap<>();
        for (int i = 0; i < workOrders.size(); i++) {
            WorkOrder wo = workOrders.get(i);
            if(!priorityCountMap.containsKey(wo.getPriority())){
                Log.d(TAG, "Adding new key: " + wo.getPriority());
                priorityCountMap.put(wo.getPriority(), 1);
                switch (wo.getPriority()){
                    case "SCHEDULED":
                        colors.add(context.getResources().getColor(R.color.scheduled_blue));
                        break;
                    case "ROUTINE":
                        colors.add(context.getResources().getColor(R.color.routine_green));
                        break;
                    case "URGENT":
                        colors.add(context.getResources().getColor(R.color.urgent_orange));
                        break;
                    case "EMERGENCY":
                        colors.add(context.getResources().getColor(R.color.emergency_red));
                        break;
                    case "TIME SENSITIVE":
                        colors.add(context.getResources().getColor(R.color.timeSensitive_yellow));
                }
            }else{
                //Seen this priority before, +1
                int count = priorityCountMap.get(wo.getPriority());
                priorityCountMap.remove(wo.getPriority());
                priorityCountMap.put(wo.getPriority(), ++count);
            }
        }

        for(Map.Entry<String, Integer> entry : priorityCountMap.entrySet()){
            Log.d(TAG, "" + entry.getKey() + " : " +entry.getValue());
        }
        return priorityCountMap;
    }

    private Map<String, Integer> countWorkOrderBuilding(){
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < workOrders.size(); i++) {
            WorkOrder wo = workOrders.get(i);
            if(!map.containsKey(wo.getBuilding())){
                map.put(wo.getBuilding(), 1);
            }else{
                int newCount = map.get(wo.getBuilding());
                map.remove(wo.getBuilding());
                map.put(wo.getBuilding(), ++newCount);
            }
        }
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            Log.d(TAG, "" + entry.getKey() + " : " +entry.getValue());
        }
        return map;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
