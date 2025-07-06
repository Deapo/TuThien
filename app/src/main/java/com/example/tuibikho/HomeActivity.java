package com.example.tuibikho;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import com.example.tuibikho.data.AppDatabase;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (BuildConfig.DEBUG) {
            // Initialize database with migration support
            Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .getOpenHelper()
                    .getWritableDatabase();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigate);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.explore) {
                selectedFragment = new AllProductsFragment();
            } else if (itemId == R.id.cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.profile){
                selectedFragment = new ProfileFragment();
            }
            // Add other cases for favourite, account when they are ready

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}
