package com.example.spendwise;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout layoutEmpty;

    RecyclerView recyclerHistory;

    TextView tvBack;

    DBHelper DB;

    ArrayList<ExpenseModel> list;

    ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerHistory =
                findViewById(R.id.recyclerHistory);

        tvBack =
                findViewById(R.id.tvBack);

        DB =
                new DBHelper(this);

        list =
                new ArrayList<>();

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadHistory();

        tvBack.setOnClickListener(v -> finish());
    }

    private void loadHistory() {

        list.clear();

        Cursor cursor =
                DB.getExpenses();

        while (cursor.moveToNext()) {

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

        cursor.close();

        adapter = new ExpenseAdapter(
                list,
                new ExpenseAdapter.OnExpenseActionListener() {

                    @Override
                    public void onEdit(int position) {
                        ExpenseModel model = list.get(position);

                        Intent intent =
                                new Intent(
                                        HistoryActivity.this,
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

                        ExpenseModel model = list.get(position);

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(
                                        HistoryActivity.this
                                );

                        builder.setTitle("Delete Expense");

                        builder.setMessage(
                                "Are you sure you want to delete this expense?"
                        );

                        builder.setPositiveButton("Delete", (dialog, which) -> {

                            DB.deleteExpense(
                                    model.getTitle(),
                                    model.getAmount(),
                                    model.getDate()
                            );

                            list.remove(position);

                            adapter.notifyItemRemoved(position);

                            Toast.makeText(
                                    HistoryActivity.this,
                                    "Expense Deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });

                        builder.setNegativeButton("Cancel", null);

                        builder.show();
                    }
                }
        );

        recyclerHistory.setAdapter(adapter);

        if(list.isEmpty()){

            recyclerHistory.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);

        }else{

            recyclerHistory.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);

        }
    }
}