package com.example.projectcampusride.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcampusride.view.CreateRideActivity;
import com.example.projectcampusride.view.MyRidesActivity;
import com.example.projectcampusride.R;


public class DriverMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        String driverId = "12345";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.my_rides_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyRidesActivity.class);
            intent.putExtra("driver_id", driverId);
            startActivity(intent);
        });


        findViewById(R.id.create_ride_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateRideActivity.class);
            startActivity(intent);
        });
    }


}
