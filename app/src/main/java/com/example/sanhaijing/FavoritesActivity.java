package com.example.sanhaijing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView rvFavorites;
    private View emptyView;
    private DatabaseHelper dbHelper;
    private FavoritesAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        currentUserId = LoginActivity.getCurrentUserId(this);
        if (currentUserId <= 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadFavorites();
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        emptyView = findViewById(R.id.emptyView);
        
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadFavorites() {
        List<Story> favorites = dbHelper.getFavoriteStories(currentUserId);
        updateUI(favorites);
    }

    private void updateUI(List<Story> favorites) {
        if (favorites.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new FavoritesAdapter(favorites);
                adapter.setOnFavoriteClickListener(new FavoritesAdapter.OnFavoriteClickListener() {
                    @Override
                    public void onFavoriteClick(Story story) {
                        // Navigate to story detail
                        Intent intent = new Intent(FavoritesActivity.this, StoryDetailActivity.class);
                        intent.putExtra("story_id", story.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onUnfavoriteClick(Story story, int position) {
                        // Remove from favorites
                        dbHelper.removeFromFavorites(currentUserId, story.getId());
                        adapter.removeItem(position);
                        Toast.makeText(FavoritesActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        
                        // Check if list is now empty
                        if (adapter.getItemCount() == 0) {
                            rvFavorites.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                rvFavorites.setAdapter(adapter);
            } else {
                adapter.setFavorites(favorites);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites in case changes were made in detail view
        loadFavorites();
    }
}