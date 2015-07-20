package edu.oregonstate.AiMLiteMobile.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.view.Window;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.AiMLiteMobile.R;
import retrofit.mime.TypedFile;

/**
 * Created by sellersk on 7/15/2015.
 */
public class BarChartDialogFragment extends DialogFragment{

    private String[] days = {"M","T","W","Th","F","Sa","S"};
    private BarChart barChart;


    private final float Y_AXIS_FONTSIZE = 14f;
    private final float X_AXIS_FONTSIZE = 16f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createBarChart(){
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setDescription("");

        barChart.getLegend().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);



        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(true);

        // draw shadows for each bar that show the maximum value
        // barChart.setDrawBarShadow(true);

        // barChart.setDrawXLabels(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(X_AXIS_FONTSIZE);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);

        ValueFormatter custom = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                DecimalFormat df = new DecimalFormat("0");
                String result = df.format(value);
                if((value % 2) == 0 || value == 0){
                    return df.format(value);
                }
                return "";
            }
        };

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setValueFormatter(custom);
        leftAxis.setTextSize(Y_AXIS_FONTSIZE);
        leftAxis.setAxisMaxValue(8);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        //leftAxis.setTypeface(Typeface.DEFAULT_BOLD);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8);
        rightAxis.setAxisMaxValue(8);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextSize(Y_AXIS_FONTSIZE);
        //rightAxis.setTypeface(Typeface.DEFAULT_BOLD);
        barChart.setDrawValuesForWholeStack(false);



/*        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);*/
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        setData(7, 8);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bar_chart, container, false);

        barChart = (BarChart)v.findViewById(R.id.bar_chart);
        createBarChart();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.ChartDialogAnimation;
        return v;
    }


    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(days[i % 12]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            if(i<3){
                yVals1.add(new BarEntry(8, i));
            }else {
                float mult = (range + 1);
                int val = (int) (Math.random() * mult);

                yVals1.add(new BarEntry(val, i));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        List<Integer> colors = new ArrayList<>();
/*        colors.add(getResources().getColor(R.color.material_red));
        colors.add(getResources().getColor(R.color.material_teal));
        colors.add(getResources().getColor(R.color.material_lime));
        colors.add(getResources().getColor(R.color.material_blue));
        colors.add(getResources().getColor(R.color.material_deep_purple));
        colors.add(getResources().getColor(R.color.material_deep_orange));
        colors.add(getResources().getColor(R.color.material_light_blue));*/

        set1.setColor(getResources().getColor(R.color.material_teal_transparent_50));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);



        BarData data = new BarData(xVals, dataSets);
        // data.setValueFormatter(new MyValueFormatter());
        //data.setValueTextSize(10f);
        data.setDrawValues(false);

        barChart.setData(data);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
