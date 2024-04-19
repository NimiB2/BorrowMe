package com.project1.borrowme.Utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project1.borrowme.interfaces.CallbackCheckUsers;
import com.project1.borrowme.interfaces.CallbackAddFirebase;
import com.project1.borrowme.interfaces.CallbackGetFirebase;
import com.project1.borrowme.models.Borrow;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.ReceivedBorrow;

import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.models.UserDetails;

public class FirebaseUtil {
    private CallbackCheckUsers callback;
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DocumentReference currentUserFirestore() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference getHistoryReference(){
        return currentUserFirestore().collection("history");
    }

    public static void fetchCurrentUserAndSet() {

        FirebaseUtil.currentUserFirestore().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    TheUser fetchedUser = task.getResult().toObject(TheUser.class);
                    setUser(fetchedUser);
                }
            }
        });
    }

    private static void setUser(TheUser fetchedUser) {
        TheUser theUser= TheUser.getInstance();

        if (fetchedUser != null && fetchedUser.getUserDetails() != null) {
            UserDetails fetchedUserDetails =fetchedUser.getUserDetails();

            if (theUser.getUserDetails() == null) {
                theUser.setUserDetails(new UserDetails());
            }
            theUser.setUid(fetchedUser.getUid());
            theUser.setHistory(fetchedUser.getHistory());
            theUser.setMessages(fetchedUser.getMessages());

            UserDetails userDetails= theUser.getUserDetails();
            userDetails.setuName(fetchedUserDetails.getuName());
            userDetails.setuEmail(fetchedUserDetails.getuEmail());
            userDetails.setLat(fetchedUserDetails.getLat());
            userDetails.setLon(fetchedUserDetails.getLon());
            userDetails.setCategories(fetchedUserDetails.getCategories());

            fetchAndSetUserProfileImage(userDetails);

        } else {
            Log.e("Firestore", "Fetched user or user details are null");
        }
    }

    private static void fetchAndSetUserProfileImage(UserDetails userDetails) {
        StorageReference profilePicRef = FirebaseUtil.getCurrentProfilePicStorageRef();
        profilePicRef.getDownloadUrl()
                .addOnSuccessListener(userDetails::setProfileImageUri)
                .addOnFailureListener(e -> {
                    userDetails.setProfileImageUri(null);
                });
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


//    public static void addBorrowToFirestore(Borrow borrow, CallbackCheckUsers checkUsers) {
//        // Convert the Borrow object into a Map
//        Map<String, Object> borrowHashMap = new HashMap<>();
//        borrowHashMap.put("id", borrow.getId());
//        borrowHashMap.put("senderId", borrow.getSenderId());
//        borrowHashMap.put("senderName", borrow.getSenderName());
//        borrowHashMap.put("itemName", borrow.getItemName());
//        borrowHashMap.put("description", borrow.getDescription());
//        borrowHashMap.put("lat", borrow.getLat());
//        borrowHashMap.put("lon", borrow.getLon());
//        borrowHashMap.put("radiusKm", borrow.getRadiusKm());
//        borrowHashMap.put("numOfSending", borrow.getNumOfSending());
//        borrowHashMap.put("numOfAnswers", borrow.getNumOfAnswers());
//        borrowHashMap.put("OpenBorrow", borrow.getOpenBorrow());
//        borrowHashMap.put("borrowComplete", borrow.getBorrowComplete());
//        borrowHashMap.put("categories", borrow.getCategories());
//
//
//        // Update the borrowHashMap in Firestore for the current user
//        DocumentReference userDocRef = currentUserFirestore();
//
//        userDocRef.update("borrowHashMap." + borrow.getId(), borrowHashMap)
//                .addOnSuccessListener(aVoid -> {
//                    if (checkUsers != null) {
//                        checkUsers.checkUsers(borrow);
//                    }
//                })
//                .addOnFailureListener(e -> Log.w("Firestore", "Error adding borrow", e));
//    }


    public static void addReceivedBorrowToFirestore(ReceivedBorrow receivedBorrow, String theMap, CallbackAddFirebase callbackAddFirebase, String userId) {
        // Convert the Borrow object into a Map
        Map<String, Object> theMapType = new HashMap<>();

        theMapType.put("id", receivedBorrow.getId());
        theMapType.put("createdAt", receivedBorrow.getCreatedAt());
        theMapType.put("receiveUserId", receivedBorrow.getReceiveUserId());
        theMapType.put("Approved", receivedBorrow.getApproved());
        theMapType.put("answer", receivedBorrow.getAnswer());
        theMapType.put("returnAnswer", receivedBorrow.getReturnAnswer());
        theMapType.put("borrow", receivedBorrow.getBorrow());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.update(theMap+ "." + receivedBorrow.getId(), theMapType)
                .addOnSuccessListener(aVoid -> {
                    if (callbackAddFirebase != null) {
                        callbackAddFirebase.onAddToFirebase(receivedBorrow);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding borrow", e));
    }

//    public void removeReceivedBorrowFromFirestore(String borrowId) {
//        // Get the document reference for the current user from Firestore
//        DocumentReference userDocRef = currentUserFirestore();
//
//        // Remove the borrow from the borrowMap
//        Map<String, Object> updates = new HashMap<>();
//
//        updates.put("receivedBorrowMap." + borrowId, FieldValue.delete());
//
//        userDocRef.update(updates)
//                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Borrow removed successfully!"))
//                .addOnFailureListener(e -> Log.w("Firestore", "Error removing borrow", e));
//    }

    public static void fetchCollectionAsMap(String collectionName, CallbackGetFirebase<Map<String, ReceivedBorrow>> callback) {
        FirebaseFirestore.getInstance().collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, ReceivedBorrow> resultMap = new HashMap<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        ReceivedBorrow receivedBorrow = snapshot.toObject(ReceivedBorrow.class);
                        resultMap.put(snapshot.getId(), receivedBorrow);
                    }
                    if (callback != null) {
                        callback.onGet(resultMap);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error fetching collection: " + collectionName, e);
                    if (callback != null) {
                        callback.onGet(null);
                    }
                });
    }


}