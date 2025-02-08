package com.example.projectcampusride;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.example.projectcampusride.models.RideStatus;

public class NotificationUtils {

    public static void addNotification(String userId, String message, String type, String passengerId, String passengerName, String requestId, String rideId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("type", type);
        notification.put("status", "pending");
        notification.put("passengerId", passengerId);
        notification.put("passengerName", passengerName);
        notification.put("requestId", requestId);
        notification.put("rideId", rideId);


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        notification.put("createdAt", formattedDate);


        firestore.collection("users").document(userId).collection("notifications")
                .add(notification)
                .addOnSuccessListener(docRef -> Log.d("Firestore", "Notification added successfully: " + docRef.getId()))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add notification.", e));
    }

    public static void sendJoinRequestNotification(String driverId, String passengerId, String rideId, String passengerName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String message = passengerName + " wants to join your ride.";

        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("type", "decision"); // סוג התראה שמצריך החלטה
        notification.put("rideId", rideId);
        notification.put("passengerId", passengerId);
        notification.put("status", "pending");

        firestore.collection("users").document(driverId).collection("notifications")
                .add(notification)
                .addOnSuccessListener(docRef -> Log.d("Firestore", "Join request notification sent: " + docRef.getId()))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to send join request notification.", e));
    }

    public static void sendRideRequestStatusNotification(String userId, String rideId, String status) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String message;

        switch (status) {
            case "accepted":
                message = "Your ride request has been approved!";
                break;
            case "declined":
                message = "Your ride request has been cancelled.";
                break;
            default:
                message = "Your ride status has been updated.";
        }

        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("type", "ride_status");
        notification.put("rideId", rideId);
        notification.put("status", status);



        firestore.collection("users").document(userId).collection("notifications")
                .add(notification)
                .addOnSuccessListener(docRef -> Log.d("Firestore", "Ride status request notification sent: " + docRef.getId()))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to send ride request status notification.", e));
    }

    public static void updateExistingNotifications(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(userId).collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String notificationId = document.getId();

                            firestore.collection("users").document(userId).collection("notifications")
                                    .document(notificationId)
                                    .update("type", "info", "status", "pending")
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification updated: " + notificationId))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to update notification: " + notificationId, e));
                        }
                    }
                });
    }
}
