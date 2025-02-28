package com.example.sanhaijing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private Path currentPath;
    private Paint drawPaint;
    private Paint canvasPaint;
    private int currentColor;
    private List<DrawPath> paths;
    private float currentX, currentY;
    
    private static class DrawPath {
        Path path;
        Paint paint;
        
        DrawPath(Path path, Paint paint) {
            this.path = new Path(path);
            this.paint = new Paint(paint);
        }
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        paths = new ArrayList<>();
        currentPath = new Path();
        drawPaint = new Paint();
        
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        currentColor = Color.BLACK;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (DrawPath dp : paths) {
            canvas.drawPath(dp.path, dp.paint);
        }
        canvas.drawPath(currentPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                currentX = x;
                currentY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2);
                currentX = x;
                currentY = y;
                break;
            case MotionEvent.ACTION_UP:
                currentPath.lineTo(currentX, currentY);
                paths.add(new DrawPath(currentPath, drawPaint));
                currentPath = new Path();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(int color) {
        currentColor = color;
        drawPaint.setColor(color);
    }

    public void setBrushSize(float size) {
        drawPaint.setStrokeWidth(size);
    }

    public void clearCanvas() {
        paths.clear();
        currentPath = new Path();
        invalidate();
    }

    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }
}