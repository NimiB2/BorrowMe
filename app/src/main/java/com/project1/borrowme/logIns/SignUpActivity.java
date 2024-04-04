package com.project1.borrowme.logIns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.MySignal;

public class SignUpActivity extends AppCompatActivity {

    private EditText signUp_ET_userName;
    private EditText signUp_ET_email;
    private EditText signUp_ET_password;
    private MaterialButton signUp_BTN_SingUp;
    private MaterialTextView signUp_MTV_LogIn;
    private FirebaseAuth auth;

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
        findViews();
        initViews();
    }

    private void initViews() {
        signUp_BTN_SingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = signUp_ET_userName.getText().toString().trim();
                String email = signUp_ET_email.getText().toString().trim();
                String password = signUp_ET_password.getText().toString().trim();

                if (validateInputs(userName, email, password)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uId = auth.getCurrentUser().getUid();
                                changeRegistrationActivity(email, password, userName, uId);
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    signUp_ET_email.setError("Email is already in use");
                                } else {
                                    MySignal.getInstance().toast("SingUp Failed");
                                }
                            }
                        }
                    });
                }

            }
        });

        signUp_MTV_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLogInActivity();
            }
        });

    }

    private void changeRegistrationActivity(String email, String password, String userName, String uId) {
        Intent intent = new Intent(this, RegistrationActivity.class);

        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("userName", userName);
        intent.putExtra("uId", uId);

        startActivity(intent);
        finish();
    }

    private void changeLogInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private boolean validateInputs(String userName, String email, String password) {
        if (userName.isEmpty()) {
            signUp_ET_userName.setError("Name cannot be empty");
            return false;
        }
        if (email.isEmpty()) {
            signUp_ET_email.setError("Email cannot be empty");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUp_ET_email.setError("Email is not valid");
            return false;
        }
        if (password.isEmpty()) {
            signUp_ET_password.setError("Password cannot be empty");
            return false;
        }
        if (password.length() < 6) {
            signUp_ET_password.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }


    private void findViews() {
        signUp_ET_userName = findViewById(R.id.signUp_ET_userName);
        signUp_ET_email = findViewById(R.id.signUp_ET_email);
        signUp_ET_password = findViewById(R.id.signUp_ET_password);
        signUp_BTN_SingUp = findViewById(R.id.signUp_BTN_LOGIN);
        signUp_MTV_LogIn = findViewById(R.id.signUp_MTV_LogIn);
    }
}