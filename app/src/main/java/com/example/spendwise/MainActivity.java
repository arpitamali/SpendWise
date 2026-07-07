package com.example.spendwise;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.AlarmManager;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;


import java.util.Calendar;

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(ALARM_SERVICE);

            if (!alarmManager.canScheduleExactAlarms()) {

                Intent intent =
                        new Intent(
                                android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        );

                startActivity(intent);
            }
        }
        setDailyReminder();

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
    private void setDailyReminder() {

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent =
                new Intent(
                        this,
                        ExpenseReminderReceiver.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        100,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.HOUR_OF_DAY,
                21
        );

        calendar.set(
                Calendar.MINUTE,
                30
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        if (calendar.getTimeInMillis()
                <= System.currentTimeMillis()) {

            calendar.add(
                    Calendar.DAY_OF_MONTH,
                    1
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

        } else {

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}