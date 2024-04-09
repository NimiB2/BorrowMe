package com.project1.borrowme.Utilities;

import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static DocumentReference getUserReference(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static String currentUserId(){
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

}
