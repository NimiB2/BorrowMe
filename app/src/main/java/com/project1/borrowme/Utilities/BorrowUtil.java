package com.project1.borrowme.Utilities;

import android.location.Location;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.models.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BorrowUtil {
    private static final int KILOMETER =1000;
    private static List<String> successfulUserIds = new ArrayList<>();

    public static CompletableFuture<List<String>> findEligibleUsers(String myId, double myLat, double myLon, double radiusKm, ArrayList<String> borrowCategoryList) {
        CompletableFuture<List<String>> result = new CompletableFuture<>();
        List<String> eligibleUserIds = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TheUser otherUser = document.toObject(TheUser.class);
                            if (otherUser != null && !otherUser.getUid().equals(myId)  && otherUser.getUserDetails() != null) {
                                UserDetails otherUserDetails = otherUser.getUserDetails();
                                if (isAnyCategoryMatched(otherUserDetails.getCategories(), borrowCategoryList)) {
                                    if (isUserWithinRadius(myLat, myLon, otherUserDetails.getLat(), otherUserDetails.getLon(), radiusKm)) {
                                        eligibleUserIds.add(document.getId());
                                    }
                                }
                            }
                        }
                        // Update the static list with successful IDs for temporary checking
                        successfulUserIds.clear();
                        successfulUserIds.addAll(eligibleUserIds);

                        result.complete(eligibleUserIds);
                    } else {
                        result.completeExceptionally(task.getException());
                    }
                });

        return result;
    }

    public static List<String> getSuccessfulUserIds() {
        return new ArrayList<>(successfulUserIds);
    }

    private static boolean isUserWithinRadius(double userLat, double userLon, double otherUserLat, double otherUserLon, double radius) {
        int distance = calculateDistance(userLat, userLon, otherUserLat, otherUserLon);
        return distance <= radius;
    }

    private static boolean isAnyCategoryMatched(Map<String, Category> userCategories, ArrayList<String> requiredCategories) {
        for (String category : requiredCategories) {
            if (userCategories.containsKey(category) && userCategories.get(category).getName().equals(category)) {
                return true;
            }
        }
        return false;
    }

    private static int calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        float[] results = new float[1];
        Location.distanceBetween(startLat, startLong, endLat, endLong, results);
        float dis = results[0];
        int km = (int) (dis / KILOMETER);
        return km;
    }

}
