package com.example.projectcampusride.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DriverRepository {
    private static final String TAG = "DriverRepository";
    private static DriverRepository instance;

    private final FirebaseFirestore db;
    private static final String COLLECTION_REQUESTS = "rideRequests";
    private static final String COLLECTION_RIDES = "rides";

    private DriverRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized DriverRepository getInstance() {
        if (instance == null) {
            instance = new DriverRepository();
        }
        return instance;
    }

    public Task<QuerySnapshot> getDriverRides(String driverId) {
        return db.collection(COLLECTION_RIDES)
                .whereEqualTo("driverId", driverId)
                .get();
    }


    public Task<Void> createRide(String driverId, String driverName,String startLocation, String endLocation, String rideDate, String rideTime, int availableSeats, double price) {
        String rideId = db.collection(COLLECTION_RIDES).document().getId();

        Map<String, Object> rideData = new HashMap<>();
        rideData.put("rideId", rideId);
        rideData.put("driverId", driverId);
        rideData.put("driverName", driverName);
        rideData.put("startLocation", startLocation);
        rideData.put("endLocation", endLocation);
        rideData.put("rideDate", rideDate);
        rideData.put("rideTime", rideTime);
        rideData.put("createdAt", FieldValue.serverTimestamp());
        rideData.put("availableSeats", availableSeats);
        rideData.put("price", price);
        rideData.put("status", "ACTIVE");
        rideData.put("passengers", new HashMap<>());

        return db.collection(COLLECTION_RIDES)
                .document(rideId)
                .set(rideData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ נסיעה נוצרה בהצלחה!"))
                .addOnFailureListener(e -> Log.w(TAG, "❌ שגיאה ביצירת נסיעה", e));
    }

}
