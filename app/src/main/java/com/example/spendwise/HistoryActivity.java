package com.example.spendwise;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout layoutEmpty;

    RecyclerView recyclerHistory;

    TextView tvBack, tvTitle;

    ImageButton btnSort;

    SearchView searchView;

    DBHelper DB;

    ArrayList<ExpenseModel> list;      // currently displayed list (filtered/sorted)
    ArrayList<ExpenseModel> fullList;  // master list, kadhi reset nahi hoto

    ExpenseAdapter adapter;

    // TODO: apla actual DB madhla date format ithe confirm kara
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerHistory = findViewById(R.id.recyclerHistory);

        tvBack = findViewById(R.id.tvBack);

        tvTitle = findViewById(R.id.tvTitle);

        btnSort = findViewById(R.id.btnSort);

        searchView = findViewById(R.id.searchView);

        DB = new DBHelper(this);

        list = new ArrayList<>();

        fullList = new ArrayList<>();

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadHistory();

        tvBack.setOnClickListener(v -> finish());

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

            popupMenu.getMenu().add("All");

            popupMenu.getMenu().add("Today");

            popupMenu.getMenu().add("Yesterday");

            popupMenu.getMenu().add("Current Week");

            popupMenu.getMenu().add("Previous Week");

            popupMenu.getMenu().add("Current Month");

            popupMenu.getMenu().add("Previous Month");

            popupMenu.getMenu().add("By Month");

            popupMenu.getMenu().add("By Year");

            popupMenu.setOnMenuItemClickListener(item -> {

                String selected = item.getTitle().toString();

                switch (selected) {

                    case "All":
                        tvTitle.setText("Expense History");
                        applyFilteredList(new ArrayList<>(fullList));
                        break;

                    case "Today":
                        tvTitle.setText("Expense History • Today");
                        filterToday();
                        break;

                    case "Yesterday":
                        tvTitle.setText("Expense History • Yesterday");
                        filterYesterday();
                        break;

                    case "Current Week":
                        tvTitle.setText("Expense History • Current Week");
                        filterCurrentWeek();
                        break;

                    case "Previous Week":
                        tvTitle.setText("Expense History • Previous Week");
                        filterPreviousWeek();
                        break;

                    case "Current Month":
                        tvTitle.setText("Expense History • Current Month");
                        filterCurrentMonth();
                        break;

                    case "Previous Month":
                        tvTitle.setText("Expense History • Previous Month");
                        filterPreviousMonth();
                        break;

                    case "By Month":
                        showMonthDialog();
                        break;

                    case "By Year":
                        showYearDialog();
                        break;
                }

                return true;

            });

            popupMenu.show();

        });
    }

    private void loadHistory() {

        list.clear();
        fullList.clear();

        Cursor cursor = DB.getExpenses();

        while (cursor.moveToNext()) {

            String title = cursor.getString(1);

            String amount = cursor.getString(2);

            String category = cursor.getString(3);

            String date = cursor.getString(4);

            ExpenseModel model = new ExpenseModel(
                    title,
                    amount,
                    category,
                    date
            );

            list.add(model);
            fullList.add(model);
        }

        cursor.close();

        adapter = new ExpenseAdapter(
                list,
                getExpenseListener()
        );

        adapter.notifyDataSetChanged();

        recyclerHistory.setAdapter(adapter);

        updateEmptyView();
    }

    private ExpenseAdapter.OnExpenseActionListener getExpenseListener() {
        return new ExpenseAdapter.OnExpenseActionListener() {
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
                    fullList.remove(model);

                    adapter.notifyItemRemoved(position);

                    Toast.makeText(
                            HistoryActivity.this,
                            "Expense Deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    updateEmptyView();
                });

                builder.setNegativeButton("Cancel", null);

                builder.show();
            }
        };
    }

    private void updateEmptyView() {
        if (list.isEmpty()) {
            recyclerHistory.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerHistory.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    // ---------------------------------------------------------
    // Filtered list apply karnyasathi common helper
    // ---------------------------------------------------------
    private void applyFilteredList(ArrayList<ExpenseModel> filtered) {

        list.clear();
        list.addAll(filtered);

        adapter = new ExpenseAdapter(
                list,
                getExpenseListener()
        );

        recyclerHistory.setAdapter(adapter);

        updateEmptyView();

        if (filtered.isEmpty()) {
            Toast.makeText(
                    this,
                    "No expenses found for this range",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private void setToStartOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void setToEndOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
    }

    // ---------------------------------------------------------
    // Individual filter methods
    // ---------------------------------------------------------

    private void filterToday() {
        Calendar today = Calendar.getInstance();
        ArrayList<ExpenseModel> filtered = new ArrayList<>();

        for (ExpenseModel model : fullList) {
            Date d = parseDate(model.getDate());
            if (d == null) continue;

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            if (isSameDay(c, today)) {
                filtered.add(model);
            }
        }
        applyFilteredList(filtered);
    }

    private void filterYesterday() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        ArrayList<ExpenseModel> filtered = new ArrayList<>();

        for (ExpenseModel model : fullList) {
            Date d = parseDate(model.getDate());
            if (d == null) continue;

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            if (isSameDay(c, yesterday)) {
                filtered.add(model);
            }
        }
        applyFilteredList(filtered);
    }

    private void filterCurrentWeek() {
        Calendar startOfWeek = Calendar.getInstance();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        setToStartOfDay(startOfWeek);

        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_YEAR, 6);
        setToEndOfDay(endOfWeek);

        filterByRange(startOfWeek, endOfWeek);
    }

    private void filterPreviousWeek() {
        Calendar startOfWeek = Calendar.getInstance();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        startOfWeek.add(Calendar.DAY_OF_YEAR, -7);
        setToStartOfDay(startOfWeek);

        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_YEAR, 6);
        setToEndOfDay(endOfWeek);

        filterByRange(startOfWeek, endOfWeek);
    }

    private void filterCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        filterByMonthYear(month, year);
    }

    private void filterPreviousMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        filterByMonthYear(month, year);
    }

    private void filterByRange(Calendar start, Calendar end) {
        ArrayList<ExpenseModel> filtered = new ArrayList<>();

        for (ExpenseModel model : fullList) {
            Date d = parseDate(model.getDate());
            if (d == null) continue;

            if (!d.before(start.getTime()) && !d.after(end.getTime())) {
                filtered.add(model);
            }
        }
        applyFilteredList(filtered);
    }

    private void filterByMonthYear(int month, int year) {
        ArrayList<ExpenseModel> filtered = new ArrayList<>();

        for (ExpenseModel model : fullList) {
            Date d = parseDate(model.getDate());
            if (d == null) continue;

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            if (c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year) {
                filtered.add(model);
            }
        }
        applyFilteredList(filtered);
    }

    private void filterByYear(int year) {
        ArrayList<ExpenseModel> filtered = new ArrayList<>();

        for (ExpenseModel model : fullList) {
            Date d = parseDate(model.getDate());
            if (d == null) continue;

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            if (c.get(Calendar.YEAR) == year) {
                filtered.add(model);
            }
        }
        applyFilteredList(filtered);
    }

    // ---------------------------------------------------------
    // By Month / By Year dialogs
    // ---------------------------------------------------------

    private void showMonthDialog() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        new AlertDialog.Builder(this)
                .setTitle("Select Month")
                .setItems(months, (dialog, which) -> {
                    tvTitle.setText("Expense History • " + months[which] + " " + currentYear);
                    filterByMonthYear(which, currentYear);
                })
                .show();
    }

    private void showYearDialog() {

        ArrayList<Integer> years = new ArrayList<>();

        for (ExpenseModel model : fullList) {
            Date d = parseDate(model.getDate());
            if (d == null) continue;

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            int y = c.get(Calendar.YEAR);

            if (!years.contains(y)) {
                years.add(y);
            }
        }

        if (years.isEmpty()) {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] yearArray = new String[years.size()];
        for (int i = 0; i < years.size(); i++) {
            yearArray[i] = String.valueOf(years.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Year")
                .setItems(yearArray, (dialog, which) -> {
                    int selectedYear = years.get(which);
                    tvTitle.setText("Expense History • " + selectedYear);
                    filterByYear(selectedYear);
                })
                .show();
    }
}