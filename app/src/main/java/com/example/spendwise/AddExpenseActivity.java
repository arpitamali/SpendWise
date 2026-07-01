package com.example.spendwise;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etTitle, etAmount, etDate;
    Spinner spCategory;
    Button btnSaveExpense;

    DBHelper DB;
    boolean isEdit = false;

    String oldTitle;
    String oldAmount;
    String oldDate;

    String[] categories = {
            "Food 🍔",
            "Grocery 🥦",
            "Fuel ⛽",
            "Travel 🚗",
            "Shopping 🛍",
            "Bills 💡",
            "Health 🏥",
            "Education 🎓",
            "Beauty 💄",
            "Investment 💰",
            "Entertainment 🎬",
            "Luxury 💎",
            "Household 🏠",
            "Other 📦"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize Views

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);

        spCategory = findViewById(R.id.spCategory);

        btnSaveExpense =
                findViewById(R.id.btnSaveExpense);

        // Database

        DB = new DBHelper(this);

        // Spinner Setup

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories
                );

        spCategory.setAdapter(adapter);

        // Edit Mode

        Intent intent = getIntent();

        isEdit = intent.getBooleanExtra(
                "isEdit",
                false
        );

        if (isEdit) {

            btnSaveExpense.setText(
                    "Update Expense"
            );

            oldTitle =
                    intent.getStringExtra("title");

            oldAmount =
                    intent.getStringExtra("amount");

            oldDate =
                    intent.getStringExtra("date");

            String oldCategory =
                    intent.getStringExtra("category");

            etTitle.setText(oldTitle);

            etAmount.setText(oldAmount);

            etDate.setText(oldDate);

            for(int i = 0; i < categories.length; i++){

                if(categories[i].equals(oldCategory)){

                    spCategory.setSelection(i);

                    break;
                }
            }
        }

        // Today's Date Auto

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(Calendar.YEAR);

        int month =
                calendar.get(Calendar.MONTH);

        int day =
                calendar.get(Calendar.DAY_OF_MONTH);

        String todayDate =
                day + "/" + (month + 1) + "/" + year;

        etDate.setText(todayDate);

        // Date Picker

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar =
                        Calendar.getInstance();

                int year =
                        calendar.get(Calendar.YEAR);

                int month =
                        calendar.get(Calendar.MONTH);

                int day =
                        calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(
                                AddExpenseActivity.this,

                                (view, year1, month1, dayOfMonth) -> {

                                    etDate.setText(
                                            dayOfMonth + "/" +
                                                    (month1 + 1) + "/" +
                                                    year1
                                    );

                                },

                                year,
                                month,
                                day
                        );

                datePickerDialog.show();

            }
        });

        // Save Expense

        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title =
                        etTitle.getText().toString().trim();

                String amount =
                        etAmount.getText().toString().trim();

                String category =
                        spCategory.getSelectedItem().toString();

                String date =
                        etDate.getText().toString().trim();

                // Validation
                if(title.isEmpty()){
                    title = category;
                }

                if(amount.isEmpty()
                        || date.isEmpty()) {

                    Toast.makeText(
                            AddExpenseActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                    ).show();

                }
                else {

                    Boolean success;

                    if(isEdit){

                        success =
                                DB.updateExpense(
                                        oldTitle,
                                        oldAmount,
                                        oldDate,
                                        title,
                                        amount,
                                        category,
                                        date
                                );

                    }else{

                        success =
                                DB.insertExpense(
                                        title,
                                        amount,
                                        category,
                                        date
                                );
                    }

                    if(success) {

                        Toast.makeText(
                                AddExpenseActivity.this,
                                isEdit
                                        ? "Expense Updated Successfully"
                                        : "Expense Saved Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent =
                                new Intent(
                                        AddExpenseActivity.this,
                                        MainActivity.class
                                );

                        startActivity(intent);

                        finish();

                    }
                    else {

                        Toast.makeText(
                                AddExpenseActivity.this,
                                "Failed to Save Expense",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                }

            }
        });

    }
}