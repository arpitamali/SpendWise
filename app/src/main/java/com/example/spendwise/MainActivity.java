package com.example.spendwise;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "LoginPrefs",
                        MODE_PRIVATE
                );

        boolean darkMode =
                sharedPreferences.getBoolean(
                        "darkMode",
                        false
                );

        if(darkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        }
        else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation =
                findViewById(R.id.bottomNavigation);

        // Default Fragment

        loadFragment(new HomeFragment());

        // Bottom Navigation Click

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if(item.getItemId() == R.id.home) {

                fragment = new HomeFragment();

            }
            else if(item.getItemId() == R.id.analytics) {

                fragment = new AnalyticsFragment();

            }
            else if(item.getItemId() == R.id.profile) {

                fragment = new ProfileFragment();

            }

            return loadFragment(fragment);

        });

    }

    // Load Fragment Function

    private boolean loadFragment(Fragment fragment) {

        if(fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();

            return true;

        }

        return false;

    }
}