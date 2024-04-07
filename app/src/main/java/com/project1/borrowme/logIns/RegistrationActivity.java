package com.project1.borrowme.logIns;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.project1.borrowme.adpters.CategoryAdapter;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.CallbackCategory;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.MyUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MyUser user = MyUser.getInstance();
    private String authEmail;
    private String authUserName;
    private String authUid;
    private double authLatitude;
    private double authLongitude;


    private RecyclerView registration_recyclerViewCategories;
    private MaterialButton registration_BTN_LOGIN;
    private MaterialButton registration_BTN_back;
    private MaterialTextView registration_MTV_categoryNum;
    private List<Category> categories;

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
        authLatitude = intent.getDoubleExtra("latitude",1.1);
        authLongitude = intent.getDoubleExtra("longitude",1.1);


        findViews();
        initCategories();
        setAdapter();
        initViews();
    }

    private void initViews() {
        registration_BTN_LOGIN.setOnClickListener(v -> {
            if (user.getCategories().size() >= 3) {
                setUser(authEmail, authUserName, authUid);
                setDb();
                changeActivity(true);
            } else {
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

    private void findViews() {
        registration_recyclerViewCategories = findViewById(R.id.registration_RECYCLER_categoryList);
        registration_BTN_LOGIN = findViewById(R.id.registration_BTN_LOGIN);
        registration_BTN_back = findViewById(R.id.registration_BTN_back);
        registration_MTV_categoryNum = findViewById(R.id.registration_MTV_categoryNum);
    }

    private void setUser(String authEmail, String authUserName, String authUid) {
        user.setUid(authUid);
        user.setuEmail(authEmail);
        user.setuName(authUserName);
    }

    private void setDb() {
        CollectionReference reference = db.collection("users");
        DocumentReference documentReference = reference.document(authUid);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("categories", user.getCategories());
        userMap.put("uid", user.getUid());
        userMap.put("uEmail", user.getuEmail());
        userMap.put("lan", authLongitude);
        userMap.put("lat", authLatitude);
        userMap.put("uName", user.getuName());

        documentReference.set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added successfully!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user", e));
    }

    private void initCategories() {
        categories = new ArrayList<>();
        categories = CategoriesData.getCategories();
    }

    private void setAdapter() {
        CallbackCategory callbackCategory = new CallbackCategory() {
            @Override
            public void addCategory(Category category) {
                user.addCategory(category);
                updateNumCategoryText(user.getCategories().size());
            }

            @Override
            public void removeCategory(Category category) {
                user.removeCategory(category.getName());
                updateNumCategoryText(user.getCategories().size());
            }
        };

        CategoryAdapter adapter = new CategoryAdapter(categories, this, callbackCategory);
        int numOfCols = 3;
        registration_recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, numOfCols));
        registration_recyclerViewCategories.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private void updateNumCategoryText(int size) {
        registration_MTV_categoryNum.setText(String.format("%d", size));
    }
}