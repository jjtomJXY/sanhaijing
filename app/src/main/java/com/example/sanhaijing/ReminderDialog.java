package com.example.sanhaijing;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Locale;

public class ReminderDialog extends Dialog {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker datePicker;
    private Context context;
    private Calendar currentDate;

    public ReminderDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_reminder);
        currentDate = Calendar.getInstance();

        initializePickers();
        setupButtons();
    }

    private void initializePickers() {
        datePicker = findViewById(R.id.datePicker);
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);

        setupDatePicker();
        setupHourPicker();
        setupMinutePicker();
    }

    private void setupDatePicker() {
        // Set up the date values for 90 days ahead
        final int DAYS_AHEAD = 90;
        final String[] dateValues = new String[DAYS_AHEAD];

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < DAYS_AHEAD; i++) {
            dateValues[i] = String.format(Locale.CHINA, "%d月%d日",
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        datePicker.setMinValue(0);
        datePicker.setMaxValue(dateValues.length - 1);
        datePicker.setDisplayedValues(dateValues);
        datePicker.setValue(0); // Start at today
        datePicker.setWrapSelectorWheel(false);
    }

    private void setupHourPicker() {
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(currentDate.get(Calendar.HOUR_OF_DAY));
        hourPicker.setFormatter(value -> String.format(Locale.CHINA, "%02d", value));
        hourPicker.setWrapSelectorWheel(true);
    }

    private void setupMinutePicker() {
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(currentDate.get(Calendar.MINUTE));
        minutePicker.setFormatter(value -> String.format(Locale.CHINA, "%02d", value));
        minutePicker.setWrapSelectorWheel(true);
    }

    private void setupButtons() {
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(v -> {
            if (checkCalendarPermission()) {
                createCalendarEvent();
            } else {
                requestCalendarPermission();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private boolean checkCalendarPermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermission() {
        ActivityCompat.requestPermissions((android.app.Activity) context,
                new String[]{Manifest.permission.WRITE_CALENDAR},
                PERMISSION_REQUEST_CODE);
    }

    private void createCalendarEvent() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, datePicker.getValue());
        calendar.set(Calendar.HOUR_OF_DAY, hourPicker.getValue());
        calendar.set(Calendar.MINUTE, minutePicker.getValue());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis() + 3600000)
                .putExtra(CalendarContract.Events.TITLE, "山海经学习提醒")
                .putExtra(CalendarContract.Events.DESCRIPTION, "山海经应用提醒事项")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        context.startActivity(intent);
        dismiss();
        Toast.makeText(context, "提醒已设置", Toast.LENGTH_SHORT).show();
    }
}