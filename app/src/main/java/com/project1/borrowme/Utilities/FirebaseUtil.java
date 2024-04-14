package com.project1.borrowme.Utilities;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project1.borrowme.models.Category;

import java.util.Map;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DocumentReference currentUserFirestore() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
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



    public static void updateUserCategories(Map<String, Category> selectedCategories) {
        currentUserFirestore()
                .update("userDetails.categories", selectedCategories)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Categories updated successfully!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating categories", e));
    }

    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
}

