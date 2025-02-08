package com.example.projectcampusride.view;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcampusride.RideAdapter;
import com.example.projectcampusride.models.Ride;
import com.example.projectcampusride.R;
import com.example.projectcampusride.repositories.RideRepository;

import java.util.List;

public class MyRidesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        // קבלת driverId מה-Intent
        String driverId = getIntent().getStringExtra("driver_id");
        if (driverId == null) {
            driverId = ""; // טיפול במקרה שהמזהה לא הועבר
        }

        RecyclerView recyclerView = findViewById(R.id.rides_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // שימוש ב-RideManager להציג רשימת נסיעות
        RideRepository rideManager = new RideRepository(); // או למשוך ממקור אחר
        rideManager.getRidesForDriver(driverId, new RideRepository.OnRidesFetchedListener() {
            @Override
            public void onSuccess(List<Ride> rides) {
                // Use the retrieved rides
                for (Ride ride : rides) {
                    Log.d("RideInfo", "Ride ID: " + ride.getRideId());
                }
//                RideAdapter adapter = new RideAdapter(MyRidesActivity.this, rides);
//                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RideError", "Failed to fetch rides", e);
            }
        });



    }
}

