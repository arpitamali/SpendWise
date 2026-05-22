package com.example.spendwise;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class MonthlyActivity extends AppCompatActivity {

    LineChart lineChart;

    TextView tvHighestMonth,
            tvTotalExpense;

    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);

        lineChart =
                findViewById(R.id.lineChart);

        tvHighestMonth =
                findViewById(R.id.tvHighestMonth);

        tvTotalExpense =
                findViewById(R.id.tvTotalExpense);

        DB =
                new DBHelper(this);

        loadMonthlyChart();
    }

    private void loadMonthlyChart() {

        float[] months =
                new float[12];

        float totalExpense = 0;

        Cursor cursor =
                DB.getExpenses();

        while(cursor.moveToNext()) {

            String date =
                    cursor.getString(4);

            float amount =
                    Float.parseFloat(
                            cursor.getString(2)
                    );

            totalExpense += amount;

            try {

                String[] parts =
                        date.split("/");

                int month =
                        Integer.parseInt(
                                parts[1]
                        );

                months[month - 1] += amount;

            }
            catch (Exception e) {

                e.printStackTrace();

            }
        }

        cursor.close();

        ArrayList<Entry> entries =
                new ArrayList<>();

        for(int i = 0; i < 12; i++) {

            entries.add(
                    new Entry(
                            i,
                            months[i]
                    )
            );
        }

        LineDataSet dataSet =
                new LineDataSet(
                        entries,
                        "Monthly Expense"
                );

        dataSet.setColor(
                Color.parseColor("#7B61FF")
        );

        dataSet.setCircleColor(
                Color.parseColor("#7B61FF")
        );

        dataSet.setLineWidth(3f);

        dataSet.setCircleRadius(5f);

        dataSet.setValueTextSize(11f);

        LineData data =
                new LineData(dataSet);

        lineChart.setData(data);

        lineChart.getDescription()
                .setEnabled(false);

        lineChart.getAxisRight()
                .setEnabled(false);

        lineChart.animateY(1500);

        String[] monthNames = {
                "Jan","Feb","Mar","Apr",
                "May","Jun","Jul","Aug",
                "Sep","Oct","Nov","Dec"
        };

        XAxis xAxis =
                lineChart.getXAxis();

        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(
                        monthNames
                )
        );

        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        xAxis.setGranularity(1f);

        xAxis.setDrawGridLines(false);

        // Highest Month

        float max =
                months[0];

        int maxIndex = 0;

        for(int i = 1; i < 12; i++) {

            if(months[i] > max) {

                max =
                        months[i];

                maxIndex =
                        i;

            }
        }

        tvHighestMonth.setText(
                monthNames[maxIndex]
                        + " - ₹"
                        + (int) max
        );

        tvTotalExpense.setText(
                "₹" + (int) totalExpense
        );

        lineChart.invalidate();
    }
}