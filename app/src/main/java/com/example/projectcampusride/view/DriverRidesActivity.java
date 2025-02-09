package com.example.projectcampusride.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcampusride.R;
import com.example.projectcampusride.RideAdapter;
import com.example.projectcampusride.SettingsActivity;
import com.example.projectcampusride.controllers.DriverController;
import com.example.projectcampusride.models.Ride;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DriverRidesActivity extends AppCompatActivity {
    private ListView ridesListView;
    private DriverController driverController;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_rides);

        ridesListView = findViewById(R.id.rides_list_view);
        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        driverController = new DriverController();
        driverId = getIntent().getStringExtra("driver_id");

        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        notificationButton.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        loadDriverRides();
    }

    private void loadDriverRides() {
        driverController.getDriverRides(driverId, new DriverController.DriverRidesCallback() {
            @Override
            public void onSuccess(List<Ride> rides) {
                updateListView(rides);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DriverRidesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateListView(List<Ride> rides) {
        RideDriverAdapter adapter = new RideDriverAdapter(this, rides);
        ridesListView.setAdapter(adapter);
    }

}

