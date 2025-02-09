package com.example.projectcampusride.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;


import com.example.projectcampusride.R;
import com.example.projectcampusride.SettingsActivity;
import com.example.projectcampusride.controllers.DriverController;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

import java.util.Calendar;

public class CreateRideActivity extends AppCompatActivity {
    private EditText availableSeatsField, priceField;
    private TextView selectedDate, selectedTime;
    private Button pickDateButton, pickTimeButton, createRideButton;
    private ProgressBar progressBar;
    private DriverController driverController;
    private String userId;
    private String rideDate = "", rideTime = "";
    private AutoCompleteTextView startLocation, endLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);

        driverController = new DriverController();
        userId = getIntent().getStringExtra("driver_id");

        startLocation = findViewById(R.id.start_location);
        endLocation = findViewById(R.id.end_location);
        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);

        setupAutoCompleteFields();

        availableSeatsField = findViewById(R.id.available_seats);
        priceField = findViewById(R.id.price);

        selectedDate = findViewById(R.id.selected_date);
        selectedTime = findViewById(R.id.selected_time);
        pickDateButton = findViewById(R.id.pick_date_button);
        pickTimeButton = findViewById(R.id.pick_time_button);
        createRideButton = findViewById(R.id.create_ride_button);
        progressBar = findViewById(R.id.progressBar);

        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        pickDateButton.setOnClickListener(v -> showDatePicker());
        pickTimeButton.setOnClickListener(v -> showTimePicker());

        createRideButton.setOnClickListener(v -> createRideA());
    }

    private void setupAutoCompleteFields() {
        String[] cities = getResources().getStringArray(R.array.israel_cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);

        startLocation.setAdapter(adapter);
        endLocation.setAdapter(adapter);

        startLocation.setThreshold(1);
        endLocation.setThreshold(1);

        startLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) startLocation.showDropDown();
        });

        endLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) endLocation.showDropDown();
        });
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            rideDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            selectedDate.setText("📅 תאריך: " + rideDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            rideTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            selectedTime.setText("⏰ שעה: " + rideTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void createRideA() {
        String startLocationStr = startLocation.getText().toString();
        String endLocationStr = endLocation.getText().toString();

        if (startLocationStr.isEmpty() || endLocationStr.isEmpty() || rideDate.isEmpty() || rideTime.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        int availableSeats;
        double price;
        try {
            availableSeats = Integer.parseInt(availableSeatsField.getText().toString().trim());
            price = Double.parseDouble(priceField.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "אנא הכנס ערכים תקינים למספר המקומות ולמחיר", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        createRideButton.setEnabled(false);

        driverController.createRide(userId,startLocationStr, endLocationStr, rideDate, rideTime, availableSeats, price, new DriverController.CreateRideCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                createRideButton.setEnabled(true);
                Toast.makeText(CreateRideActivity.this, "✅ נסיעה נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                createRideButton.setEnabled(true);
                Toast.makeText(CreateRideActivity.this, "❌ שגיאה בהעלאת נסיעה: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
