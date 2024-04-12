package com.project1.borrowme.logIns;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.project1.borrowme.BuildConfig;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.interfaces.LocationFetchListener;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextInputEditText signUp_ET_userName;
    private TextInputEditText signUp_ET_email;
    private TextInputEditText signUp_ET_password;
    private MaterialButton signUp_BTN_SingUp;
    private MaterialTextView signUp_MTV_LogIn;
    private FirebaseAuth auth;
    private SwitchMaterial signUp_SWITCH_location;
    private MaterialCardView signUp_CARD_search;
    private double longitude ;
    private double latitude ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), BuildConfig.my_api_key);
        }

        findViews();
        initViews();
        autoCompleteInit();
    }

    private void autoCompleteInit() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.signUp_FRAGMENT_autoComplete);

        // Set the type of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Handle the response when a place is selected.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                // Save the selected location's latitude and longitude
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void initViews() {
        initSignUp();
        initLocationButtonsVisibility();
        initLogIn();
    }


    private void initLogIn() {
        signUp_MTV_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLogInActivity();
            }
        });
    }

    private void initLocationButtonsVisibility() {
        signUp_SWITCH_location.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Hide search box when using current location
                signUp_CARD_search.setVisibility(View.GONE);
            } else {
                // Show search box when manual location is needed
                signUp_CARD_search.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initSignUp() {
        signUp_BTN_SingUp.setOnClickListener(v -> {
            initiateSignUp();
        });
    }
    private void initiateSignUp() {
        // Extract input values
        String userName = signUp_ET_userName.getText().toString().trim();
        String email = signUp_ET_email.getText().toString().trim();
        String password = signUp_ET_password.getText().toString().trim();

        // Validate inputs and proceed if valid
        if (FirebaseUtil.validateUserName(signUp_ET_userName) &&
                FirebaseUtil.validateEmail(signUp_ET_email) &&
                FirebaseUtil.validatePassword(signUp_ET_password)) {
            createUserWithEmail(email, password, userName);
        }
    }

    private void createUserWithEmail(String email, String password, String userName) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                // User successfully created, proceed with additional setup
                String uId = auth.getCurrentUser().getUid();
                LocationFetchListener locationFetchListener = new LocationFetchListener() {
                    @Override
                    public void onLocationFetched(double lat, double lon) {
                        changeRegistrationActivity(email, password, userName, uId, lat, lon);
                    }

                    @Override
                    public void onLocationFetchFailed() {
                        runOnUiThread(() -> MySignal.getInstance().toast( "Failed to fetch location. Please ensure your location services are enabled, or enter a valid address."));
                    };
                };

                getLocation(locationFetchListener);

            } else {
                // Handle failure, such as email already in use or other errors
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    signUp_ET_email.setError("Email is already in use");
                } else {
                    MySignal.getInstance().toast("SignUp Failed");
                }
            }
        });
    }

    private void getLocation(LocationFetchListener locationFetchListener) {
        // The switch is disabled/unchecked
        if (signUp_SWITCH_location.isChecked()) {
            getCurrentLocation(locationFetchListener);
        } else {
            if (latitude != 0 && longitude != 0) {
                locationFetchListener.onLocationFetched(latitude, longitude);
            } else {
                locationFetchListener.onLocationFetchFailed();
            }
        }
    }


//    private void getLocationByAddress(String address, LocationFetchListener listener) {
//        Geocoder geocoder = new Geocoder(SignUpActivity.this);
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(address, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address location = addresses.get(0);
//                listener.onLocationFetched(location.getLatitude(), location.getLongitude());
//            } else {
//                listener.onLocationFetchFailed();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            listener.onLocationFetchFailed();
//        }
//    }

    private void getCurrentLocation(LocationFetchListener listener) {
        checkLocationPermission();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        listener.onLocationFetched(location.getLatitude(), location.getLongitude());
                    } else {
                        listener.onLocationFetchFailed();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LocationError", "Failed to get location: ", e);
                    listener.onLocationFetchFailed();
                });
    }


    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
    }

    private void changeRegistrationActivity(String email, String password, String
            userName, String uId, double latitude, double longitude) {
        Intent intent = new Intent(this, RegistrationActivity.class);

        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("userName", userName);
        intent.putExtra("uId", uId);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        startActivity(intent);
        finish();
    }

    private void changeLogInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        signUp_ET_userName = findViewById(R.id.signUp_ET_userName);
        signUp_ET_email = findViewById(R.id.signUp_ET_email);
        signUp_ET_password = findViewById(R.id.signUp_ET_password);
        signUp_BTN_SingUp = findViewById(R.id.signUp_BTN_LOGIN);
        signUp_MTV_LogIn = findViewById(R.id.signUp_MTV_LogIn);
        signUp_SWITCH_location = findViewById(R.id.signUp_SWITCH_location);
        signUp_CARD_search= findViewById(R.id.signUp_CARD_search);
    }
}