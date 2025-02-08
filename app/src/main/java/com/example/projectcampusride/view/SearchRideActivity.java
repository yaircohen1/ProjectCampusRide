package com.example.projectcampusride.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcampusride.R;
import com.example.projectcampusride.RideAdapter;
import com.example.projectcampusride.SettingsActivity;
import com.example.projectcampusride.controllers.PassengerController;
import com.example.projectcampusride.models.Ride;
import com.example.projectcampusride.models.User;
import com.example.projectcampusride.models.UserRole;

import java.util.List;

public class SearchRideActivity extends AppCompatActivity {
    private static final String TAG = "SearchRideActivity";

    private EditText startLocationEdit;
    private EditText endLocationEdit;
    private ListView ridesListView;
    private ProgressBar progressBar;
    private PassengerController passengerController;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.activity_search_ride);

        passengerController = new PassengerController();

        startLocationEdit = findViewById(R.id.start_location_edit);
        endLocationEdit = findViewById(R.id.end_location_edit);
        ridesListView = findViewById(R.id.rides_list_view);
        progressBar = findViewById(R.id.progress_bar);
        Button searchButton = findViewById(R.id.search_button);
        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);

        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        searchButton.setOnClickListener(v -> searchRides());
    }

    private void searchRides() {
        showProgress(true);
        passengerController.searchRides(
                startLocationEdit.getText().toString().trim(),
                endLocationEdit.getText().toString().trim(),
                new PassengerController.SearchCallback() {
                    @Override
                    public void onSuccess(List<Ride> rides) {
                        showProgress(false);
                        updateRidesList(rides);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        showProgress(false);
                        showToast(errorMessage);
                    }
                });
    }

    private void updateRidesList(List<Ride> rideList) {
        RideAdapter adapter = new RideAdapter(this, rideList);
        adapter.setOnJoinClickListener(this::handleJoinRequest);
        ridesListView.setAdapter(adapter);

        if (rideList.isEmpty()) {
            showToast("No rides found");
        }
    }

    private void handleJoinRequest(Ride ride) {
        // יצירת אובייקט משתמש (החלף בערכים אמיתיים)
        User passenger = new User(
                "USER_ID",  // מזהה המשתמש
                "User Name", // שם
                "1234567890", // טלפון
                "user@example.com", // אימייל
                false,  // לא נהג
                5.0,  // דירוג
                UserRole.PASSENGER
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Ride")
                .setMessage("Would you like to join this ride?\nFrom: " + ride.getStartLocation() +
                        "\nTo: " + ride.getEndLocation() + "\nPrice: ₪" + ride.getPrice())
                .setPositiveButton("Join", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Sending request...");
                    progressDialog.show();

                    passengerController.requestToJoinRide(ride.getRideId(), passenger, ride.getDriverId(),ride.getDriverName(),ride.getDate(), ride.getTime(), ride.getStartLocation(),ride.getEndLocation())
                            .addOnSuccessListener(requestId -> {
                                progressDialog.dismiss();
                                showToast("Successfully sent request! ID: " + requestId);
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                if (e.getMessage().contains("Request already exists")) {
                                    showToast("You have already requested to join this ride.");
                                } else {
                                    showToast("Failed to send request: " + e.getMessage());
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
