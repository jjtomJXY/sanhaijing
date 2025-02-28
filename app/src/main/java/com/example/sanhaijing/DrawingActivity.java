package com.example.sanhaijing;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DrawingActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private SeekBar brushSizeBar;
    private LinearLayout colorPalette;
    private ImageButton btnClear;
    private ImageButton btnUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        initializeViews();
        setupColorPalette();
        setupBrushSize();
        setupButtons();
    }

    private void initializeViews() {
        drawingView = findViewById(R.id.drawingView);
        brushSizeBar = findViewById(R.id.brushSizeBar);
        colorPalette = findViewById(R.id.colorPalette);
        btnClear = findViewById(R.id.btnClear);
        btnUndo = findViewById(R.id.btnUndo);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void setupColorPalette() {
        // Define colors for the palette
        int[] colors = {
                R.color.red, R.color.blue, R.color.green,
                R.color.yellow, R.color.purple, R.color.orange,
                R.color.black
        };

        // Create color buttons
        for (int colorRes : colors) {
            View colorButton = new View(this);
            int size = getResources().getDimensionPixelSize(R.dimen.color_button_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 0, 8, 0);
            colorButton.setLayoutParams(params);

            colorButton.setBackgroundColor(ContextCompat.getColor(this, colorRes));
            colorButton.setOnClickListener(v ->
                    drawingView.setColor(ContextCompat.getColor(this, colorRes)));

            colorPalette.addView(colorButton);
        }
    }

    private void setupBrushSize() {
        brushSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setBrushSize(progress + 5); // Minimum brush size of 5
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupButtons() {
        btnClear.setOnClickListener(v -> drawingView.clearCanvas());
        btnUndo.setOnClickListener(v -> drawingView.undo());
    }
}