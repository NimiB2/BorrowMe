package com.project1.borrowme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.logIns.LoginActivity;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.models.UserDetails;
import com.project1.borrowme.screens.HomeFragment;
import com.project1.borrowme.screens.InboxFragment;
import com.project1.borrowme.screens.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private TheUser theUser= TheUser.getInstance();;

    private AppCompatImageButton main_BTN_logout;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private MenuItem navigation_home;
    private MenuItem navigation_profile;
    private MenuItem navigation_inbox;
    private HomeFragment homeFragment;
    private InboxFragment inboxFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViews();
        getTheUser();
        initViews();
        initFragments();
    }

    private void getTheUser() {
        FirebaseUtil.currentUserFirestore().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    TheUser fetchedUser = task.getResult().toObject(TheUser.class);
                    setUser(fetchedUser,task.getResult());
                }
            }
        });
    }

    private void setUser(TheUser fetchedUser, DocumentSnapshot document) {
        if (fetchedUser != null && fetchedUser.getUserDetails() != null) {
            UserDetails fetchedUserDetails =fetchedUser.getUserDetails();

            if (theUser.getUserDetails() == null) {
                theUser.setUserDetails(new UserDetails());
            }
            theUser.setUid(fetchedUser.getUid());
            theUser.setBorrowMap(fetchedUser.getBorrowMap());
            theUser.setReceivedBorrowMap(fetchedUser.getReceivedBorrowMap());
            theUser.setHistory(fetchedUser.getHistory());
            theUser.setMassages(fetchedUser.getMassages());

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

    private void fetchAndSetUserProfileImage(UserDetails userDetails) {
        StorageReference profilePicRef = FirebaseUtil.getCurrentProfilePicStorageRef();
        profilePicRef.getDownloadUrl()
                .addOnSuccessListener(userDetails::setProfileImageUri)
                .addOnFailureListener(e -> {
                    userDetails.setProfileImageUri(null);
                });
    }

    private void initViews() {
        main_BTN_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void logOut() {
        //theUser.resetUser();
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }



    private void findViews() {
        homeFragment = new HomeFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();
        main_BTN_logout = findViewById(R.id.main_BTN_logout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.main_FARM_layout);
    }

    private void initFragments() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.navigation_home) {
                    replaceFragment(new HomeFragment());
                } else if (itemId == R.id.navigation_profile) {
                    replaceFragment(new ProfileFragment());

                } else if (itemId == R.id.navigation_inbox) {
                    replaceFragment(new InboxFragment());
                }

                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_FARM_layout, fragment).commit();
    }
}