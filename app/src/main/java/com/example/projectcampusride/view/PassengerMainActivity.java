package com.example.projectcampusride.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcampusride.R;

public class PassengerMainActivity extends AppCompatActivity {
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);

        userId = getIntent().getStringExtra("USER_ID");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.my_p_rides_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyRidesActivity.class);
            intent.putExtra("passenger_id", userId);
            startActivity(intent);
        });


        findViewById(R.id.search_ride_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchRideActivity.class);
            intent.putExtra("passenger_id", userId);
            startActivity(intent);
        });
    }
}
