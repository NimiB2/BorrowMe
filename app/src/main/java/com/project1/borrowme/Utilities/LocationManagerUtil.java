package com.project1.borrowme.Utilities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.project1.borrowme.BuildConfig;
import com.project1.borrowme.interfaces.LocationFetchListener;

import java.util.Arrays;

public class LocationManagerUtil {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private AutocompleteSupportFragment autocompleteFragment;
    private LocationFetchListener locationFetchListener;



    public LocationManagerUtil(Context context, AutocompleteSupportFragment autocompleteFragment, LocationFetchListener locationFetchListener) {
        this.context = context;
        this.autocompleteFragment = autocompleteFragment;
        this.locationFetchListener = locationFetchListener;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.my_api_key);
        }
        initAutocomplete();
    }


    public void handleLocation(boolean isLocationSwitchOn) {
        if (isLocationSwitchOn) {
            getCurrentLocation();
        }
    }


    private void initAutocomplete() {
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                locationFetchListener.onLocationFetched(latitude, longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("TAG", "An error occurred: " + status);
                locationFetchListener.onLocationFetchFailed();
            }
        });
    }


    private void getCurrentLocation() {
        checkLocationPermission();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        locationFetchListener.onLocationFetched(location.getLatitude(), location.getLongitude());
                    } else {
                        locationFetchListener.onLocationFetchFailed();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LocationError", "Failed to get location: ", e);
                    locationFetchListener.onLocationFetchFailed();
                });
    }


    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                locationFetchListener.onLocationFetchFailed();
            }
        }
    }

}