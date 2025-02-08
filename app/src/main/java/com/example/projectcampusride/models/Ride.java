package com.example.projectcampusride.models;

import java.util.ArrayList;
import java.util.List;

public class Ride {
    private String rideId;
    private String driverId;
    private String driverName;
    private String startLocation;
    private String endLocation;
    private String rideDate;
    private String rideTime;
    private int availableSeats;
    private double price;
    private RideStatus status;
    private List<String> passengers;

    public Ride(String rideId, String driverId, String driverName , String startLocation, String endLocation,
                String rideDate, String rideTime, int availableSeats, double price, RideStatus status) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.rideDate = rideDate;
        this.rideTime = rideTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.status = status != null ? status : RideStatus.ACTIVE;
        this.passengers = new ArrayList<>();
    }

    // Getters & Setters
    public String getRideId() { return rideId; }
    public String getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public String getDate() { return rideDate; }
    public String getTime() { return rideTime; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }
    public RideStatus getStatus() { return status; }
    public List<String> getPassengers() { return new ArrayList<>(passengers); }

    public void setRideId(String rideId) { this.rideId = rideId; }

    public void setStatus(RideStatus status) { this.status = status; }


    public void addPassenger(String passengerId) { passengers.add(passengerId); }
    public void removePassenger(String passengerId) { passengers.remove(passengerId); }

    @Override
    public String toString() {
        return "Ride { " +
                "ID='" + rideId + '\'' +
                ", Driver='" + driverId + '\'' +
                ", From='" + startLocation + '\'' +
                ", To='" + endLocation + '\'' +
                ", Date='" + rideDate + '\'' +
                ", Time='" + rideTime + '\'' +
                ", Available Seats=" + availableSeats +
                ", Price=" + price +
                ", Status='" + status + '\'' +
                '}';
    }
}


