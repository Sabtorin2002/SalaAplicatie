package com.example.salaaplicatie.profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.salaaplicatie.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Grafice {
    private Context context;
    private LinearLayout layout;
    private BarChart barChartWeek, barChartMonth, barChartYear;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private String uid;
    private Spinner chartSelector;
    private String chartType;
    private Handler mainHandler;
    private ExecutorService executorService;

    public Grafice(Context context, LinearLayout layout, Spinner chartSelector) {
        this(context, layout, chartSelector, "duration");
    }

    public Grafice(Context context, LinearLayout layout, Spinner chartSelector, String chartType) {
        this.context = context;
        this.layout = layout;
        this.mAuth = FirebaseAuth.getInstance();
        this.chartSelector = chartSelector;
        this.chartType = chartType;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newSingleThreadExecutor();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            this.uid = currentUser.getUid();
        }
        collectionReference = FirebaseFirestore.getInstance().collection("workouts");

        barChartWeek = new BarChart(context);
        barChartMonth = new BarChart(context);
        barChartYear = new BarChart(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        barChartWeek.setLayoutParams(params);
        barChartMonth.setLayoutParams(params);
        barChartYear.setLayoutParams(params);

        setupChart(barChartWeek, "Week");
        setupChart(barChartMonth, "Month");
        setupChart(barChartYear, "Year");

        layout.addView(barChartWeek);
        layout.addView(barChartMonth);
        layout.addView(barChartYear);

        setupSpinner();
    }

    private void setupChart(BarChart chart, String description) {
        chart.getDescription().setText(description);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(ContextCompat.getColor(context,R.color.davysgray));

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);

        chart.getLegend().setEnabled(false);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.chart_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chartSelector.setAdapter(adapter);
        chartSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        barChartWeek.setVisibility(View.VISIBLE);
                        barChartMonth.setVisibility(View.GONE);
                        barChartYear.setVisibility(View.GONE);
                        fetchDataAndDisplayCharts("week");
                        break;
                    case 1:
                        barChartWeek.setVisibility(View.GONE);
                        barChartMonth.setVisibility(View.VISIBLE);
                        barChartYear.setVisibility(View.GONE);
                        fetchDataAndDisplayCharts("month");
                        break;
                    case 2:
                        barChartWeek.setVisibility(View.GONE);
                        barChartMonth.setVisibility(View.GONE);
                        barChartYear.setVisibility(View.VISIBLE);
                        fetchDataAndDisplayCharts("year");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchDataAndDisplayCharts(String period) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        Date startDate;

        switch (period) {
            case "week":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = calendar.getTime();
                break;
            case "month":
                calendar.add(Calendar.MONTH, -1);
                startDate = calendar.getTime();
                break;
            case "year":
                calendar.add(Calendar.YEAR, -1);
                startDate = calendar.getTime();
                break;
            default:
                startDate = endDate;
        }

        Timestamp startTimestamp = new Timestamp(startDate);
        Timestamp endTimestamp = new Timestamp(endDate);

        Log.d("Grafice", "Fetching data from " + startTimestamp.toDate().toString() + " to " + endTimestamp.toDate().toString());

        Query query = collectionReference.whereEqualTo("userId", uid)
                .whereGreaterThan("timestamp", startTimestamp)
                .whereLessThan("timestamp", endTimestamp);

        query.get().addOnCompleteListener(executorService, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Workout> workouts = new ArrayList<>();
                    QuerySnapshot snapshots = task.getResult();
                    if (snapshots != null) {
                        Log.d("Grafice", "Total documents fetched: " + snapshots.size());
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                Log.d("Grafice", "Document data: " + map.toString());
                                Workout workout = new Workout(map);
                                workouts.add(workout);
                                Log.d("Grafice", "Workout added: " + workout.toString());
                            }
                        }
                    }

                    if (workouts.isEmpty()) {
                        Log.d("Grafice", "No workouts found for the given period.");
                    } else {
                        Log.d("Grafice", "Workouts found: " + workouts.size());
                    }

                    processAndDisplayCharts(workouts, period);
                } else {
                    Log.e("Grafice", "Error fetching data: ", task.getException());
                }
            }
        });
    }

    private void processAndDisplayCharts(List<Workout> workouts, String period) {
        List<BarEntry> entries = new ArrayList<>();

        int index = 0;
        for (Workout workout : workouts) {
            float value = chartType.equals("volume") ? workout.getVolume() : workout.getDuration();
            entries.add(new BarEntry(index, value));
            index++;
        }

        if (entries.isEmpty()) {
            Log.d("Grafice", "No data entries available for the chart.");
        } else {
            Log.d("Grafice", "Data entries: " + entries.size());
        }

        BarDataSet dataSet = new BarDataSet(entries, chartType.equals("volume") ? "Volume (" + period + ")" : "Duration (" + period + ")");
        dataSet.setColors(ContextCompat.getColor(context,R.color.brandeisblue));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.45f);


        mainHandler.post(() -> {
            switch (period) {
                case "week":
                    barChartWeek.setData(data);
                    barChartWeek.invalidate();
                    break;
                case "month":
                    barChartMonth.setData(data);
                    barChartMonth.invalidate();
                    break;
                case "year":
                    barChartYear.setData(data);
                    barChartYear.invalidate();
                    break;
            }
        });
    }

    public void updateChartType(String chartType) {
        this.chartType = chartType;
        String period = getSelectedPeriod();
        fetchDataAndDisplayCharts(period);
    }

    private String getSelectedPeriod() {
        int position = chartSelector.getSelectedItemPosition();
        switch (position) {
            case 0:
                return "week";
            case 1:
                return "month";
            case 2:
                return "year";
            default:
                return "week";
        }
    }
}
