package com.project1.borrowme;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project1.borrowme.views.HomeFragment;
import com.project1.borrowme.views.InboxFragment;
import com.project1.borrowme.views.ProfileFragment;

public class MainActivity extends AppCompatActivity {
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
        initFragments();
    }

    private void findViews() {
        homeFragment = new HomeFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();

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

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_FARM_layout, fragment).commit();
    }

}