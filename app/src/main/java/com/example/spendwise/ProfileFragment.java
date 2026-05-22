package com.example.spendwise;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
    TextView tvEmail;

    Button btnLogout;

    LinearLayout layoutReset,
            layoutAbout;

    SharedPreferences sharedPreferences;

    DBHelper DB;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.fragment_profile,
                        container,
                        false
                );

        // Initialize Views
        tvEmail =
                view.findViewById(R.id.tvEmail);

        btnLogout =
                view.findViewById(R.id.btnLogout);

        layoutReset =
                view.findViewById(R.id.layoutReset);

        layoutAbout =
                view.findViewById(R.id.layoutAbout);

        // SharedPreferences

        sharedPreferences =
                requireActivity().getSharedPreferences(
                        "LoginPrefs",
                        requireActivity().MODE_PRIVATE
                );

        // Database

        DB = new DBHelper(requireContext());

        // Show Email

        String email =
                sharedPreferences.getString(
                        "email",
                        "No Email"
                );

        tvEmail.setText(email);

        // Logout

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor =
                        sharedPreferences.edit();

                editor.clear();

                editor.apply();

                Toast.makeText(
                        requireContext(),
                        "Logged Out",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent =
                        new Intent(
                                requireContext(),
                                LoginActivity.class
                        );

                startActivity(intent);

                requireActivity().finish();

            }
        });

        // Reset Expenses

        layoutReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(
                                requireContext()
                        );

                builder.setTitle("Reset Expenses");

                builder.setMessage(
                        "Delete all expenses?"
                );

                builder.setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            DB.resetExpenses();

                            Toast.makeText(
                                    requireContext(),
                                    "All Expenses Deleted",
                                    Toast.LENGTH_SHORT
                            ).show();

                        });

                builder.setNegativeButton(
                        "Cancel",
                        null
                );

                builder.show();

            }
        });

        // About App

        layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(
                                requireContext()
                        );

                builder.setTitle("About SpendWise");

                builder.setMessage(
                        "SpendWise\n\n" +
                                "Modern Expense Tracker App 💰\n\n" +
                                "Version 1.0"
                );

                builder.setPositiveButton(
                        "OK",
                        null
                );

                builder.show();

            }
        });

        return view;
    }
}