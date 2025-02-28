package com.example.sanhaijing;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private FeedbackAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        dbHelper = new DatabaseHelper(this);
        currentUserId = LoginActivity.getCurrentUserId(this);

        initViews();
        loadFeedbackList();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeedbackList();
    }

    private void loadFeedbackList() {
        List<Feedback> feedbackList = dbHelper.getFeedbackList(currentUserId);
        
        if (feedbackList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            adapter = new FeedbackAdapter(feedbackList);
            recyclerView.setAdapter(adapter);
        }
    }
}