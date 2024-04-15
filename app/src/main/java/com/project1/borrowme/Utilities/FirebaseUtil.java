package com.project1.borrowme.Utilities;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project1.borrowme.adpters.UserAdapter;
import com.project1.borrowme.interfaces.CallbackCheckUsers;
import com.project1.borrowme.interfaces.CallbackReceivedBorrow;
import com.project1.borrowme.models.Borrow;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.ReceivedBorrow;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {
    private CallbackCheckUsers callback;

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


    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }


    public static void addBorrowToFirestore(Borrow borrow, CallbackCheckUsers checkUsers) {
        // Convert the Borrow object into a Map
        Map<String, Object> borrowMap = new HashMap<>();
        borrowMap.put("isOpenBorrow", borrow.isOpenBorrow());
        borrowMap.put("borrowComplete", borrow.isBorrowComplete());
        borrowMap.put("itemName", borrow.getItemName());
        borrowMap.put("description", borrow.getDescription());
        borrowMap.put("categories", borrow.getCategories());
        borrowMap.put("distance", borrow.getRadiusKm());
        borrowMap.put("numOfSending", borrow.getNumOfSending());
        borrowMap.put("numOfAnswers", borrow.getNumOfAnswers());
        borrowMap.put("lat", borrow.getLat());
        borrowMap.put("lon", borrow.getLon());

        // Update the borrowMap in Firestore for the current user
        DocumentReference userDocRef = currentUserFirestore();
        userDocRef.update("borrowMap." + borrow.getId(), borrowMap)
                .addOnSuccessListener(aVoid -> {
                    if (checkUsers != null) {
                        checkUsers.checkUsers(borrow);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding borrow", e));
    }

    public void removeBorrowFromFirestore(String borrowId) {
        // Get the document reference for the current user from Firestore
        DocumentReference userDocRef = currentUserFirestore();

        // Remove the borrow from the borrowMap
        Map<String, Object> updates = new HashMap<>();
        updates.put("borrowMap." + borrowId, FieldValue.delete());

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Borrow removed successfully!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing borrow", e));
    }


    public static void addReceivedBorrowToFirestore(ReceivedBorrow receivedBorrow, String theMap, CallbackReceivedBorrow callbackReceivedBorrow) {
        // Convert the Borrow object into a Map
        Map<String, Object> theMapType = new HashMap<>();
        theMapType.put("id", receivedBorrow.getId());
        theMapType.put("borrow", receivedBorrow.getBorrow());
        theMapType.put("receiveUser", receivedBorrow.getReceiveUser());
        theMapType.put("isApprove", receivedBorrow.isApprove());

        // Update the borrowMap in Firestore for the current user
        DocumentReference userDocRef = currentUserFirestore();
        userDocRef.update(theMap + "." + receivedBorrow.getId(), theMapType) // Notice the change here
                .addOnSuccessListener(aVoid -> {
                    if (callbackReceivedBorrow != null) {
                        callbackReceivedBorrow.onAddToFirebase(receivedBorrow);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding borrow", e));
    }

    public void removeReceivedBorrowFromFirestore(String borrowId) {
        // Get the document reference for the current user from Firestore
        DocumentReference userDocRef = currentUserFirestore();

        // Remove the borrow from the borrowMap
        Map<String, Object> updates = new HashMap<>();
        updates.put("borrowMap." + borrowId, FieldValue.delete());

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Borrow removed successfully!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing borrow", e));
    }
}

