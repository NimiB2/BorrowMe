package com.project1.borrowme.logIns;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.MySignal;

import java.io.IOException;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private final int FINE_PERMISSION_CODE = 1;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationClient;
    private TextInputEditText signUp_ET_userName;
    private TextInputEditText signUp_ET_email;
    private TextInputEditText signUp_ET_password;
    private MaterialButton signUp_BTN_SingUp;
    private MaterialTextView signUp_MTV_LogIn;
    private FirebaseAuth auth;
    private SwitchMaterial signUp_SWITCH_location;
    private TextInputEditText signUp_ET_searchBox;
    private double latitude = 32.8007048;
    private double longitude = 32.1027879;


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

        auth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViews();
        initViews();
    }

    private void initViews() {
        initSignUp();
        initLocation();
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

    private void initLocation() {
        signUp_SWITCH_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The switch is enabled/checked
                    signUp_ET_searchBox.setVisibility(View.GONE);
                    String uId = auth.getCurrentUser().getUid();
                    getLocation(uId);
                } else {
                    // The switch is disabled/unchecked
                    signUp_ET_searchBox.setVisibility(View.VISIBLE);
                }
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
                getLocation(uId);
                changeRegistrationActivity(email, password, userName, uId, latitude, longitude);
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

    private void getLocation(String uId) {
        // The switch is disabled/unchecked
        if (signUp_SWITCH_location.isChecked()) {
            getCurrentLocation(uId);
        } else getLocationByAddress(uId);
    }


    private void getLocationByAddress(String uId) {
        String address = signUp_ET_searchBox.getText().toString().trim(); // Correctly obtain the address from the input
        Address locationAddress = FirebaseUtil.fetchLocationFromAddress(SignUpActivity.this, address);
        if (locationAddress != null) {
            double latitude = locationAddress.getLatitude();
            double longitude = locationAddress.getLongitude();
            FirebaseUtil.updateUserLocation(uId, latitude, longitude);
        } else {
            // Address not found
        }
    }

    private void getCurrentLocation(String uId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request for permissions
            ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        FirebaseUtil.fetchCurrentLocation(SignUpActivity.this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                FirebaseUtil.updateUserLocation(uId, latitude, longitude);
            } else {
                // Handle location is null
            }
        });
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

//    private boolean validateInputs(String userName, String email, String password) {
//        if (userName.isEmpty() || userName.length() < 3) {
//            signUp_ET_userName.setError("Username must be at least 3 characters");
//            return false;
//        }
//        if (email.isEmpty()) {
//            signUp_ET_email.setError("Email cannot be empty");
//            return false;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            signUp_ET_email.setError("Email is not valid");
//            return false;
//        }
//        if (password.isEmpty()) {
//            signUp_ET_password.setError("Password cannot be empty");
//            return false;
//        }
//        if (password.length() < 6) {
//            signUp_ET_password.setError("Password must be at least 6 characters");
//            return false;
//        }
//
//        return true;
//    }

    private void findViews() {
        signUp_ET_userName = findViewById(R.id.signUp_ET_userName);
        signUp_ET_email = findViewById(R.id.signUp_ET_email);
        signUp_ET_password = findViewById(R.id.signUp_ET_password);
        signUp_BTN_SingUp = findViewById(R.id.signUp_BTN_LOGIN);
        signUp_MTV_LogIn = findViewById(R.id.signUp_MTV_LogIn);
        signUp_SWITCH_location = findViewById(R.id.signUp_SWITCH_location);
        signUp_ET_searchBox = findViewById(R.id.signUp_ET_searchBox);
    }
}