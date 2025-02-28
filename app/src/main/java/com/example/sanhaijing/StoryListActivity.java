package com.example.sanhaijing;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StoryListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StoryListAdapter storyAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("故事列表");
        }

        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewStories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load and display stories
        loadStories();
    }

    private void loadStories() {
        // Get all stories from database
        List<Story> allStories = dbHelper.getAllStories();

        // Setup story adapter
        storyAdapter = new StoryListAdapter(allStories);
        recyclerView.setAdapter(storyAdapter);

        // Setup story click listener
        storyAdapter.setOnStoryClickListener(story -> {
            // TODO: Navigate to story detail activity
            // Intent intent = new Intent(this, StoryDetailActivity.class);
            // intent.putExtra("story_id", story.getId());
            // startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}