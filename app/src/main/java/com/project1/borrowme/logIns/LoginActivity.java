package com.project1.borrowme.logIns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.project1.borrowme.MainActivity;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.MySignal;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private TextInputEditText LogIn_ET_email;
    private TextInputEditText LogIn_ET_password;
    private MaterialButton LogIn_BTN_LOGIN;
    private MaterialTextView LogIn_MTV_singUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        findViews();
        setupViewListeners();
        redirectIfLoggedIn();

    }


    private void redirectIfLoggedIn() {
        FirebaseUser authCurrentUser = auth.getCurrentUser();

        if (authCurrentUser == null) {
            attemptLogin();
        } else {
            changeActivity(false);
        }
    }

    private void changeActivity(boolean isNewUser) {
        Intent intent;
        if (isNewUser) {
            intent = new Intent(this, SignUpActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);

        }
        startActivity(intent);
        finish();
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser currentUser = auth.getCurrentUser();
                        MySignal.getInstance().toast("Login Successful");
                        FirebaseUtil.fetchCurrentUserAndSet();
                        changeActivity(false);

                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }
    private void handleLoginFailure(Exception exception) {
        MySignal.getInstance().vibrate(true);

        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            MySignal.getInstance().toast("Invalid password. Please try again.");
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            MySignal.getInstance().toast("Email not registered. Please sign up.");
        } else {
            MySignal.getInstance().toast("Authentication failed. " + exception.getMessage());
        }
    }
    private boolean inputsAreValid(String email, String password) {
        if (email.isEmpty()) {
            LogIn_ET_email.setError("Email cannot be empty");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            LogIn_ET_email.setError("Email is not valid");
            return false;
        }
        if (password.isEmpty()) {
            LogIn_ET_password.setError("Password cannot be empty");
            return false;
        }
        if (password.length()<6) {
            LogIn_ET_password.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void attemptLogin() {
        String email = LogIn_ET_email.getText().toString().trim();
        String password = LogIn_ET_password.getText().toString().trim();

        if (inputsAreValid(email, password)) {
            login(email, password);
        }
    }
    
    private void setupViewListeners() {
        LogIn_BTN_LOGIN.setOnClickListener(v -> attemptLogin());

        LogIn_MTV_singUp.setOnClickListener(v -> changeActivity(true));
    }

    private void findViews() {
        LogIn_ET_email = findViewById(R.id.LogIn_ET_email);
        LogIn_ET_password = findViewById(R.id.LogIn_ET_password);
        LogIn_BTN_LOGIN = findViewById(R.id.LogIn_BTN_LOGIN);
        LogIn_MTV_singUp = findViewById(R.id.LogIn_MTV_singUp);
    }
}