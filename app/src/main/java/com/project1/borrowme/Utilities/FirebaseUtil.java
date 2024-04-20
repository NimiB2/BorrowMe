package com.project1.borrowme.Utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
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

import java.util.ArrayList;
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

    public static CollectionReference getHistoryReference() {
        return currentUserFirestore().collection("history");
    }

    public static void fetchCurrentUserAndSet() {

        FirebaseUtil.currentUserFirestore().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    TheUser fetchedUser = task.getResult().toObject(TheUser.class);
                    setUser(fetchedUser);
                }
            }
        });
    }

    private static void setUser(TheUser fetchedUser) {
        TheUser theUser = TheUser.getInstance();

        if (fetchedUser != null && fetchedUser.getUserDetails() != null) {
            UserDetails fetchedUserDetails = fetchedUser.getUserDetails();

            if (theUser.getUserDetails() == null) {
                theUser.setUserDetails(new UserDetails());
            }
            theUser.setUid(fetchedUser.getUid());
            theUser.setHistory(fetchedUser.getHistory());
            theUser.setMessages(fetchedUser.getMessages());

            UserDetails userDetails = theUser.getUserDetails();
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

    public static void addReceivedBorrowToFirestore(ReceivedBorrow receivedBorrow, String theMap, CallbackAddFirebase callbackAddFirebase, String userId) {
        // Convert the Borrow object into a Map
        Map<String, Object> theMapType = new HashMap<>();

        theMapType.put("id", receivedBorrow.getId());
        theMapType.put("createdAt", receivedBorrow.getCreatedAt());
        theMapType.put("receiveUserId", receivedBorrow.getReceiveUserId());
        theMapType.put("Approved", receivedBorrow.getApproved());
        theMapType.put("deal", receivedBorrow.getDeal());
        theMapType.put("answer", receivedBorrow.getAnswer());
        theMapType.put("returnAnswer", receivedBorrow.getReturnAnswer());
        theMapType.put("borrow", receivedBorrow.getBorrow());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.update(theMap + "." + receivedBorrow.getId(), theMapType)
                .addOnSuccessListener(aVoid -> {
                    if (callbackAddFirebase != null) {
                        callbackAddFirebase.onAddToFirebase(receivedBorrow);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding borrow", e));
    }

    public static void getReceivedBorrowFromFirestore(String theMap, String userId, String borrowId, CallbackGetFirebase callbackGetFirebase) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Access the user document directly
        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();
                if (userDocument.exists()) {
                    Map<String, Object> allHistory = (Map<String, Object>) userDocument.get(theMap);
                    if (allHistory != null) {
                        Map<String, Object> borrowData = (Map<String, Object>) allHistory.get(borrowId);
                        if (borrowData != null) {
                            // Create ReceivedBorrow object manually or use a conversion method if appropriate
                            ReceivedBorrow receivedBorrow = createReceivedBorrowFromMap(borrowData);
                            if (receivedBorrow != null && callbackGetFirebase != null) {
                                callbackGetFirebase.onGetFromFirebase(receivedBorrow);
                            } else {
                                Log.w("Firestore", "Failed to convert map to ReceivedBorrow");
                                if (callbackGetFirebase != null) {
                                    callbackGetFirebase.onFailure(new Exception("Document conversion failure"));
                                }
                            }
                        } else {
                            Log.w("Firestore", "No such borrow document in the history map");
                            if (callbackGetFirebase != null) {
                                callbackGetFirebase.onFailure(new Exception("No document found in the map"));
                            }
                        }
                    } else {
                        Log.w("Firestore", "History map is empty or null");
                        if (callbackGetFirebase != null) {
                            callbackGetFirebase.onFailure(new Exception("History data is missing"));
                        }
                    }
                } else {
                    Log.w("Firestore", "No such user document");
                    if (callbackGetFirebase != null) {
                        callbackGetFirebase.onFailure(new Exception("No user document found"));
                    }
                }
            } else {
                Log.w("Firestore", "Get failed with ", task.getException());
                if (callbackGetFirebase != null) {
                    callbackGetFirebase.onFailure(task.getException());
                }
            }
        });
    }


    public static ReceivedBorrow createReceivedBorrowFromMap(Map<String, Object> borrowData) {
        if (borrowData == null) {
            return null;
        }

        ReceivedBorrow receivedBorrow = new ReceivedBorrow();

        receivedBorrow.setId((String) borrowData.get("id"));

        Object createdAtObj = borrowData.get("createdAt");
        if (createdAtObj instanceof Timestamp) {
            receivedBorrow.setCreatedAt((Timestamp) createdAtObj);
        }

        receivedBorrow.setReceiveUserId((String) borrowData.get("receiveUserId"));
        receivedBorrow.setApproved((Boolean) borrowData.get("Approved"));
        receivedBorrow.setDeal((Boolean) borrowData.get("deal"));
        receivedBorrow.setAnswer((Boolean) borrowData.get("answer"));
        receivedBorrow.setReturnAnswer((Boolean) borrowData.get("returnAnswer"));

        Map<String, Object> borrowMap = (Map<String, Object>) borrowData.get("borrow");
        if (borrowMap != null) {
            Borrow borrow = createBorrowFromMap(borrowMap);
            receivedBorrow.setBorrow(borrow);
        } else {
            System.out.println("Borrow data is missing or null");
        }

        return receivedBorrow;
    }

    private static Borrow createBorrowFromMap(Map<String, Object> borrowMap) {
        Borrow borrow = new Borrow();
        borrow.setId((String) borrowMap.get("id"));
        borrow.setSenderId((String) borrowMap.get("senderId"));
        borrow.setOpenBorrow((Boolean) borrowMap.get("openBorrow"));
        borrow.setBorrowComplete((Boolean) borrowMap.get("borrowComplete"));
        borrow.setItemName((String) borrowMap.get("itemName"));
        borrow.setDescription((String) borrowMap.get("description"));
        borrow.setCategories((ArrayList<String>) borrowMap.get("categories"));
        borrow.setLat((Double) borrowMap.get("lat"));
        borrow.setLon((Double) borrowMap.get("lon"));
        borrow.setSenderName((String) borrowMap.get("senderName"));

        borrow.setNumOfSending(convertToInt(borrowMap.get("numOfSending")));
        borrow.setNumOfAnswers(convertToInt(borrowMap.get("numOfAnswers")));
        borrow.setRadiusKm(convertToInt(borrowMap.get("radiusKm")));

        return borrow;
    }

    private static Integer convertToInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
}