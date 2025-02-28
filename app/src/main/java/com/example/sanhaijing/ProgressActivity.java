package com.example.sanhaijing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgressActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private LineChart lineChart;
    private RecyclerView recyclerView;
    private TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        dbHelper = new DatabaseHelper(this);
        
        // Initialize views
        lineChart = findViewById(R.id.lineChart);
        recyclerView = findViewById(R.id.recyclerView);
        tvNoData = findViewById(R.id.tvNoData);
        
        // Setup back button
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // Get current user ID
        int userId = LoginActivity.getCurrentUserId(this);
        if (userId != -1) {
            setupLineChart(userId);
            setupRankingList(userId);
        }
    }

    private void setupLineChart(int userId) {
        List<WeeklyVisitStat> weeklyStats = dbHelper.getWeeklyVisitStats(userId);
        
        if (weeklyStats.isEmpty()) {
            lineChart.setVisibility(View.GONE);
            return;
        }

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

        for (int i = 0; i < weeklyStats.size(); i++) {
            WeeklyVisitStat stat = weeklyStats.get(i);
            entries.add(new Entry(i, stat.getVisitCount()));
            try {
                String formattedDate = outputFormat.format(inputFormat.parse(stat.getDate()));
                labels.add(formattedDate);
            } catch (Exception e) {
                labels.add(stat.getDate());
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "阅读次数");
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimaryLight));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelRotationAngle(45);
        xAxis.setGranularity(1f);

        // Customize chart
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private void setupRankingList(int userId) {
        List<StoryVisitStat> ranking = dbHelper.getStoryVisitRanking(userId);
        
        if (ranking.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VisitRankingAdapter adapter = new VisitRankingAdapter(ranking);
        recyclerView.setAdapter(adapter);

        // Set click listener for items
        adapter.setOnItemClickListener(storyId -> {
            Intent intent = new Intent(this, StoryDetailActivity.class);
            intent.putExtra("story_id", storyId);
            startActivity(intent);
        });
    }
}