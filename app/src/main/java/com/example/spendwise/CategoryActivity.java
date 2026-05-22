package com.example.spendwise;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    PieChart pieChart;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        pieChart = findViewById(R.id.pieChart);

        DB = new DBHelper(this);

        loadChart();
    }

    private void loadChart() {

        float food = 0;
        float travel = 0;
        float shopping = 0;
        float bills = 0;
        float health = 0;
        float entertainment = 0;
        float other = 0;

        float totalExpense = 0;

        Cursor cursor = DB.getExpenses();

        while (cursor.moveToNext()) {

            String category =
                    cursor.getString(3);

            float amount =
                    Float.parseFloat(
                            cursor.getString(2)
                    );

            totalExpense += amount;

            if(category.contains("Food")) {

                food += amount;

            }
            else if(category.contains("Travel")) {

                travel += amount;

            }
            else if(category.contains("Shopping")) {

                shopping += amount;

            }
            else if(category.contains("Bills")) {

                bills += amount;

            }
            else if(category.contains("Health")) {

                health += amount;

            }
            else if(category.contains("Entertainment")) {

                entertainment += amount;

            }
            else {

                other += amount;

            }

        }

        cursor.close();

        ArrayList<PieEntry> entries =
                new ArrayList<>();

        if(food > 0)
            entries.add(
                    new PieEntry(food, "Food")
            );

        if(travel > 0)
            entries.add(
                    new PieEntry(travel, "Travel")
            );

        if(shopping > 0)
            entries.add(
                    new PieEntry(shopping, "Shopping")
            );

        if(bills > 0)
            entries.add(
                    new PieEntry(bills, "Bills")
            );

        if(health > 0)
            entries.add(
                    new PieEntry(health, "Health")
            );

        if(entertainment > 0)
            entries.add(
                    new PieEntry(
                            entertainment,
                            "Entertainment"
                    )
            );

        if(other > 0)
            entries.add(
                    new PieEntry(
                            other,
                            "Other"
                    )
            );

        PieDataSet dataSet =
                new PieDataSet(
                        entries,
                        ""
                );

        ArrayList<Integer> colors =
                new ArrayList<>();

        colors.add(Color.parseColor("#7B61FF"));
        colors.add(Color.parseColor("#FF8A65"));
        colors.add(Color.parseColor("#4DB6AC"));
        colors.add(Color.parseColor("#FFD54F"));
        colors.add(Color.parseColor("#EF5350"));
        colors.add(Color.parseColor("#42A5F5"));
        colors.add(Color.parseColor("#AB47BC"));

        dataSet.setColors(colors);

        PieData data =
                new PieData(dataSet);

        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12f);

        pieChart.setData(data);

        // Clean UI

        pieChart.getDescription()
                .setEnabled(false);

        pieChart.setUsePercentValues(true);

        pieChart.setDrawEntryLabels(false);

        // Donut Style

        pieChart.setDrawHoleEnabled(true);

        pieChart.setHoleRadius(58f);

        pieChart.setTransparentCircleRadius(63f);

        // Center Text

        pieChart.setCenterText(
                "₹" +
                        (int) totalExpense +
                        "\nTotal Expense"
        );

        pieChart.setCenterTextSize(18f);

        // Legend

        Legend legend =
                pieChart.getLegend();

        legend.setWordWrapEnabled(true);

        legend.setTextSize(13f);

        legend.setFormSize(12f);

        legend.setVerticalAlignment(
                Legend.LegendVerticalAlignment.BOTTOM
        );

        legend.setHorizontalAlignment(
                Legend.LegendHorizontalAlignment.CENTER
        );

        legend.setOrientation(
                Legend.LegendOrientation.HORIZONTAL
        );

        legend.setDrawInside(false);

        pieChart.animateY(1500);

        pieChart.invalidate();
    }
}