package com.example.projectcampusride.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;

import android.Manifest;

import com.example.projectcampusride.R;
import com.example.projectcampusride.controllers.RoleSelectionController;
import com.example.projectcampusride.models.UserRole;

public class MainMenuActivity extends AppCompatActivity {

    // FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // מוסר בינתיים
    private RoleSelectionController roleSelectionController;

    // במקום לקחת ID מהמשתמש המחובר, נשתמש ב-ID קבוע שקיים בפיירבייס
    String userId = "JXC2GvsPvhBcC0REPJ9F";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        roleSelectionController = new RoleSelectionController();

        // Buttons
        Button btnProfile = findViewById(R.id.btn_profile);
        Button btnDriver = findViewById(R.id.driver_button);
        Button btnPassenger = findViewById(R.id.passenger_button);
        Button btnLogout = findViewById(R.id.btn_logout);
        Button btnNotifications = findViewById(R.id.btn_notifications);

        // Profile Button Click
        btnProfile.setOnClickListener(v -> {
            // כאשר תוסיף מסך התחברות, ניתן לשחזר את קבלת ה- UID כאן
            // if (currentUser != null) {
            Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
            intent.putExtra("USER_ID", userId); // שולח את ה-ID הקבוע לפרופיל
            startActivity(intent);
            // } else {
            //     Toast.makeText(MainMenuActivity.this, "User is not logged in!", Toast.LENGTH_SHORT).show();
            // }
        });

        // Driver Button Click
        btnDriver.setOnClickListener(v -> selectRole(userId, UserRole.DRIVER));

        // Search Ride Button Click
        btnPassenger.setOnClickListener(v -> selectRole(userId, UserRole.PASSENGER));

        // Logout Button Click - מוסר כרגע כי אין מערכת התחברות
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(MainMenuActivity.this, "Logout feature not implemented yet.", Toast.LENGTH_SHORT).show();
            // כאשר תוסיף מסך התחברות, ניתן להפעיל את הקוד הבא:
            // FirebaseAuth.getInstance().signOut();
            // Intent intent = new Intent(MainMenuActivity.this, Login.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
            // finish();
        });

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            intent.putExtra("USER_ID", userId); // שולח את ה-ID הקבוע
            startActivity(intent);
        });
    }

    private void selectRole(String userId, UserRole role) {
        roleSelectionController.setUserRole(userId, role, success -> {
            if (success) {
                navigateToNextScreen(role);
            } else {
                Toast.makeText(this, "Failed to set role. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToNextScreen(UserRole role) {
        Intent intent;
        if (role == UserRole.DRIVER) {
            intent = new Intent(this, DriverMainActivity.class);
        } else {
            intent = new Intent(this, PassengerMainActivity.class);
        }

        // במקום להשתמש ב- currentUser.getUid(), נעביר את ה-ID הקבוע
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
}


