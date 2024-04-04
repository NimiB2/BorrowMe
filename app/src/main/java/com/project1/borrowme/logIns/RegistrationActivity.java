package com.project1.borrowme.logIns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project1.borrowme.MainActivity;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.adpters.CategoryAdapter;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.Callback_Category;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.MyUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MyUser user = MyUser.getInstance();
    private Callback_Category callbackCategory;
    private final int numOfCols = 3;

    private String authEmail;
    private String authPassword;
    private String authUserName;
    private String authUid;

    private RecyclerView registration_recyclerViewCategories;
    private MaterialButton registration_BTN_LOGIN;
    private MaterialButton registration_BTN_back;
    private MaterialTextView registration_MTV_categoryNum;
    private CategoryAdapter adapter;
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
        authPassword = intent.getStringExtra("password");
        authUserName = intent.getStringExtra("userName");
        authUid= intent.getStringExtra("uId");

        findViews();
        initCategories();
        setAdapter();
        initViews();
    }

    private void initViews() {
        registration_BTN_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getCategories().size() >= 3) {
                    setUser(authEmail,authUserName,authUid);
                    setDb();
                    changeActivity(true);
                } else {
                    MySignal.getInstance().toast("You must select at least 3 categories");
                }
            }
        });

        registration_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


                if (currentUser != null) {
                    currentUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("User account", "User account deleted.");
                                        changeActivity(false);
                                    } else {
                                        Log.e("User account", "Failed to delete user account", task.getException());
                                    }
                                }
                            });
                } else {
                    Log.d("User account", "No user is signed in.");
                    changeActivity(false);
                }
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
        userMap.put("lan", user.getLan());
        userMap.put("lat", user.getLat());
        userMap.put("uName", user.getuName());

        documentReference.set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "User added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding user", e);
                    }
                });
    }

    private void initCategories() {
        categories = new ArrayList<>();
        categories = CategoriesData.getCategories();
    }

    private void setAdapter() {
        callbackCategory = new Callback_Category() {
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

        adapter = new CategoryAdapter(categories, this, callbackCategory);
        registration_recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, numOfCols));
        registration_recyclerViewCategories.setAdapter(adapter);
    }

    private void updateNumCategoryText(int size) {
        registration_MTV_categoryNum.setText(size + "");
    }

}