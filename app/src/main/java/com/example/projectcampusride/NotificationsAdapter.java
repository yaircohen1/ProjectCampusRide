package com.example.projectcampusride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projectcampusride.models.RideStatus;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class NotificationsAdapter extends ArrayAdapter<Map<String, Object>> {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final String userId;

    public NotificationsAdapter(@NonNull Context context, @NonNull List<Map<String, Object>> notifications, String userId) {
        super(context, 0, notifications);
        this.userId = userId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        Map<String, Object> notification = getItem(position);

        TextView messageView = convertView.findViewById(R.id.notificationMessage);
        Button approveButton = convertView.findViewById(R.id.approveButton);
        Button refuseButton = convertView.findViewById(R.id.refuseButton);

        String message = (String) notification.get("message");
        String type = (String) notification.get("type");
        String notificationId = (String) notification.get("id");
        String rideId = (String) notification.get("rideId");
        String passengerId = (String) notification.get("passengerId");
        String passengerName = (String) notification.get("passengerName");
        String createdAt = (String) notification.get("createdAt");
        String requestId = (String) notification.get("requestId");


        if (createdAt != null) {
            messageView.setText(message + "\nDate: " + createdAt);
        } else {
            messageView.setText(message);
        }

        if ("decision".equals(type)) {
            String status = (String) notification.get("status");
            Log.d("NotificationsAdapter", "Notification is of type 'decision'");
            if ("pending".equals(status)) {
                approveButton.setVisibility(View.VISIBLE);
                refuseButton.setVisibility(View.VISIBLE);

                approveButton.setOnClickListener(v -> updateNotificationStatus(notificationId, "accepted", rideId, passengerId, passengerName, requestId));
                refuseButton.setOnClickListener(v -> updateNotificationStatus(notificationId, "declined", rideId, passengerId, passengerName,requestId));
            } else {
                approveButton.setVisibility(View.GONE);
                refuseButton.setVisibility(View.GONE);
                messageView.setText(message + "\nDate: "+ createdAt+"\nStatus: " + status.toUpperCase());
            }
        } else {
            Log.d("NotificationsAdapter", "Notification is of other type: " + type);
            approveButton.setVisibility(View.GONE);
            refuseButton.setVisibility(View.GONE);
        }

        return convertView;
    }


    private void updateNotificationStatus(String notificationId, String status, String rideId, String passengerId, String passengerName, String requestId) {
        firestore.collection("rideRequests").document(requestId)
                .update("status", status)
                .addOnSuccessListener(a -> Log.d("RideRequest", "Ride request status updated to: " + status))
                .addOnFailureListener(e -> Log.e("RideRequest", "Failed to update ride request status", e));

        firestore.collection("users").document(userId).collection("notifications")
                .document(notificationId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Notification " + status, Toast.LENGTH_SHORT).show();

                    if ("accepted".equals(status)) {
                        firestore.collection("rides")
                                .whereEqualTo("rideId", rideId)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                            String documentId = document.getId();

                                            // עדכון הנוסע והפחתת מספר המקומות הפנויים
                                            firestore.collection("rides").document(documentId)
                                                    .update(
                                                            "passengers." + passengerId, passengerName,
                                                            "availableSeats", FieldValue.increment(-1)
                                                    )
                                                    .addOnSuccessListener(a -> Log.d("RideUpdate", "Passenger added and seats updated"))
                                                    .addOnFailureListener(e -> Log.e("RideUpdate", "Failed to update ride", e));

                                            // עדכון רשימת הנסיעות של הנוסע
                                            firestore.collection("users").document(passengerId)
                                                    .update("rides", FieldValue.arrayUnion(rideId))
                                                    .addOnSuccessListener(a -> Log.d("UserUpdate", "Ride added to passenger's ride list"))
                                                    .addOnFailureListener(e -> Log.e("UserUpdate", "Failed to update passenger's ride list", e));

                                            // שליחת התראה לנוסע
                                            firestore.collection("users").document(passengerId)
                                                    .collection("notifications")
                                                    .add(Map.of(
                                                            "message", "✅ הבקשה שלך להצטרף לנסיעה אושרה!",
                                                            "type", "info",
                                                            "rideId", rideId,
                                                            "createdAt", System.currentTimeMillis()
                                                    ))
                                                    .addOnSuccessListener(a -> Log.d("Notification", "Approval notification sent to passenger"))
                                                    .addOnFailureListener(e -> Log.e("Notification", "Failed to send notification", e));
                                        }
                                    } else {
                                        Log.e("RideUpdate", "No ride found with rideId: " + rideId);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("RideUpdate", "Failed to query rides collection", e));

                        NotificationUtils.sendRideRequestStatusNotification(passengerId, rideId, status);
                    } else if ("declined".equals(status)) {
                        NotificationUtils.sendRideRequestStatusNotification(passengerId, rideId, status);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update notification.", Toast.LENGTH_SHORT).show();
                });
    }


}
