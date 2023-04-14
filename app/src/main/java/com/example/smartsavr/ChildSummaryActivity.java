package com.example.smartsavr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;

public class ChildSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_summary);

        LineChart lineChart = findViewById(R.id.lineChart);

        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(0f, 3.5f));
        values.add(new Entry(1f, 1f));
        values.add(new Entry(2f, 5f));
        values.add(new Entry(3f, 2.5f));
        values.add(new Entry(4f, 3f));
        values.add(new Entry(5f, Float.NaN));
        values.add(new Entry(6f, Float.NaN));


        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Sun");
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tues");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thurs");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelCount(xAxisLabel.size(), true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(1.0f);
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setGranularityEnabled(true);
        yAxisRight.setGranularity(1.0f);

        LineDataSet dataset = new LineDataSet(values,"Dataset");
        LineData data = new LineData(dataset);
        lineChart.setData(data);
    }
}


