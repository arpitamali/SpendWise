package com.example.spendwise;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    Button btnAddBalance, btnResetBalance;

    FloatingActionButton fabAdd;

    RecyclerView recyclerExpenses;

    LinearLayout layoutEmpty, layoutShowBalance;

    // Balance Card + Close Button
    LinearLayout cardBalance;
    ImageButton btnCloseBalance;

    TextView tvBalance,
            tvExpense,
            tvTransactions,
            tvFullHistory,
            tvShowBalance;

    DBHelper DB;

    ArrayList<ExpenseModel> list;

    ExpenseAdapter adapter;

    int totalExpense = 0;

    int todayExpense = 0;

    // SharedPreferences for balance card visibility + first launch
    private static final String PREF_NAME = "SpendWisePrefs";
    private static final String KEY_BALANCE_CARD_VISIBLE = "balance_card_visible";
    private static final String KEY_FIRST_LAUNCH = "is_first_launch";
    SharedPreferences prefs;

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
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

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

        // Balance Card + Close Button
        cardBalance =
                view.findViewById(R.id.cardBalance);

        btnCloseBalance =
                view.findViewById(R.id.btnCloseBalance);

        layoutShowBalance =
                view.findViewById(R.id.layoutShowBalance);

        tvShowBalance =
                view.findViewById(R.id.tvShowBalance);

        // SharedPreferences

        prefs = requireContext().getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        // Show/Hide balance card based on saved state

        boolean isBalanceCardVisible =
                prefs.getBoolean(KEY_BALANCE_CARD_VISIBLE, true);

        if (isBalanceCardVisible) {

            cardBalance.setVisibility(View.VISIBLE);
            layoutShowBalance.setVisibility(View.GONE);

        } else {

            cardBalance.setVisibility(View.GONE);
            layoutShowBalance.setVisibility(View.VISIBLE);

        }

        // Close Balance Card

        btnCloseBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(
                                requireContext()
                        );

                builder.setTitle("Hide Balance Card");

                builder.setMessage(
                        "Are you sure you don't want to track expenses with the balance card?"
                );

                builder.setPositiveButton(
                        "Yes, Hide",
                        (dialog, which) -> {

                            cardBalance.setVisibility(View.GONE);
                            layoutShowBalance.setVisibility(View.VISIBLE);

                            prefs.edit()
                                    .putBoolean(KEY_BALANCE_CARD_VISIBLE, false)
                                    .apply();

                            Toast.makeText(
                                    requireContext(),
                                    "Balance Card Hidden",
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

        // Show Balance Card back (Enable)

        tvShowBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardBalance.setVisibility(View.VISIBLE);
                layoutShowBalance.setVisibility(View.GONE);

                prefs.edit()
                        .putBoolean(KEY_BALANCE_CARD_VISIBLE, true)
                        .apply();

                Toast.makeText(
                        requireContext(),
                        "Balance Card Enabled",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

        // Database

        DB = new DBHelper(requireContext());

        // Default Balance — फक्त पहिल्याच वेळी द्या

        boolean isFirstLaunch =
                prefs.getBoolean(KEY_FIRST_LAUNCH, true);

        if (isFirstLaunch) {

            DB.setBalance(5000);

            prefs.edit()
                    .putBoolean(KEY_FIRST_LAUNCH, false)
                    .apply();

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

                            int amt = Integer.parseInt(amount);

                            // Balance मध्ये रक्कम add करा (replace नाही)
                            DB.addToBalance(amt);

                            Toast.makeText(
                                    requireContext(),
                                    "Balance Added",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();

                            loadExpenses();

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

                            // फक्त balance 0 होतो, Expense history तशीच राहते
                            DB.resetBalance();

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

                                            // Delete झालेली रक्कम परत balance ला मिळते
                                            DB.addToBalance(
                                                    Integer.parseInt(model.getAmount())
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

        if(list.isEmpty()){

            recyclerExpenses.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);

        }
        else{

            recyclerExpenses.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);

        }

        // Get Current Balance — थेट DB मधून, कुठलंही recalculation नाही

        int remainingBalance =
                DB.getTotalBalance();

        // Dashboard Values

        tvExpense.setText(
                "₹" + monthlyExpense
        );

        // Negative असेल तर लाल रंगात "-₹" दाखवा

        if(remainingBalance < 0) {

            tvBalance.setText(
                    "-₹" + Math.abs(remainingBalance)
            );

            tvBalance.setTextColor(
                    android.graphics.Color.parseColor("#FF6B6B")
            );

        }
        else {

            tvBalance.setText(
                    "₹" + remainingBalance
            );

            tvBalance.setTextColor(
                    getResources().getColor(android.R.color.white)
            );

        }

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