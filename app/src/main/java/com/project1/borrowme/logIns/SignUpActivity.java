package com.project1.borrowme.logIns;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.LocationManagerUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.interfaces.LocationFetchListener;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private LocationManagerUtil locationManagerUtil;
    private FusedLocationProviderClient fusedLocationClient;


    private TextInputEditText signUp_ET_userName;
    private TextInputEditText signUp_ET_email;
    private TextInputEditText signUp_ET_password;
    private MaterialButton signUp_BTN_SingUp;
    private MaterialTextView signUp_MTV_LogIn;
    private SwitchMaterial signUp_SWITCH_location;
    private MaterialCardView signUp_CARD_search;



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

        findViews();
        initViews();
        setupLocationManager();
    }



    private void setupLocationManager() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.signUp_FRAGMENT_autoComplete);
        locationManagerUtil = new LocationManagerUtil(this, autocompleteFragment, new LocationFetchListener() {
            @Override
            public void onLocationFetched(double lat, double lon) {
                signUp(signUp_ET_email.getText().toString(), signUp_ET_password.getText().toString(), signUp_ET_userName.getText().toString(), lat, lon);
            }

            @Override
            public void onLocationFetchFailed() {
                MySignal.getInstance().toast("Failed to fetch location. Please ensure your location services are enabled, or enter a valid address.");
            }
        });
    }

    private void getLocation() {
        if (signUp_SWITCH_location.isChecked()) {
            // The switch is ON, get the current location
            locationManagerUtil.getCurrentLocation();
        } else {
            // The switch is OFF, trigger the update with the selected location from autocomplete
            locationManagerUtil.triggerLocationUpdate();
        }
    }

    private void signUp(String email, String password, String userName, double latitude, double longitude) {
        if (FirebaseUtil.validateUserName(signUp_ET_userName) &&
                FirebaseUtil.validateEmail(signUp_ET_email) &&
                FirebaseUtil.validatePassword(signUp_ET_password)) {

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uId = auth.getCurrentUser().getUid();
                    changeRegistrationActivity(email, userName, uId, latitude, longitude);
                } else {

                    MySignal.getInstance().vibrate(true);
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        signUp_ET_email.setError("Email is already in use");
                    } else {
                        MySignal.getInstance().toast("SignUp Failed");
                    }
                }
            });
        }
    }



    private void changeRegistrationActivity(String email, String userName, String uId, double latitude, double longitude) {
        Intent intent = new Intent(this, RegistrationActivity.class);

        intent.putExtra("email", email);
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

    private void initViews() {
        signUp_BTN_SingUp.setOnClickListener(v -> getLocation());
        signUp_MTV_LogIn.setOnClickListener(v -> changeLogInActivity());
        signUp_SWITCH_location.setOnCheckedChangeListener((buttonView, isChecked) -> {
            signUp_CARD_search.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
    }

    private void findViews() {
        signUp_ET_userName = findViewById(R.id.signUp_ET_userName);
        signUp_ET_email = findViewById(R.id.signUp_ET_email);
        signUp_ET_password = findViewById(R.id.signUp_ET_password);
        signUp_BTN_SingUp = findViewById(R.id.signUp_BTN_SingUp);
        signUp_MTV_LogIn = findViewById(R.id.signUp_MTV_LogIn);
        signUp_SWITCH_location = findViewById(R.id.signUp_SWITCH_location);
        signUp_CARD_search= findViewById(R.id.signUp_CARD_search);
    }
}