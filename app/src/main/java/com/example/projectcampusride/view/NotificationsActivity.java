package com.example.projectcampusride.view;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcampusride.NotificationAdapter;
import com.example.projectcampusride.NotificationModel;
import com.example.projectcampusride.NotificationsAdapter;
import com.example.projectcampusride.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private ListView listViewNotifications;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        listViewNotifications = findViewById(R.id.listView_notifications);
        //userId = getIntent().getStringExtra("USER_ID");
        userId ="JXC2GvsPvhBcC0REPJ9F";

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestore = FirebaseFirestore.getInstance();

        loadNotifications();
    }

    private void loadNotifications() {
        firestore.collection("users").document(userId).collection("notifications")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("NotificationsActivity", "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Map<String, Object>> notifications = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Map<String, Object> notification = document.getData();
                            notification.put("id", document.getId());
                            notifications.add(notification);
                            Log.d("NotificationsActivity", "Loaded notification: " + notification.toString());
                        }
                        updateListView(notifications);
                    } else {
                        Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void updateListView(List<Map<String, Object>> notifications) {
        Log.d("NotificationsActivity", "Updating ListView with " + notifications.size() + " notifications.");
        NotificationsAdapter adapter = new NotificationsAdapter(this, notifications, userId);
        listViewNotifications.setAdapter(adapter);
    }
}
