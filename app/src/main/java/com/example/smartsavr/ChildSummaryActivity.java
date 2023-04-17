package com.example.smartsavr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChildSummaryActivity extends AppCompatActivity {

    static List<Chore> listApprovedChores = new ArrayList<>();
    static List<Float> listEarnings = new ArrayList<>();
    static List<String> xAxisTopLabels = new ArrayList<>();
    static List<String> xAxisBottomLabels = new ArrayList<>();
    FirebaseFirestore db;
    CollectionReference chores;
    static String childId;

    final String TAG = "DB ERROR";
    TextView weekly_earnings;
    TextView monthly_earnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_summary);

        Intent intent = getIntent();
//        childId = intent.getExtras().getString("child");
        listApprovedChores.clear();

        db = FirebaseFirestore.getInstance();
        chores = db.collection("chores");

        //TODO change query back to correct conditions
        Query query = chores.whereEqualTo("childID","sam").whereEqualTo("complete",true).orderBy("completedTimestamp", Query.Direction.ASCENDING);
        Task<QuerySnapshot> querySnapshotTask = query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                for (QueryDocumentSnapshot document : snapshot) {
                    Chore chore = document.toObject(Chore.class);
                    listApprovedChores.add(chore);
                }
                calculateDailyEarnings();
                populateGraph();
                weekly_earnings = findViewById(R.id.weekly_amt);
                weekly_earnings.setText("Weekly Earnings: $" + Float.valueOf(calculateWeeklyEarnings()));
                monthly_earnings = findViewById(R.id.monthly_amt);
                monthly_earnings.setText("Monthly Earnings: $" + Float.valueOf(calculateMonthlyEarnings()));
            } else {
                Log.e(TAG, "Database error when loading documents");
            }
        });
    }

    public String convertTimestampToDate(long timestamp) {
        SimpleDateFormat date = new SimpleDateFormat("MM-dd", Locale.US);
        TimeZone timezone = TimeZone.getTimeZone("America/New_York");
        date.setTimeZone(timezone);
        return (date).format(timestamp);
    }

    public String convertTimestampToDayOfWeek(long timestamp) {
        SimpleDateFormat date = new SimpleDateFormat("EEEE", Locale.US);
        TimeZone timezone = TimeZone.getTimeZone("America/New_York");
        date.setTimeZone(timezone);
        return (date).format(timestamp);
    }

    public void calculateDailyEarnings() {
        int earning = 0;
        String compareToDate = convertTimestampToDate(listApprovedChores.get(0).getCompletedTimestamp());
        xAxisBottomLabels.add(compareToDate);
        xAxisTopLabels.add(convertTimestampToDayOfWeek(listApprovedChores.get(0).getCompletedTimestamp()));
        for (Chore chore : listApprovedChores) {
            String choreDate = convertTimestampToDate(chore.getCompletedTimestamp());
            if (choreDate.compareTo(compareToDate) == 0) {
                earning += chore.getRewardCents();
            } else if (choreDate.compareTo(compareToDate) > 0) {
                Log.e(TAG, "Reached Here: " + chore.getRewardCents());
                //add previous day's records to chart data
                listEarnings.add((float) earning);
                //set compareToDate to next date and reset earnings amt
                compareToDate = choreDate;
                earning = 0;
                earning += chore.getRewardCents();
                xAxisTopLabels.add(convertTimestampToDayOfWeek(chore.getCompletedTimestamp()));
                xAxisBottomLabels.add(convertTimestampToDate(chore.getCompletedTimestamp()));
            }
        }
        //add last day's records to chart data
        listEarnings.add((float) earning);
    }

    public void populateGraph() {
        LineChart lineChart = findViewById(R.id.lineChart);

        //populate Bar data entry arraylist
        ArrayList<Entry> values = new ArrayList<>();
        int i = 0;
        for (float earning : listEarnings) {
            values.add(new Entry(i, earning/100));
            i++;
        }

        // Add more labels to XAxis
        long lastDate = listApprovedChores.get(listApprovedChores.size()-1).getCompletedTimestamp();
        populateXAxisLabels(lastDate, values);

        //configure graph settings
        XAxis XAxisBottom = lineChart.getXAxis();
        XAxisBottom.setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxisBottom.setValueFormatter(new IndexAxisValueFormatter(xAxisBottomLabels));

        YAxis yAxis = lineChart.getAxisLeft();
//        yAxis.setGranularityEnabled(true);
//        yAxis.setGranularity(1.0f);
        YAxis yAxisRight = lineChart.getAxisRight();

        LineDataSet dataset = new LineDataSet(values,"Dataset");
        LineData data = new LineData(dataset);
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.setVisibleXRange(5,5);
        lineChart.moveViewToX(5);
        lineChart.setDrawGridBackground(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDoubleTapToZoomEnabled(false);
    }

    public void populateXAxisLabels(long lastDate, ArrayList<Entry> values) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastDate);
        int cal_day = cal.get(Calendar.DAY_OF_WEEK);
        long dateTS = lastDate;
        while (cal_day < 8) {
            dateTS += (1000 * 60 * 60 * 24);
            String date = convertTimestampToDate(dateTS);
            xAxisBottomLabels.add(date);
            values.add(new Entry(cal_day, Float.NaN));
            cal_day++;
        }


    }

    public float calculateWeeklyEarnings() {
        long ts = System.currentTimeMillis();
        float earnings = 0;
        for (Chore chore : listApprovedChores) {
            if (checkDaysOfSameWeek(ts, chore.getCompletedTimestamp())) {
                earnings += chore.getRewardCents();
            }
        }
        return earnings/100;
    }

    public float calculateMonthlyEarnings() {
        long ts = System.currentTimeMillis();
        float earnings = 0;
        for (Chore chore : listApprovedChores) {
            if (checkDaysOfSameMonth(ts, chore.getCompletedTimestamp())) {
                earnings += chore.getRewardCents();
            }
        }
        return earnings/100;
    }

    public boolean checkDaysOfSameWeek(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        int cal1_year = cal1.get(Calendar.YEAR);
        int cal1_week = cal1.get(Calendar.WEEK_OF_YEAR);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(timestamp2);
        int cal2_year = cal2.get(Calendar.YEAR);
        int cal2_week = cal2.get(Calendar.WEEK_OF_YEAR);

        return (cal1_year == cal2_year && cal1_week == cal2_week);
    }

    public boolean checkDaysOfSameMonth(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        int cal1_year = cal1.get(Calendar.YEAR);
        int cal1_month = cal1.get(Calendar.MONTH);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(timestamp2);
        int cal2_year = cal2.get(Calendar.YEAR);
        int cal2_month = cal2.get(Calendar.MONTH);

        return (cal1_year == cal2_year && cal1_month == cal2_month);
    }
}

//TODO: format graph to prettier layout