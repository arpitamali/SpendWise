package com.example.spendwise;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    PieChart pieChart;
    DBHelper DB;

    TextView tvBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        pieChart = findViewById(R.id.pieChart);
        tvBreakdown = findViewById(R.id.tvBreakdown);
        DB = new DBHelper(this);

        loadChart();
    }

    private void loadChart() {
        float food = 0;
        float grocery = 0;
        float fuel = 0;
        float travel = 0;
        float shopping = 0;
        float bills = 0;
        float health = 0;
        float education = 0;
        float beauty = 0;
        float investment = 0;
        float entertainment = 0;
        float luxury = 0;
        float household = 0;
        float other = 0;
        float totalExpense = 0;

        Cursor cursor = DB.getExpenses();

        while (cursor.moveToNext()) {

            String category =
                    cursor.getString(3).trim();

            float amount =
                    Float.parseFloat(
                            cursor.getString(2)
                    );

            totalExpense += amount;

            if(category.contains("Food")) {

                food += amount;

            }
            else if(category.contains("Grocery")) {

                grocery += amount;

            }
            else if(category.contains("Fuel")) {

                fuel += amount;

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
            else if(category.contains("Education")) {

                education += amount;

            }
            else if(category.contains("Beauty")) {

                beauty += amount;

            }
            else if(category.contains("Investment")) {

                investment += amount;

            }
            else if(category.contains("Entertainment")) {

                entertainment += amount;

            }
            else if(category.contains("Luxury")) {

                luxury += amount;

            }
            else if(category.contains("Household")) {

                household += amount;

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
                    new PieEntry(
                            food,
                            "Food ₹" + (int) food
                    )
            );

        if(grocery > 0)
            entries.add(
                    new PieEntry(
                            grocery,
                            "Grocery ₹" + (int) grocery
                    )
            );

        if(fuel > 0)
            entries.add(
                    new PieEntry(
                            fuel,
                            "Fuel ₹" + (int) fuel
                    )
            );

        if(travel > 0)
            entries.add(
                    new PieEntry(
                            travel,
                            "Travel ₹" + (int) travel
                    )
            );

        if(shopping > 0)
            entries.add(
                    new PieEntry(
                            shopping,
                            "Shopping ₹" + (int) shopping
                    )
            );

        if(bills > 0)
            entries.add(
                    new PieEntry(
                            bills,
                            "Bills ₹" + (int) bills
                    )
            );

        if(health > 0)
            entries.add(
                    new PieEntry(
                            health,
                            "Health ₹" + (int) health
                    )
            );

        if(education > 0)
            entries.add(
                    new PieEntry(
                            education,
                            "Education ₹" + (int) education
                    )
            );

        if(beauty > 0)
            entries.add(
                    new PieEntry(
                            beauty,
                            "Beauty ₹" + (int) beauty
                    )
            );

        if(investment > 0)
            entries.add(
                    new PieEntry(
                            investment,
                            "Investment ₹" + (int) investment
                    )
            );

        if(entertainment > 0)
            entries.add(
                    new PieEntry(
                            entertainment,
                            "Entertainment ₹" + (int) entertainment
                    )
            );

        if(luxury > 0)
            entries.add(
                    new PieEntry(
                            luxury,
                            "Luxury ₹" + (int) luxury
                    )
            );

        if(household > 0)
            entries.add(
                    new PieEntry(
                            household,
                            "Household ₹" + (int) household
                    )
            );

        if(other > 0)
            entries.add(
                    new PieEntry(
                            other,
                            "Other ₹" + (int) other
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
        colors.add(Color.parseColor("#26A69A"));
        colors.add(Color.parseColor("#EC407A"));
        colors.add(Color.parseColor("#8BC34A"));
        colors.add(Color.parseColor("#FF7043"));
        colors.add(Color.parseColor("#5C6BC0"));
        colors.add(Color.parseColor("#9CCC65"));
        colors.add(Color.parseColor("#78909C"));
        dataSet.setColors(colors);

        PieData data =
                new PieData(dataSet);

        data.setValueFormatter(
                new PercentFormatter(pieChart)
        );

        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(10f);

        pieChart.setData(data);

        // Clean UI

        pieChart.getDescription()
                .setEnabled(false);

        pieChart.setUsePercentValues(true);

        pieChart.setDrawEntryLabels(false);

        // Donut Style

        pieChart.setDrawHoleEnabled(true);

        pieChart.setHoleRadius(65f);

        pieChart.setTransparentCircleRadius(70f);

        // Center Text
        pieChart.setCenterText(
                "₹" +
                        (int) totalExpense +
                        "\nSpent"
        );

        pieChart.setCenterTextSize(20f);


        pieChart.setCenterTextSize(18f);

        // Legend

        Legend legend =
                pieChart.getLegend();

        legend.setWordWrapEnabled(true);

        legend.setTextSize(11f);

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
        String breakdown = "";

        if(food > 0)
            breakdown += "🍔 Food          ₹" + (int) food + "\n";

        if(grocery > 0)
            breakdown += "🛒 Grocery      ₹" + (int) grocery + "\n";

        if(fuel > 0)
            breakdown += "⛽ Fuel            ₹" + (int) fuel + "\n";

        if(travel > 0)
            breakdown += "🚗 Travel        ₹" + (int) travel + "\n";

        if(shopping > 0)
            breakdown += "🛍 Shopping    ₹" + (int) shopping + "\n";

        if(bills > 0)
            breakdown += "💡 Bills           ₹" + (int) bills + "\n";

        if(health > 0)
            breakdown += "🏥 Health       ₹" + (int) health + "\n";

        if(education > 0)
            breakdown += "📚 Education   ₹" + (int) education + "\n";

        if(beauty > 0)
            breakdown += "💄 Beauty      ₹" + (int) beauty + "\n";

        if(investment > 0)
            breakdown += "💰 Investment ₹" + (int) investment + "\n";

        if(entertainment > 0)
            breakdown += "🎬 Entertainment ₹" + (int) entertainment + "\n";

        if(luxury > 0)
            breakdown += "✨ Luxury      ₹" + (int) luxury + "\n";

        if(household > 0)
            breakdown += "🏠 Household  ₹" + (int) household + "\n";

        if(other > 0)
            breakdown += "📦 Other        ₹" + (int) other + "\n";

        tvBreakdown.setText(breakdown);
        pieChart.invalidate();
    }
}