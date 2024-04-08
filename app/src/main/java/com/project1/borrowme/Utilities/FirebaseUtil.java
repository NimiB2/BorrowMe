package com.project1.borrowme.Utilities;

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
}
