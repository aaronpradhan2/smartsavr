package com.example.smartsavr;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ParentSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_summary);

        BarChart barChart = findViewById(R.id.lineChart);

        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0f, 3.5f));
        values.add(new BarEntry(1f, 1f));
        values.add(new BarEntry(2f, 5f));
        values.add(new BarEntry(3f, 2.5f));
        values.add(new BarEntry(4f, 3f));


        ArrayList<BarEntry> values2 = new ArrayList<>();
        values2.add(new BarEntry(0f, 1.5f));
        values2.add(new BarEntry(1f, 0f));
        values2.add(new BarEntry(2f, 3f));
        values2.add(new BarEntry(3f, 1f));
        values2.add(new BarEntry(4f, 2f));


        ArrayList<BarEntry> values3 = new ArrayList<>();
        values3.add(new BarEntry(0f, 1f));
        values3.add(new BarEntry(1f, 2f));
        values3.add(new BarEntry(2f, 5f));
        values3.add(new BarEntry(3f, 4f));
        values3.add(new BarEntry(4f, 5f));
        values3.add(new BarEntry(5f, 4f));


        ArrayList<BarEntry> values4 = new ArrayList<>();
        values4.add(new BarEntry(0f, 5f));
        values4.add(new BarEntry(1f, 4f));
        values4.add(new BarEntry(2f, 3f));
        values4.add(new BarEntry(3f, 2f));
        values4.add(new BarEntry(4f, 2f));



        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Sun");
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tues");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thurs");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");

        XAxis xAxis = barChart.getXAxis();
//        xAxis.setLabelCount(xAxisLabel.size(), true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setCenterAxisLabels(true);
//        xAxis.setGranularity(1);
//        xAxis.setGranularityEnabled(true);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(1.0f);
        YAxis yAxisRight = barChart.getAxisRight();
//        yAxisRight.setGranularityEnabled(true);
//        yAxisRight.setGranularity(1.0f);

        BarDataSet noah = new BarDataSet(values,"Noah");
        noah.setColor(Color.BLUE);
        BarDataSet jim = new BarDataSet(values2,"Jim");
        jim.setColor(Color.GREEN);
        BarDataSet maja = new BarDataSet(values3,"Maja");
        maja.setColor(Color.RED);
        BarDataSet sarah = new BarDataSet(values4,"Sarah");
        sarah.setColor(Color.MAGENTA);
        BarData data = new BarData(noah, jim, maja, sarah);
        barChart.setData(data);

        float barSpace = 0.05f;
        float groupSpace = 0.16f;
        float barWidth = (1-groupSpace) / barChart.getBarData().getDataSetCount() - barSpace;

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        // restrict the x-axis range
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace,barSpace)*7);
        barChart.groupBars(0f, groupSpace, barSpace);

        barChart.invalidate();
    }
}