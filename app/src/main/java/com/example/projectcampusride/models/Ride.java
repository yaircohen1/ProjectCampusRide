package com.example.projectcampusride.models;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

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
    private String status;  // נשמור את RideStatus כ-String
    private Map<String, String> passengers;
    private Timestamp createdAt; // נוסיף את createdAt

    public Ride() {
        // Constructor ללא פרמטרים – חובה לשימוש עם Firestore

    }

    public Ride(String rideId, String driverId, String driverName, String startLocation, String endLocation,
                String rideDate, String rideTime, int availableSeats, double price, String status, Timestamp createdAt) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.rideDate = rideDate;
        this.rideTime = rideTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt;
        this.passengers = passengers != null ? passengers : new HashMap<>();
    }

    public Ride(String rideId, String driverId, String driverName, String startLocation, String endLocation, String rideDate, String rideTime, int i, double price, RideStatus rideStatus) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.rideDate = rideDate;
        this.rideTime = rideTime;
        this.price = price;
        this.status = status != null ? status : "ACTIVE";
        this.passengers = new HashMap<>();

    }

    // Getters ו-Setters לכל השדות

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getDate() {
        return rideDate;
    }

    public void setDate(String rideDate) {
        this.rideDate = rideDate;
    }

    public String getTime() {
        return rideTime;
    }

    public void setTime(String rideTime) {
        this.rideTime = rideTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, String> getPassengers() {
        return passengers;
    }

    public void setPassengers(Map<String, String> passengers) {
        this.passengers = passengers;
    }
}



