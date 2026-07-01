package com.example.spendwise;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    Button btnAddBalance, btnResetBalance;

    FloatingActionButton fabAdd;

    RecyclerView recyclerExpenses;

    TextView tvBalance,
            tvExpense,
            tvTransactions,
            tvFullHistory;

    DBHelper DB;

    ArrayList<ExpenseModel> list;

    ExpenseAdapter adapter;

    int totalExpense = 0;

    int todayExpense = 0;

    // Stores expense amount
    // at time of adding balance

    int balanceStartExpense = 0;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.fragment_home,
                        container,
                        false
                );

        // Initialize Views

        btnAddBalance =
                view.findViewById(R.id.btnAddBalance);

        btnResetBalance =
                view.findViewById(R.id.btnResetBalance);

        fabAdd =
                view.findViewById(R.id.fabAdd);

        recyclerExpenses =
                view.findViewById(R.id.recyclerExpenses);

        tvBalance =
                view.findViewById(R.id.tvBalance);

        tvExpense =
                view.findViewById(R.id.tvExpense);

        tvTransactions =
                view.findViewById(R.id.tvTransactions);

        tvFullHistory =
                view.findViewById(R.id.tvFullHistory);

        // Database

        DB = new DBHelper(requireContext());
        // Default Balance

        if(DB.getTotalBalance() == 0) {

            DB.insertBalance("5000");

        }
        // ArrayList

        list = new ArrayList<>();

        // RecyclerView

        recyclerExpenses.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // Load Expenses

        loadExpenses();

        // Add Expense

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =
                        new Intent(
                                requireContext(),
                                AddExpenseActivity.class
                        );

                startActivity(intent);

            }
        });

        // Full History

        tvFullHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =
                        new Intent(
                                requireContext(),
                                HistoryActivity.class
                        );

                startActivity(intent);

            }
        });

        // Add Balance

        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog =
                        new Dialog(requireContext());

                dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                );

                dialog.setContentView(
                        R.layout.dialog_add_balance
                );

                EditText etBalance =
                        dialog.findViewById(R.id.etBalance);

                Button btnSaveBalance =
                        dialog.findViewById(R.id.btnSaveBalance);

                Button btnCancel =
                        dialog.findViewById(R.id.btnCancel);

                // Save Balance

                btnSaveBalance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String amount =
                                etBalance.getText().toString().trim();

                        if(amount.isEmpty()) {

                            Toast.makeText(
                                    requireContext(),
                                    "Enter Amount",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                        else {

                            Boolean insert =
                                    DB.insertBalance(amount);

                            if(insert == true) {

                                // Store old expense count

                                balanceStartExpense =
                                        totalExpense;

                                Toast.makeText(
                                        requireContext(),
                                        "Balance Added",
                                        Toast.LENGTH_SHORT
                                ).show();

                                dialog.dismiss();

                                loadExpenses();

                            }

                        }

                    }
                });

                // Cancel Button

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                // Dialog Width

                if(dialog.getWindow() != null) {

                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    dialog.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );

                }

                dialog.show();

            }
        });

        // Reset Balance

        btnResetBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(
                                requireContext()
                        );

                builder.setTitle("Reset Balance");

                builder.setMessage(
                        "Are you sure?"
                );

                builder.setPositiveButton(
                        "Reset",
                        (dialog, which) -> {

                            DB.resetBalance();

                            balanceStartExpense = 0;

                            Toast.makeText(
                                    requireContext(),
                                    "Balance Reset Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadExpenses();

                        });

                builder.setNegativeButton(
                        "Cancel",
                        null
                );

                builder.show();

            }
        });

        return view;
    }

    // Load Expenses

    private void loadExpenses() {

        list.clear();

        totalExpense = 0;

        todayExpense = 0;

        int monthlyExpense = 0;

        Cursor cursor =
                DB.getExpenses();

        SimpleDateFormat sdf =
                new SimpleDateFormat("d/M/yyyy");

        String todayDate =
                sdf.format(new Date());

        SimpleDateFormat monthFormat =
                new SimpleDateFormat("M/yyyy");



        while(cursor.moveToNext()) {

            String title =
                    cursor.getString(1);

            String amount =
                    cursor.getString(2);

            String category =
                    cursor.getString(3);

            String date =
                    cursor.getString(4);

            int expenseAmount =
                    Integer.parseInt(amount);

            // Today's Expense

            if(date.equals(todayDate)) {

                todayExpense += expenseAmount;

            }

            // Monthly Expense

            try {

                String[] parts = date.split("/");

                String expenseMonth = parts[1];

                java.util.Calendar calendar =
                        java.util.Calendar.getInstance();

                String currentMonth =
                        String.valueOf(
                                calendar.get(
                                        java.util.Calendar.MONTH
                                ) + 1
                        );

                if(expenseMonth.equals(currentMonth)) {

                    monthlyExpense += expenseAmount;

                }

            }
            catch (Exception e) {

                e.printStackTrace();

            }

            // Total Expense

            totalExpense += expenseAmount;

            // RecyclerView

            list.add(
                    new ExpenseModel(
                            title,
                            amount,
                            category,
                            date
                    )
            );

        }
        adapter =
                new ExpenseAdapter(
                        list,
                        new ExpenseAdapter.OnExpenseActionListener() {

                            @Override
                            public void onEdit(int position) {

                                ExpenseModel model = list.get(position);

                                Intent intent =
                                        new Intent(
                                                requireContext(),
                                                AddExpenseActivity.class
                                        );

                                intent.putExtra("isEdit", true);

                                intent.putExtra(
                                        "title",
                                        model.getTitle()
                                );

                                intent.putExtra(
                                        "amount",
                                        model.getAmount()
                                );

                                intent.putExtra(
                                        "category",
                                        model.getCategory()
                                );

                                intent.putExtra(
                                        "date",
                                        model.getDate()
                                );

                                startActivity(intent);
                            }

                            @Override
                            public void onDelete(int position) {

                                ExpenseModel model =
                                        list.get(position);

                                android.app.AlertDialog.Builder builder =
                                        new android.app.AlertDialog.Builder(
                                                requireContext()
                                        );

                                builder.setTitle("Delete Expense");

                                builder.setMessage(
                                        "Are you sure you want to delete this expense?"
                                );

                                builder.setPositiveButton(
                                        "Delete",
                                        (dialog, which) -> {

                                            DB.deleteExpense(
                                                    model.getTitle(),
                                                    model.getAmount(),
                                                    model.getDate()
                                            );

                                            Toast.makeText(
                                                    requireContext(),
                                                    "Expense Deleted",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            loadExpenses();

                                        });

                                builder.setNegativeButton(
                                        "Cancel",
                                        null
                                );

                                builder.show();
                            }
                        });

        recyclerExpenses.setAdapter(adapter);

        // Get Current Balance

        int totalBalance =
                DB.getTotalBalance();

        // Calculate only new expenses
        // after balance added

        int newExpense =
                totalExpense - balanceStartExpense;

        if(newExpense < 0) {

            newExpense = 0;

        }

        int remainingBalance =
                totalBalance - newExpense;

        if(remainingBalance < 0) {

            remainingBalance = 0;

        }

        // Dashboard Values

        tvExpense.setText(
                "₹" + monthlyExpense
        );

        tvBalance.setText(
                "₹" + remainingBalance
        );

        tvTransactions.setText(
                "₹" + todayExpense
        );

    }

    @Override
    public void onResume() {
        super.onResume();

        loadExpenses();
    }
}