package com.example.sanhaijing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class DateScrollPicker extends NumberPicker {
    private Formatter formatter;

    public DateScrollPicker(Context context) {
        super(context);
        init();
    }

    public DateScrollPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateScrollPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWrapSelectorWheel(false); // Prevent wrapping around
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
        super.setFormatter(formatter);
    }
}