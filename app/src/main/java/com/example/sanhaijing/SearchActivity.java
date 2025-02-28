package com.example.sanhaijing;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageView ivClear;
    private RecyclerView recyclerView;
    private SearchListAdapter storyAdapter;
    private DatabaseHelper dbHelper;
    private List<Story> allStories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);
        allStories = dbHelper.getAllStories();

        initViews();
        setupSearchFunction();
    }

    private void initViews() {
        // Initialize views
        etSearch = findViewById(R.id.etSearch);
        ivClear = findViewById(R.id.ivClear);
        recyclerView = findViewById(R.id.recyclerViewSearch);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        storyAdapter = new SearchListAdapter(new ArrayList<>());
        recyclerView.setAdapter(storyAdapter);

        // Setup click listeners
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        
        ivClear.setOnClickListener(v -> {
            etSearch.setText("");
            ivClear.setVisibility(View.GONE);
        });

        // Setup story click listener
        storyAdapter.setOnStoryClickListener(story -> {
            // TODO: Navigate to story detail
            // Intent intent = new Intent(this, StoryDetailActivity.class);
            // intent.putExtra("story_id", story.getId());
            // startActivity(intent);
        });
    }

    private void setupSearchFunction() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();
                performSearch(searchText);
                ivClear.setVisibility(searchText.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String searchText) {
        List<Story> searchResults = new ArrayList<>();
        
        if (!searchText.isEmpty()) {
            for (Story story : allStories) {
                if (story.getName().toLowerCase().contains(searchText)) {
                    searchResults.add(story);
                }
            }
        }

        // Update RecyclerView with search results
        storyAdapter = new SearchListAdapter(searchResults);
        recyclerView.setAdapter(storyAdapter);
        
        // Restore click listener for new adapter
        storyAdapter.setOnStoryClickListener(story -> {
            // TODO: Navigate to story detail
            // Intent intent = new Intent(this, StoryDetailActivity.class);
            // intent.putExtra("story_id", story.getId());
            // startActivity(intent);
        });
    }
}