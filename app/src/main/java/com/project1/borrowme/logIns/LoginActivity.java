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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project1.borrowme.MainActivity;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.models.MyUser;

public class LoginActivity extends AppCompatActivity {
    private MyUser myUser ;
    private FirebaseAuth auth;
    private EditText LogIn_ET_email;
    private EditText LogIn_ET_password;
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


    }

    @Override
    protected void onStart() {
        super.onStart();
        initViews();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            login();
        } else {
            getTheUser();
        }

    }

    private void getTheUser() {
        FirebaseUtil.getUserReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    MyUser fetchedUser = task.getResult().toObject(MyUser.class);
                    setUser(fetchedUser);
                }
            }
        });
    }

    private void setUser(MyUser fetchedUser) {
        if (fetchedUser != null) {
            myUser =MyUser.getInstance();
            myUser.setUid(fetchedUser.getUid());
            myUser.setuName(fetchedUser.getuName());
            myUser.setuEmail(fetchedUser.getuEmail());
            myUser.setLat(fetchedUser.getLat());
            myUser.setLan(fetchedUser.getLan());
            myUser.setCategories(fetchedUser.getCategories());

            changeActivity(false);
        }
    }

    private void initViews() {
        LogIn_BTN_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        LogIn_MTV_singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(true);
            }
        });
    }

    private void findViews() {
        LogIn_ET_email = findViewById(R.id.LogIn_ET_email);
        LogIn_ET_password = findViewById(R.id.LogIn_ET_password);
        LogIn_BTN_LOGIN = findViewById(R.id.LogIn_BTN_LOGIN);
        LogIn_MTV_singUp = findViewById(R.id.LogIn_MTV_singUp);
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


    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser currentUser = auth.getCurrentUser();
                            MySignal.getInstance().toast("Login Successful");
                            getTheUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The password is invalid or the user does not have a password
                                MySignal.getInstance().toast("Invalid password. Please try again.");
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                // There is no user record corresponding to this identifier
                                MySignal.getInstance().toast("Email not registered. Please sign up.");
                            } else {
                                // Other errors
                                MySignal.getInstance().toast("Authentication failed. " + task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
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

    private void login() {
        String email = LogIn_ET_email.getText().toString().trim();
        String password = LogIn_ET_password.getText().toString().trim();

        if (validateInputs(email, password)) {
            signIn(email, password);
        }
    }
}