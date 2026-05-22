package com.example.spendwise;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;

public class AnalyticsFragment extends Fragment {

    LinearLayout layoutCategory,
            layoutWeekly,
            layoutMonthly;

    public AnalyticsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.fragment_analytics,
                        container,
                        false
                );

        // Initialize Views

        layoutCategory =
                view.findViewById(R.id.layoutCategory);

        layoutWeekly =
                view.findViewById(R.id.layoutWeekly);

        layoutMonthly =
                view.findViewById(R.id.layoutMonthly);

        // Category Wise

        layoutCategory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            CategoryActivity.class
                    );

            startActivity(intent);

        });

        // Weekly Comparison

        layoutWeekly.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            WeeklyActivity.class
                    );

            startActivity(intent);

        });

        // Monthly Comparison

        layoutMonthly.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            MonthlyActivity.class
                    );

            startActivity(intent);

        });

        return view;
    }
}