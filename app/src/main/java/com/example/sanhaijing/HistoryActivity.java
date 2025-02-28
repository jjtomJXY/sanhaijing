package com.example.sanhaijing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private View emptyView;
    private View normalModeToolbar;
    private View selectionModeToolbar;
    private TextView tvSelectedCount;
    private TextView tvSelectAll;
    private TextView tvDelete;
    private DatabaseHelper dbHelper;
    private HistoryAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        currentUserId = LoginActivity.getCurrentUserId(this);
        if (currentUserId <= 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadHistory();
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        emptyView = findViewById(R.id.emptyView);
        normalModeToolbar = findViewById(R.id.normalModeToolbar);
        selectionModeToolbar = findViewById(R.id.selectionModeToolbar);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvSelectAll = findViewById(R.id.tvSelectAll);
        tvDelete = findViewById(R.id.tvDelete);
        
        // Set up RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // Back button
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // Edit button to enter selection mode
        findViewById(R.id.ivEdit).setOnClickListener(v -> {
            if (adapter != null) {
                enterSelectionMode();
            }
        });

        // Close selection mode
        findViewById(R.id.ivClose).setOnClickListener(v -> exitSelectionMode());

        // Select all button
        tvSelectAll.setOnClickListener(v -> {
            if (adapter != null) {
                boolean selectAll = tvSelectAll.getText().toString().equals("全选");
                adapter.toggleSelectAll(selectAll);
                tvSelectAll.setText(selectAll ? "取消全选" : "全选");
            }
        });

        // Delete button
        tvDelete.setOnClickListener(v -> {
            if (adapter != null) {
                List<Story> selectedStories = adapter.getSelectedStories();
                if (!selectedStories.isEmpty()) {
                    showDeleteConfirmationDialog(selectedStories);
                }
            }
        });
    }

    private void loadHistory() {
        List<Story> histories = dbHelper.getHistoryStories(currentUserId);
        updateUI(histories);
    }

    private void updateUI(List<Story> histories) {
        if (histories.isEmpty()) {
            rvHistory.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new HistoryAdapter(histories);
                adapter.setOnHistoryClickListener(new HistoryAdapter.OnHistoryClickListener() {
                    @Override
                    public void onHistoryClick(Story story) {
                        if (!adapter.isSelectionMode()) {
                            Intent intent = new Intent(HistoryActivity.this, StoryDetailActivity.class);
                            intent.putExtra("story_id", story.getId());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onSelectionChanged(int count) {
                        updateSelectionCount(count);
                    }
                });
                rvHistory.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void enterSelectionMode() {
        adapter.setSelectionMode(true);
        normalModeToolbar.setVisibility(View.GONE);
        selectionModeToolbar.setVisibility(View.VISIBLE);
        updateSelectionCount(0);
    }

    private void exitSelectionMode() {
        adapter.setSelectionMode(false);
        normalModeToolbar.setVisibility(View.VISIBLE);
        selectionModeToolbar.setVisibility(View.GONE);
        tvSelectAll.setText("全选");
    }

    private void updateSelectionCount(int count) {
        tvSelectedCount.setText(String.format("已选择 %d 项", count));
        tvDelete.setEnabled(count > 0);
        tvDelete.setAlpha(count > 0 ? 1.0f : 0.5f);
    }

    private void showDeleteConfirmationDialog(List<Story> selectedStories) {
        new AlertDialog.Builder(this)
                .setTitle("删除历史记录")
                .setMessage("确定要删除选中的" + selectedStories.size() + "条历史记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    deleteSelectedItems(selectedStories);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteSelectedItems(List<Story> selectedStories) {
        for (Story story : selectedStories) {
            dbHelper.clearHistory(currentUserId);
        }
        adapter.removeItems(selectedStories);
        exitSelectionMode();
        
        if (adapter.getItemCount() == 0) {
            rvHistory.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload history in case changes were made
        loadHistory();
    }
}