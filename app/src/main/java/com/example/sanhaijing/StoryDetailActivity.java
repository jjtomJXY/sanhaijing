package com.example.sanhaijing;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StoryDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Story story;
    private ImageView ivStoryImage;
    private TextView tvTitle;
    private TextView tvDescription;
    private MaterialButton btnFavorite;
    private int currentUserId;
    private boolean isFavorite = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    downloadImage();
                } else {
                    showPermissionDeniedDialog();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        // Get story ID from intent
        int storyId = getIntent().getIntExtra("story_id", -1);
        if (storyId == -1) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        currentUserId = LoginActivity.getCurrentUserId(this);
        story = dbHelper.getStoryById(storyId);

        if (currentUserId > 0) {
            // Record visit and history
            dbHelper.recordStoryVisit(currentUserId, storyId);
            dbHelper.addToHistory(currentUserId, storyId);
        }

        initViews();
        setupData();
        setupClickListeners();
    }

    private void initViews() {
        ivStoryImage = findViewById(R.id.ivStoryImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnFavorite = findViewById(R.id.btnFavorite);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void setupData() {
        // Load story image
        Glide.with(this)
                .load(story.getImage())
                .into(ivStoryImage);

        // Set text content
        tvTitle.setText(story.getName());
        tvDescription.setText(story.getDescribe());

        // Check and update favorite status
        if (currentUserId > 0) {
            isFavorite = dbHelper.isFavorite(currentUserId, story.getId());
            updateFavoriteButton();
        }
    }

    private void setupClickListeners() {
        // Favorite button
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Download image button
        findViewById(R.id.btnDownload).setOnClickListener(v -> {
            checkAndRequestPermissions();
        });

        // Share button
        findViewById(R.id.btnShare).setOnClickListener(v -> {
            shareStory();
        });
    }

    private void toggleFavorite() {
        if (currentUserId <= 0) {
            // User not logged in
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            dbHelper.removeFromFavorites(currentUserId, story.getId());
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addToFavorites(currentUserId, story.getId());
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        }

        isFavorite = !isFavorite;
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setText("已收藏");
            btnFavorite.setIcon(getDrawable(R.drawable.ic_favorite_filled));
            btnFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.favorite_active)));
        } else {
            btnFavorite.setText("收藏");
            btnFavorite.setIcon(getDrawable(R.drawable.ic_favorite));
            btnFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.favorite_inactive)));
        }
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, we don't need storage permission
            downloadImage();
        } else {
            // For Android 9 and below, check storage permission
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionExplanationDialog();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("需要权限")
                .setMessage("下载图片需要存储权限，请授予权限。")
                .setPositiveButton("授予", (dialog, which) -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("权限被拒绝")
                .setMessage("下载图片需要存储权限。请在应用设置中启用此权限。")
                .setPositiveButton("设置", (dialog, which) -> {
                    openAppSettings();
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void downloadImage() {
        // Show progress
        Toast.makeText(this, "正在下载图片...", Toast.LENGTH_SHORT).show();

        // Download image using Glide
        Glide.with(this)
                .asBitmap()
                .load(story.getImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        saveImage(bitmap);
                    }
                });
    }

    private void saveImage(Bitmap bitmap) {
        String fileName = "山海经_" + story.getName() + "_" + System.currentTimeMillis() + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/山海经");

            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (imageUri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(imageUri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "山海经");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);

            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                // Notify gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)));
                Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareStory() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, story.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                story.getName() + "\n\n" + story.getDescribe());
        startActivity(Intent.createChooser(shareIntent, "分享故事"));
    }
}