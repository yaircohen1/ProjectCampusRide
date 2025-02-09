package com.example.projectcampusride.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projectcampusride.R;
import com.example.projectcampusride.models.Ride;

import java.util.List;

public class RideDriverAdapter extends ArrayAdapter<Ride> {

    public RideDriverAdapter(Context context, List<Ride> rides) {
        super(context, 0, rides);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_driver_ride, parent, false);
        }

        Ride ride = getItem(position);

        TextView rideInfo = convertView.findViewById(R.id.ride_info);
        TextView passengerCount = convertView.findViewById(R.id.passenger_count);

        String rideDetails = "מ-" + ride.getStartLocation() + " ל-" + ride.getEndLocation() + "\n"
                + "תאריך: " + ride.getRideDate() + " שעה: " + ride.getRideTime() + "\n"
                + "מקומות פנויים: " + ride.getAvailableSeats() + " מחיר: ₪" + ride.getPrice();

        rideInfo.setText(rideDetails);

        int numberOfPassengers = ride.getPassengers() != null ? ride.getPassengers().size() : 0;
        passengerCount.setText("נוסעים: " + numberOfPassengers);

        return convertView;
    }
}

