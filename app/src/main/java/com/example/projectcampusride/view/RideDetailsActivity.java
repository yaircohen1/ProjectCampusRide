package com.example.projectcampusride.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectcampusride.R;
import com.example.projectcampusride.RideManager;
import com.example.projectcampusride.models.Ride;
import com.example.projectcampusride.models.User;
import com.example.projectcampusride.models.UserRole;
import com.example.projectcampusride.repositories.RideRepository;

public class RideDetailsActivity extends AppCompatActivity {
    private RideRepository rideManager;
    private Ride selectedRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        rideManager = RideRepository.getInstance();
        selectedRide = (Ride) getIntent().getSerializableExtra("SELECTED_TRIP");

        // הצגת פרטי הנסיעה
        TextView tripDetails = findViewById(R.id.ride_details);
        tripDetails.setText(String.format("מסלול: %s → %s\nנהג: %s\nמקומות פנויים: %d\nמחיר: %.2f ₪",
                selectedRide.getStartLocation(),
                selectedRide.getEndLocation(),
                selectedRide.getDriverId(),
                selectedRide.getAvailableSeats(),
                selectedRide.getPrice()));

        Button requestJoinButton = findViewById(R.id.request_join_button);
        requestJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // כאן תהיה הזדהות משתמש אמיתית - זו דוגמה פשוטה
                User passenger = new User("1234","ישראל ישראלי",
                        "israel@gmail.com", "050-00000000", false
                        , 0, UserRole.PASSENGER);

                if (selectedRide.getAvailableSeats() > 0) {
                    rideManager.requestToJoinRide(
                            selectedRide.getRideId(),
                            passenger
                    );
                    Toast.makeText(RideDetailsActivity.this,
                            "בקשת הצטרפות נשלחה לנהג",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RideDetailsActivity.this,
                            "אין מקומות פנויים בנסיעה",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
