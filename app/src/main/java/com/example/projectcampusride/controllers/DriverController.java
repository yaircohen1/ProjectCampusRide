package com.example.projectcampusride.controllers;


import android.util.Log;

import com.example.projectcampusride.models.Ride;
import com.example.projectcampusride.repositories.DriverRepository;
import com.example.projectcampusride.repositories.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class DriverController {
    private DriverRepository driverRepository;
    private UserRepository userRepository;

    public DriverController() {
        this.driverRepository = DriverRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
    }

    // יצירת נסיעה חדשה
    public void createRide(String driverId, String startLocation, String endLocation, String rideDate, String rideTime, int availableSeats, double price, CreateRideCallback callback) {
        getDriverName(driverId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String driverName = task.getResult();
                driverRepository.createRide(driverId, driverName, startLocation, endLocation, rideDate, rideTime, availableSeats, price)
                        .addOnCompleteListener(createTask -> {
                            if (createTask.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                callback.onFailure(createTask.getException().getMessage());
                            }
                        });
            } else {
                callback.onFailure("Failed to retrieve driver name.");
            }
        });
    }

    public void getDriverRides(String driverId, DriverRidesCallback callback) {
        driverRepository.getDriverRides(driverId)
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Ride> rides = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            try {
                                Ride ride = document.toObject(Ride.class);
                                if (ride != null) {
                                    rides.add(ride);
                                }
                            } catch (Exception e) {
                                Log.e("DriverRides", "Error parsing ride: " + e.getMessage(), e);
                            }
                        }
                        callback.onSuccess(rides);
                    } else {
                        callback.onFailure("No rides found for the driver.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to fetch rides: " + e.getMessage());
                });
    }


    private Task<String> getDriverName(String driverId) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("userId", driverId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String driverName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                        taskCompletionSource.setResult(driverName != null ? driverName : "Unknown");
                    } else {
                        taskCompletionSource.setResult("Unknown");
                    }
                })
                .addOnFailureListener(e -> {
                    taskCompletionSource.setResult("Unknown");
                });

        return taskCompletionSource.getTask();
    }

    public interface DriverRidesCallback {
        void onSuccess(List<Ride> rides);
        void onFailure(String errorMessage);
    }


    public interface CreateRideCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}



