package com.example.spendwise;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerHistory;

    TextView tvBack;

    DBHelper DB;

    ArrayList<ExpenseModel> list;

    ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize Views

        recyclerHistory =
                findViewById(R.id.recyclerHistory);

        tvBack =
                findViewById(R.id.tvBack);

        // Database

        DB = new DBHelper(this);

        // ArrayList

        list = new ArrayList<>();

        // RecyclerView

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Load Data

        loadHistory();

        // Back Button

        tvBack.setOnClickListener(v -> finish());

    }

    // Load History

    private void loadHistory() {

        Cursor cursor =
                DB.getExpenses();

        while(cursor.moveToNext()) {

            String title =
                    cursor.getString(1);

            String amount =
                    cursor.getString(2);

            String category =
                    cursor.getString(3);

            String date =
                    cursor.getString(4);

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
                        position -> {

                            ExpenseModel model =
                                    list.get(position);

                            DB.deleteExpense(
                                    model.getTitle(),
                                    model.getAmount(),
                                    model.getDate()
                            );

                            list.remove(position);

                            adapter.notifyItemRemoved(position);

                        });

        recyclerHistory.setAdapter(adapter);

    }
}