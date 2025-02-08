package com.example.projectcampusride.view;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcampusride.R;
import com.example.projectcampusride.controllers.RideController;
import com.example.projectcampusride.models.Ride;
import com.example.projectcampusride.models.RideStatus;
import com.example.projectcampusride.repositories.RideRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


public class CreateRideActivity extends AppCompatActivity {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);

        findViewById(R.id.submit_button).setOnClickListener(v -> {
            String startLocation = ((EditText) findViewById(R.id.start_location)).getText().toString();
            String endLocation = ((EditText) findViewById(R.id.end_location)).getText().toString();
            String date = ((EditText) findViewById(R.id.date_field)).getText().toString();
            String time = ((EditText) findViewById(R.id.time_field)).getText().toString();
            String priceString = ((EditText) findViewById(R.id.price_field)).getText().toString();  // Fixed the price EditText reference
            String availableSeatsString = ((EditText) findViewById(R.id.available_seats_field)).getText().toString(); // Fixed availableSeats field

            // Parse price and availableSeats if needed, handle possible empty inputs
            int price = priceString.isEmpty() ? 0 : Integer.parseInt(priceString);
            int availableSeats = availableSeatsString.isEmpty() ? 0 : Integer.parseInt(availableSeatsString);

            // Generate a unique ride ID (you can use other methods if needed)
            String rideId = UUID.randomUUID().toString();

            // Get the current user's ID (assuming you have a method to fetch it)
            String currentUserId = currentUser.getUid(); // This should return the current user's ID


            RideController controller = new RideController();
            Ride ride = new Ride(rideId,currentUserId, "",startLocation, endLocation,
                    date, time, availableSeats, price, RideStatus.ACTIVE);
            controller.createRide(ride);

            Toast.makeText(this, "נסיעה נוצרה בהצלחה!", Toast.LENGTH_SHORT).show();
        });
    }
}
