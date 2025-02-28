package com.example.sanhaijing;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;

public class ProfileFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView ivAvatar;
    private TextView tvUsername;
    private Button btnLogout;
    private DatabaseHelper dbHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        setupClickListeners(view);
        loadUserData();

        return view;
    }

    private void initializeViews(View view) {
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupClickListeners(View view) {
        // Setup function area click listeners
        view.findViewById(R.id.llFavorites).setOnClickListener(v -> handleFavorites());
        view.findViewById(R.id.llHistory).setOnClickListener(v -> handleHistory());
        view.findViewById(R.id.llReminders).setOnClickListener(v -> handleReminders());
        view.findViewById(R.id.llDrawing).setOnClickListener(v -> handleDrawing());
        view.findViewById(R.id.llKnowledge).setOnClickListener(v -> handleKnowledge());
        view.findViewById(R.id.llFeedback).setOnClickListener(v -> handleFeedback());

        // Setup logout button
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserData() {
        // Get current user ID
        int userId = LoginActivity.getCurrentUserId(requireContext());
        String userName = LoginActivity.getCurrentUserName(requireContext());

        // TODO: Load user data from database and set to views
        // For now, we'll just set a placeholder username
        tvUsername.setText(userName);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    LoginActivity.logout(requireContext());
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // Function area handlers
    private void handleFavorites() {
        startActivity(new Intent(getContext(), FavoritesActivity.class));
    }

    private void handleHistory() {
        Intent intent = new Intent(getContext(), HistoryActivity.class);
        startActivity(intent);
    }

    private void handleReminders() {
        if (checkCalendarPermission()) {
            showReminderDialog();
        } else {
            requestCalendarPermission();
        }
    }

    private boolean checkCalendarPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                PERMISSION_REQUEST_CODE);
    }

    private void showReminderDialog() {
        ReminderDialog dialog = new ReminderDialog(requireContext());
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showReminderDialog();
            } else {
                Toast.makeText(requireContext(), "需要日历权限来设置提醒", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleDrawing() {
        startActivity(new Intent(getContext(), DrawingActivity.class));
    }

    private void handleKnowledge() {
        startActivity(new Intent(getContext(), KnowledgeDetailActivity.class));
    }

    private void handleFeedback() {
        startActivity(new Intent(getContext(), FeedbackActivity.class));
    }
}