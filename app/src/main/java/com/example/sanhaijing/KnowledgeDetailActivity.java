package com.example.sanhaijing;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class KnowledgeDetailActivity extends AppCompatActivity {
    private ImageView ivKnowledgeImage;
    private TextView tvTitle;
    private TextView tvDescription;
    private ImageView btnAudio;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_detail);

        // Get knowledge data from intent
        int knowledgeId = getIntent().getIntExtra("knowledge_id", -1);
//        if (knowledgeId == -1) {
//            finish();
//            return;
//        }

        initViews();
        setupAudioPlayer();
        loadKnowledgeData(knowledgeId);
    }

    private void initViews() {
        ivKnowledgeImage = findViewById(R.id.ivKnowledgeImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnAudio = findViewById(R.id.btnAudio);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        btnAudio.setOnClickListener(v -> toggleAudio());
    }

    private void setupAudioPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.phoenix_explanation);
        mediaPlayer.setLooping(false);

        // Set completion listener
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            updateAudioButton();
        });
        toggleAudio();
    }

    private void loadKnowledgeData(int knowledgeId) {
        // For now, we'll use the hardcoded data
        Knowledge knowledge = new Knowledge(
                6,
                "凤凰",
                "https://picx.zhimg.com/v2-888beec10cffa801064e57dec82b071d_1440w.jpg",
                "《山海经·南山经》记载，丹穴之山，有鸟焉，其状如鸡，五采而文，名曰凤皇，首文曰德，翼文曰义，背文曰礼，膺文曰仁，腹文曰信。是鸟也，饮食自然，自歌自舞，见则天下安宁。"
        );

        // Load image using Glide
        Glide.with(this)
                .load(knowledge.getImage())
                .into(ivKnowledgeImage);

        // Set text content
        tvTitle.setText(knowledge.getName());
        tvDescription.setText(knowledge.getDescribe());
    }

    private void toggleAudio() {
        if (isPlaying) {
            pauseAudio();
        } else {
            playAudio();
        }
    }

    private void playAudio() {
        mediaPlayer.start();
        isPlaying = true;
        updateAudioButton();
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        updateAudioButton();
    }

    private void updateAudioButton() {
        btnAudio.setImageResource(isPlaying ?
                R.drawable.ic_audio_pause : R.drawable.ic_audio_play);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) {
            pauseAudio();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}