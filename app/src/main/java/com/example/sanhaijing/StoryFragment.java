package com.example.sanhaijing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StoryFragment extends Fragment {
    private RecyclerView rvCategories;
    private RecyclerView rvStories;
    private CategoryAdapter categoryAdapter;
    private StoryAdapter storyAdapter;
    private DatabaseHelper dbHelper;
    private List<Story> allStories;

    public StoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        dbHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        loadData();

        return view;
    }

    private void initializeViews(View view) {
        // Initialize RecyclerViews
        rvCategories = view.findViewById(R.id.rvCategories);
        rvStories = view.findViewById(R.id.rvStories);

        // Set up category RecyclerView
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up stories RecyclerView
        rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadData() {
        // Load all stories
        allStories = dbHelper.getAllStories();

        // Get unique categories
        Set<String> categorySet = new HashSet<>();
        for (Story story : allStories) {
            categorySet.add(story.getType());
        }
        List<String> categories = new ArrayList<>(categorySet);

        // Set up category adapter
        categoryAdapter = new CategoryAdapter(categories);
        rvCategories.setAdapter(categoryAdapter);

        // Set up story adapter with initial category
        if (!categories.isEmpty()) {
            filterStoriesByCategory(categories.get(0));
        }

        // Set up category click listener
        categoryAdapter.setOnCategoryClickListener((category, position) -> {
            categoryAdapter.setSelectedPosition(position);
            filterStoriesByCategory(category);
        });
    }

    private void filterStoriesByCategory(String category) {
        List<Story> filteredStories = allStories.stream()
                .filter(story -> story.getType().equals(category))
                .collect(Collectors.toList());

        if (storyAdapter == null) {
            storyAdapter = new StoryAdapter(filteredStories);
            storyAdapter.setOnStoryClickListener(story -> {
                Intent intent = new Intent(getContext(), StoryDetailActivity.class);
                intent.putExtra("story_id", story.getId());
                startActivity(intent);
            });
            rvStories.setAdapter(storyAdapter);
        } else {
            storyAdapter = new StoryAdapter(filteredStories);
            storyAdapter.setOnStoryClickListener(story -> {
                Intent intent = new Intent(getContext(), StoryDetailActivity.class);
                intent.putExtra("story_id", story.getId());
                startActivity(intent);
            });
            rvStories.setAdapter(storyAdapter);
        }
    }
}