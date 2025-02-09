package com.example.projectcampusride.repositories;

import java.util.*;
import android.util.Log;
import android.widget.ListView;

import com.example.projectcampusride.RideAdapter;
import com.example.projectcampusride.models.Ride;
import com.example.projectcampusride.models.RideStatus;
import com.example.projectcampusride.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RideRepository {
    private static final String TAG = "RideRepository";
    private static final String COLLECTION_RIDES = "rides";
    private static final String COLLECTION_REQUESTS = "rideRequests";

    private static final String COLLECTION_USERS= "users";

    private static RideRepository instance;
    private final FirebaseFirestore db;
    private ListView ridesListView;
    private RideAdapter.OnJoinClickListener joinClickListener;

    public RideRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized RideRepository getInstance() {
        if (instance == null) {
            instance = new RideRepository();
        }
        return instance;
    }

    public interface OnRidesFetchedListener {
        void onSuccess(List<Ride> rides);
        void onFailure(Exception e);
    }

    public Task<Void> createRide(Ride ride) {
        String rideId = db.collection(COLLECTION_RIDES).document().getId();
//        ride.setRideId(rideId);
        ride.setStatus("ACTIVE");

        Map<String, Object> rideData = new HashMap<>();
        rideData.put("rideId", ride.getRideId());
        rideData.put("status", ride.getStatus());
        rideData.put("driverId", ride.getDriverId());
        rideData.put("startLocation", ride.getStartLocation());
        rideData.put("endLocation", ride.getEndLocation());
        rideData.put("date", ride.getDate().toString());
        rideData.put("time", ride.getTime().toString());
        rideData.put("availableSeats", ride.getAvailableSeats());
        rideData.put("price", ride.getPrice());
        rideData.put("passengers", new ArrayList<String>());

        return db.collection(COLLECTION_RIDES)
                .document(rideId)
                .set(rideData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ride created successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating ride", e));
    }

    public void getRidesForDriver(String driverId, OnRidesFetchedListener listener) {
        db.collection(COLLECTION_RIDES)
                .whereEqualTo("driverId", driverId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Ride> driverRides = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Ride ride = document.toObject(Ride.class); // Convert Firestore document to Ride object
                        driverRides.add(ride);
                    }
                    listener.onSuccess(driverRides); // Return list via callback
                })
                .addOnFailureListener(e -> {
                    Log.e("RideService", "Error fetching rides for driver", e);
                    listener.onFailure(e);
                });
    }

    public void getRidesForPassenger(String passengerId, OnRidesFetchedListener listener) {
        db.collection(COLLECTION_REQUESTS)
                .whereEqualTo("passengerId", passengerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Ride> passengerRides = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Ride ride = document.toObject(Ride.class); // Convert Firestore document to Ride object
                        passengerRides.add(ride);
                    }
                    listener.onSuccess(passengerRides); // Return list via callback
                })
                .addOnFailureListener(e -> {
                    Log.e("RideService", "Error fetching rides for driver", e);
                    listener.onFailure(e);
                });
    }

    public Task<Boolean> isDriver(String id){
        // Get reference to the user's document in the 'users' collection
        DocumentReference userDocRef = db.collection(COLLECTION_USERS).document(id);

        // Fetch the document asynchronously
        return userDocRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc = task.getResult();
                if (userDoc != null && userDoc.exists()) {
                    // Check if the user has the role 'driver'
                    String role = userDoc.getString("role"); // Assuming 'role' field holds the user role
                    return "driver".equals(role); // Return true if the role is 'driver', false otherwise
                }
            }
            return false; // Return false if the user is not found or the role is not 'driver'
        });
    }


    //   public void searchRides(String startLocation, String endLocation) {
    //      db.collection(COLLECTION_RIDES)
    //               .whereEqualTo("startLocation", startLocation)
    //              .whereEqualTo("endLocation", endLocation)
    //              .get()
    //              .addOnSuccessListener(queryDocumentSnapshots -> {
//                    showProgress(false);
    //
    //                  Set<Map<String, Object>> matchingRides = new HashSet<>();
    //                  for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
    //                    matchingRides.add(document.getData());
    //                 }
    //               updateRidesList(new ArrayList<>(matchingRides));
    //            })
    //        .addOnFailureListener(e -> {
//                    showProgress(false);
//                    showToast("Error: " + e.getMessage());
    //                Log.e(TAG, "Error searching rides", e);
    //             });
//        return db.collection(COLLECTION_RIDES)
//                .whereEqualTo("startLocation", startLocation)
//                .whereEqualTo("endLocation", endLocation)
//                .whereGreaterThan("availableSeats", 0)
//                .whereEqualTo("status", "ACTIVE")
//                .get()
//                .continueWith(task -> {
//                    List<Map<String, Object>> results = new ArrayList<>();
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Map<String, Object> ride = document.getData();
//                            ride.put("id", document.getId());
//                            results.add(ride);
//                        }
//                    }
//                    return results;
//                });
    //   }

//    public void updateRidesList(List<Map<String, Object>> rides) {
//        List<Ride> rideList = new ArrayList<>();

//        for (Map<String, Object> rideData : rides) {
////          String driverName = (String) rideData.get("driverName");
//            String startLocation = (String) rideData.get("startLocation");
//            String endLocation = (String) rideData.get("endLocation");
//            Long seats = (Long) rideData.get("availableSeats");
//            Double price = (Double) rideData.get("price");
//            //String rideId = (String) rideData.get("rideId");  // Make sure to get the ID
//            String RideDate = (String) rideData.get("date");
//            String time = (String) rideData.get("time");
//
//            if ( startLocation != null && endLocation != null &&
//                    seats != null && price != null) {
//                Ride ride = new Ride("", "", startLocation, endLocation, RideDate, time,
//                        seats.intValue(), price, RideStatus.ACTIVE);  // Update Ride constructor to include ID
//                rideList.add(ride);
//                System.out.println("found a ride");
//            }
//        }
//
//    }


    public Task<List<Map<String, Object>>> getAllRides() {
        return db.collection(COLLECTION_RIDES)
                .whereEqualTo("status", RideStatus.ACTIVE)
                .get()
                .continueWith(task -> {
                    List<Map<String, Object>> rides = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> ride = document.getData();
                            ride.put("id", document.getId());
                            rides.add(ride);
                        }
                    }
                    return rides;
                });
    }

    public Task<String> requestToJoinRide(String rideId, User passenger) {
        Map<String, Object> request = new HashMap<>();
        request.put("rideId", rideId);
        request.put("passengerId", passenger.getUserId());
        request.put("passengerName", passenger.getName());
        request.put("status", "PENDING");
        request.put("createdAt", com.google.firebase.Timestamp.now());

        return db.collection(COLLECTION_REQUESTS)
                .add(request)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().getId();
                    } else {
                        throw new Exception("Failed to create request");
                    }
                });
    }

    public Task<Void> approvePassengerRequest(String requestId) {
        return db.runTransaction(transaction -> {
            DocumentSnapshot requestDoc = transaction.get(db.collection(COLLECTION_REQUESTS).document(requestId));

            if (!requestDoc.exists()) {
                try {
                    throw new Exception("Request not found");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            String rideId = requestDoc.getString("rideId");
            String passengerId = requestDoc.getString("passengerId");

            DocumentSnapshot rideDoc = transaction.get(db.collection(COLLECTION_RIDES).document(rideId));
            if (!rideDoc.exists()) {
                try {
                    throw new Exception("Ride not found");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Long availableSeats = rideDoc.getLong("availableSeats");
            if (availableSeats == null || availableSeats <= 0) {
                try {
                    throw new Exception("No seats available");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            List<String> passengers = (List<String>) rideDoc.get("passengers");
            if (passengers == null) passengers = new ArrayList<>();
            passengers.add(passengerId);

            transaction.update(db.collection(COLLECTION_RIDES).document(rideId),
                    "availableSeats", availableSeats - 1,
                    "passengers", passengers);
            transaction.update(db.collection(COLLECTION_REQUESTS).document(requestId), "status", "APPROVED");

            return null;
        });
    }

    public Task<Void> rejectPassengerRequest(String requestId) {
        return db.collection(COLLECTION_REQUESTS).document(requestId).update("status", "REJECTED");
    }

    public Task<List<Map<String, Object>>> getRequestsForDriver(String driverName) {
        return db.collection(COLLECTION_RIDES)
                .whereEqualTo("driverName", driverName)
                .get()
                .continueWithTask(task -> {
                    List<Task<QuerySnapshot>> requestTasks = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot rideDoc : task.getResult()) {
                            Task<QuerySnapshot> requestTask = db.collection(COLLECTION_REQUESTS)
                                    .whereEqualTo("rideId", rideDoc.getId())
                                    .whereEqualTo("status", "PENDING")
                                    .get();
                            requestTasks.add(requestTask);
                        }
                    }
                    return Tasks.whenAllSuccess(requestTasks);
                })
                .continueWith(task -> {
                    List<Map<String, Object>> requests = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (Object result : task.getResult()) {
                            if (result instanceof QuerySnapshot) {
                                for (DocumentSnapshot document : ((QuerySnapshot) result).getDocuments()) {
                                    Map<String, Object> request = document.getData();
                                    request.put("id", document.getId());
                                    requests.add(request);
                                }
                            }
                        }
                    }
                    return requests;
                });
    }

    public void setOnJoinClickListener(RideAdapter.OnJoinClickListener listener) {
        this.joinClickListener = listener;
    }

    public Task<Void> deleteRide(String rideId) {
        DocumentReference rideRef = db.collection(COLLECTION_RIDES).document(rideId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot rideSnapshot = transaction.get(rideRef);
            if (!rideSnapshot.exists()) {
                throw new RuntimeException("Ride not found");
            }

            List<String> passengers = (List<String>) rideSnapshot.get("passengers");
            String startLocation = rideSnapshot.getString("startLocation");
            String endLocation = rideSnapshot.getString("endLocation");
            String driverName = rideSnapshot.getString("driverName");
            String rideTime = rideSnapshot.getString("time");
            String rideDate = rideSnapshot.getString("date");


            transaction.delete(rideRef);

            if (passengers != null) {
                for (String passengerId : passengers) {
                    DocumentReference userRef = db.collection(COLLECTION_USERS).document(passengerId);
                    String notificationMessage = "🚗 הנסיעה בוטלה ע\"י הנהג: " + driverName +
                            "\n מוצא: " + startLocation +
                            "\n יעד: " + endLocation +
                            "\n תאריך: " + rideDate +
                            "\n שעה: " + rideTime;
                    transaction.update(userRef, "notifications." + rideId, notificationMessage);
                }
            }
            return null;
        });
    }

    public Task<Boolean> removePassengerFromRide(String rideId, String passengerId) {
        DocumentReference rideRef = db.collection(COLLECTION_RIDES).document(rideId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot rideSnapshot = transaction.get(rideRef);
            if (!rideSnapshot.exists()) {
                try {
                    throw new Exception("Ride not found");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            List<String> passengers = (List<String>) rideSnapshot.get("passengers");
            if (passengers == null || !passengers.contains(passengerId)) {
                return false;
            }

            passengers.remove(passengerId);
            transaction.update(rideRef, "passengers", passengers);


            long availableSeats = (long) rideSnapshot.get("availableSeats");
            transaction.update(rideRef, "availableSeats", availableSeats + 1);
            DocumentReference userRef = db.collection(COLLECTION_USERS).document(passengerId);
            String notificationMessage = "הוסרת מנסיעה מספר" + rideId + "על ידי הנהג.";
            transaction.update(userRef, "notifications." + rideId, notificationMessage);

            return true;
        });
    }

    public Task<Boolean> cancelRideByPassenger(String rideId, String passengerId) {
        DocumentReference rideRef = db.collection(COLLECTION_RIDES).document(rideId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot rideSnapshot = transaction.get(rideRef);
            if (!rideSnapshot.exists()) {
                try {
                    throw new Exception("Ride not found");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            List<String> passengers = (List<String>) rideSnapshot.get("passengers");
            if (passengers == null || !passengers.contains(passengerId)) {
                return false;
            }

            passengers.remove(passengerId);
            transaction.update(rideRef, "passengers", passengers);

            long availableSeats = (long) rideSnapshot.get("availableSeats");
            transaction.update(rideRef, "availableSeats", availableSeats + 1);

            String driverId = rideSnapshot.getString("driverId");
            DocumentReference driverRef = db.collection(COLLECTION_USERS).document(driverId);

            String notificationMessage = "נוסע ביטל את השתתפותו בנסיעה שלך.";
            transaction.update(driverRef, "notifications." + rideId, notificationMessage);

            return true;
        });
    }
}
