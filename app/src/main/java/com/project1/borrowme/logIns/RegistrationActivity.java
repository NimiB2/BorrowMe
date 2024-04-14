package com.project1.borrowme.logIns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project1.borrowme.MainActivity;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.CategorySelectionListener;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.views.CategoriesFragment;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Category> selectedCategories;
    private Map<String, Category> allCategories;
    private String authEmail;
    private String authUserName;
    private String authUid;
    private double authLatitude;
    private double authLongitude;
    private FrameLayout registration_fragment_container;
    private MaterialButton registration_BTN_LOGIN;
    private MaterialButton registration_BTN_back;
    private MaterialTextView registration_MTV_categoryNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        authEmail = intent.getStringExtra("email");
        authUserName = intent.getStringExtra("userName");
        authUid = intent.getStringExtra("uId");
        authLatitude = intent.getDoubleExtra("latitude", 1.1);
        authLongitude = intent.getDoubleExtra("longitude", 1.1);


        findViews();
        initCategories();
        initFragment();
        initViews();
    }


    private void saveUserToFirestore() {
        // create userDetails :
        Map<String, Object> userDetailsMap = new HashMap<>();
        userDetailsMap.put("uName", authUserName);
        userDetailsMap.put("uEmail", authEmail);
        userDetailsMap.put("lat", authLatitude);
        userDetailsMap.put("lon", authLongitude);
        userDetailsMap.put("categories", selectedCategories);

        // create user map:
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", authUid);
        userMap.put("userDetails", userDetailsMap);

        // Get the document reference from Firestore and set the user data
        DocumentReference userDocRef = db.collection("users").document(authUid);
        userDocRef.set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User added successfully!");
                    initializeEmptyCollections(userDocRef);
                    changeActivity(true);
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user", e));
    }

    private void initializeEmptyCollections(DocumentReference userDocRef) {
        String placeholderId = "placeholder";
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("placeholder", true);  // Arbitrary data to allow document creation

        // Initializing each collection with a placeholder document
        userDocRef.collection("borrowMap").document(placeholderId).set(emptyData);
        userDocRef.collection("receivedBorrowMap").document(placeholderId).set(emptyData);
        userDocRef.collection("history").document(placeholderId).set(emptyData);
        userDocRef.collection("massages").document(placeholderId).set(emptyData);
    }

    private void initFragment() {
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        CategorySelectionListener listener = new CategorySelectionListener() {
            @Override
            public void onCategorySelectionUpdated(Map<String, Category> selectedCategories) {
                updateCategories(selectedCategories);
            }
        };

        categoriesFragment.setSelectionListener(listener);


        if (allCategories != null) {
            categoriesFragment.initCategories(allCategories);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.registration_fragment_container, categoriesFragment)
                .commit();
    }

    private void updateCategories(Map<String, Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    private void initCategories() {
        allCategories = new HashMap<>();
        allCategories = CategoriesData.getCategories();
    }

    private void changeActivity(boolean status) {
        Intent intent;
        if (status) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, SignUpActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void initViews() {
        registration_BTN_LOGIN.setOnClickListener(v -> {

            if (selectedCategories != null && selectedCategories.size() >= 3) {
                saveUserToFirestore();

            } else {
                MySignal.getInstance().vibrate(true);
                MySignal.getInstance().toast("You must select at least 3 categories");
            }
        });

        registration_BTN_back.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                currentUser.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("User account", "User account deleted.");
                                changeActivity(false);
                            } else {
                                Log.e("User account", "Failed to delete user account", task.getException());
                            }
                        });
            } else {
                Log.d("User account", "No user is signed in.");
                changeActivity(false);
            }
        });
    }

    private void findViews() {
        registration_fragment_container = findViewById(R.id.registration_fragment_container);
        registration_BTN_LOGIN = findViewById(R.id.registration_BTN_LOGIN);
        registration_BTN_back = findViewById(R.id.registration_BTN_back);
        registration_MTV_categoryNum = findViewById(R.id.MTV_categoryNum);
    }

}