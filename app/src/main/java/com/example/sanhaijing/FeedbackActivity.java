package com.example.sanhaijing;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class FeedbackActivity extends AppCompatActivity {
    private EditText etFeedback;
    private EditText etEmail;
    private MaterialButton btnSubmit;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        dbHelper = new DatabaseHelper(this);
        currentUserId = LoginActivity.getCurrentUserId(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etFeedback = findViewById(R.id.etFeedback);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set current user's email if logged in
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.ivHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackListActivity.class));
        });
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedback = etFeedback.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(feedback)) {
            etFeedback.setError("请输入反馈内容");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("请输入邮箱");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("请输入有效的邮箱地址");
            return;
        }

        // Submit feedback
        long result = dbHelper.submitFeedback(currentUserId, feedback, email);
        
        if (result != -1) {
            Toast.makeText(this, "感谢您的反馈！", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}