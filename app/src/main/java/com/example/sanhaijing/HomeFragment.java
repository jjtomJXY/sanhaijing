package com.example.sanhaijing;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private BannerAdapter bannerAdapter;
    private DatabaseHelper dbHelper;
    private Handler handler = new Handler();
    private Timer timer;
    private Dialog storyPreviewDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());

        // Initialize ViewPager
        viewPager = view.findViewById(R.id.viewPager);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        view.findViewById(R.id.llSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        // Load initial data
        loadInitialData();

        // Load stories and setup banner
        setupBannerAndStories();

        // Set click listeners for function areas
        setupFunctionAreas(view);

        return view;
    }

    private void loadInitialData() {
        try {
            // Check if stories table is empty
            if (dbHelper.isStoriesTableEmpty()) {
                // Load JSON from assets
                String jsonString = loadJSONFromAsset();
                if (jsonString != null) {
                    dbHelper.insertStoryData(jsonString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open("content.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setupBannerAndStories() {
        List<Story> allStories = dbHelper.getAllStories();

        // Setup banner with first 5 stories
        List<Story> bannerStories = allStories.subList(0, Math.min(4, allStories.size()));
        bannerAdapter = new BannerAdapter(bannerStories);

        // Set up ViewPager2
        viewPager.setOffscreenPageLimit(3);
        float nextItemVisiblePx = getResources().getDimensionPixelSize(R.dimen.viewpager_next_item_visible);
        float currentItemHorizontalMarginPx = getResources().getDimensionPixelSize(R.dimen.viewpager_current_item_horizontal_margin);
        float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;

        ViewPager2.PageTransformer pageTransformer = (page, position) -> {
            page.setTranslationX(-pageTranslationX * position);

            // Scale the page down
            float scaleFactor = 0.8f;
            float scale = scaleFactor + (1 - scaleFactor) * (1 - Math.abs(position));
            page.setScaleY(scale);
            page.setScaleX(scale);

            // Fade the page
//            page.setAlpha(0.5f + (1 - Math.abs(position)));
        };

        viewPager.setPageTransformer(pageTransformer);
        viewPager.setAdapter(bannerAdapter);
        viewPager.setCurrentItem(2);

        // Setup auto-scrolling for banner
        setupAutoScroll();

        List<Story> listStories = allStories.subList(0, Math.min(20, allStories.size()));
        // Setup story list
        storyAdapter = new StoryAdapter(listStories);
        recyclerView.setAdapter(storyAdapter);

        // Setup story click listener
        storyAdapter.setOnStoryClickListener(story -> {
            Intent intent = new Intent(getContext(), StoryDetailActivity.class);
            intent.putExtra("story_id", story.getId());
            startActivity(intent);
        });
    }

    private void setupAutoScroll() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    int currentItem = viewPager.getCurrentItem();
                    int totalItems = viewPager.getAdapter().getItemCount();
                    int nextItem = (currentItem + 1) % totalItems;
                    viewPager.setCurrentItem(nextItem, true);
                });
            }
        }, 3000, 3000); // Change banner every 3 seconds
    }

    private void setupFunctionAreas(View view) {
        view.findViewById(R.id.llTodayRecommend).setOnClickListener(v -> {
            showRandomStoryPreview();
        });

        view.findViewById(R.id.llStoryList).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StoryListActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.llProgress).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProgressActivity.class);
            startActivity(intent);
        });
    }

    private void showRandomStoryPreview() {
        // Get all stories and select a random one
        List<Story> allStories = dbHelper.getAllStories();
        if (allStories.isEmpty()) {
            return;
        }

        int randomIndex = (int) (Math.random() * allStories.size());
        Story randomStory = allStories.get(randomIndex);

        // Create and show the dialog
        storyPreviewDialog = new Dialog(requireContext());
        storyPreviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        storyPreviewDialog.setContentView(R.layout.dialog_story_preview);

        // Set dialog width to match parent with margins
        Window window = storyPreviewDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize views
        ImageView ivStoryImage = storyPreviewDialog.findViewById(R.id.ivStoryImage);
        TextView tvTitle = storyPreviewDialog.findViewById(R.id.tvTitle);
        TextView tvDescription = storyPreviewDialog.findViewById(R.id.tvDescription);

        // Set story data
        Glide.with(this)
                .load(randomStory.getImage())
                .into(ivStoryImage);
        tvTitle.setText(randomStory.getName());
        tvDescription.setText(randomStory.getDescribe());

        // Set click listeners
        storyPreviewDialog.findViewById(R.id.ivClose).setOnClickListener(v ->
                storyPreviewDialog.dismiss());

        storyPreviewDialog.findViewById(R.id.btnReadMore).setOnClickListener(v -> {
            // Navigate to story detail activity
            Intent intent = new Intent(getContext(), StoryDetailActivity.class);
            intent.putExtra("story_id", randomStory.getId());
            startActivity(intent);
            storyPreviewDialog.dismiss();
        });

        storyPreviewDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (storyPreviewDialog != null && storyPreviewDialog.isShowing()) {
            storyPreviewDialog.dismiss();
        }
    }
}