package com.example.spendwise;

import static java.util.Locale.filter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
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

    ImageButton btnSort;

    SearchView searchView;

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

        btnSort = findViewById(R.id.btnSort);

        searchView = findViewById(R.id.searchView);

        DB =
                new DBHelper(this);

        list =
                new ArrayList<>();

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadHistory();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.getFilter().filter(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return true;
            }
        });

        btnSort.setOnClickListener(v -> {

            PopupMenu popupMenu =
                    new PopupMenu(
                            HistoryActivity.this,
                            btnSort
                    );

            popupMenu.getMenu().add("📅 Today");

            popupMenu.getMenu().add("🕒 Yesterday");

            popupMenu.getMenu().add("Current Month");

            popupMenu.getMenu().add("Previous Month");

            popupMenu.getMenu().add("----------------");

            popupMenu.getMenu().add("January");
            popupMenu.getMenu().add("February");
            popupMenu.getMenu().add("March");
            popupMenu.getMenu().add("April");
            popupMenu.getMenu().add("May");
            popupMenu.getMenu().add("June");
            popupMenu.getMenu().add("July");
            popupMenu.getMenu().add("August");
            popupMenu.getMenu().add("September");
            popupMenu.getMenu().add("October");
            popupMenu.getMenu().add("November");
            popupMenu.getMenu().add("December");

            popupMenu.show();

        });

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
        adapter.notifyDataSetChanged();

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