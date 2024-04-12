package com.project1.borrowme.Utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project1.borrowme.models.Category;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static boolean validateUserName(TextInputEditText userNameEditText) {
        String userName = userNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError("Name cannot be empty");
            return false;
        } else if (userName.length() <= 3) {
            userNameEditText.setError("Name must be more than 3 characters");
            return false;
        }
        return true;
    }

    public static boolean validateEmail(TextInputEditText emailEditText) {
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is not valid");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(TextInputEditText passwordEditText) {
        String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    public static void fetchCurrentLocation(Context context, OnSuccessListener<Location> onSuccessListener) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(onSuccessListener);
    }

    public static Address fetchLocationFromAddress(Context context, String addressInput) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(addressInput, 1);
            if (!addresses.isEmpty()) {
                return addresses.get(0); // Returning the first result
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Address not found or error occurred
    }

    public static void updateUserLocation(String userId, double latitude, double longitude) {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);

        FirebaseFirestore.getInstance().collection("users").document(userId)
                .update(locationData)
                .addOnSuccessListener(aVoid -> {
                    // Log success or inform the user
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    public static void updateUserCategories(Map<String, Category> selectedCategories) {
        currentUserDetails()
                .update("categories", selectedCategories)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Categories updated successfully!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating categories", e));
    }

    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
}

