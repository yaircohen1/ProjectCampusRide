package com.example.projectcampusride.repositories;

import android.util.Log;

import com.example.projectcampusride.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PassengerRepository {
    private static final String TAG = "PassengerRepository";
    private static PassengerRepository instance;

    private final FirebaseFirestore db;
    private static final String COLLECTION_REQUESTS = "rideRequests";

    private PassengerRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized PassengerRepository getInstance() {
        if (instance == null) {
            instance = new PassengerRepository();
        }
        return instance;
    }

    public Task<String> requestToJoinRide(String rideId, User passenger, String driverId, String driverName, String date, String time) {
        Map<String, Object> request = new HashMap<>();
        request.put("rideId", rideId);
        request.put("driverId", driverId != null ? driverId : "");
        request.put("driverName", driverName != null ? driverName : "Unknown");
        request.put("dateRide", date != null ? date : "");
        request.put("dateTime", time != null ? time : "");
        request.put("passengerId", passenger.getUserId());
        request.put("passengerName", passenger.getName());
        request.put("status", "PENDING");
        request.put("createdAt", Timestamp.now());

        Log.d(TAG, "rideId: " + rideId);
        Log.d(TAG, "driverId: " + driverId);
        Log.d(TAG, "driverName: " + driverName);
        Log.d(TAG, "dateRide: " + date);
        Log.d(TAG, "dateTime: " + time);

        return db.collection(COLLECTION_REQUESTS)
                .add(request)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String requestId = task.getResult().getId();
                        Log.d(TAG, "Created request with ID: " + requestId);

                        return db.collection(COLLECTION_REQUESTS)
                                .document(requestId)
                                .update("requestId", requestId)
                                .continueWith(t -> requestId);
                    } else {
                        throw new Exception("Failed to create request");
                    }
                });
    }

    public Task<Boolean> checkExistingRequest(String rideId, String passengerId) {
        return db.collection(COLLECTION_REQUESTS)
                .whereEqualTo("rideId", rideId)
                .whereEqualTo("passengerId", passengerId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        return true;  // בקשה קיימת
                    }
                    return false;  // אין בקשה
                });
    }

}


