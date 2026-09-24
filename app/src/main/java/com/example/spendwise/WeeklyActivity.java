package com.example.spendwise;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WeeklyActivity extends AppCompatActivity {

    BarChart barChart;
    TextView tvHighestDay;

    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        barChart = findViewById(R.id.barChart);
        tvHighestDay = findViewById(R.id.tvHighestDay);

        DB = new DBHelper(this);

        loadWeeklyChart();
    }

    private void loadWeeklyChart() {

        float mon = 0;
        float tue = 0;
        float wed = 0;
        float thu = 0;
        float fri = 0;
        float sat = 0;
        float sun = 0;

        Calendar today = Calendar.getInstance();

        Calendar weekStart = (Calendar) today.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);
        weekStart.set(Calendar.MILLISECOND, 0);

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_MONTH, 6);
        weekEnd.set(Calendar.HOUR_OF_DAY, 23);
        weekEnd.set(Calendar.MINUTE, 59);
        weekEnd.set(Calendar.SECOND, 59);

        Cursor cursor = DB.getExpenses();

        while (cursor.moveToNext()) {

            String date = cursor.getString(4);
            float amount = Float.parseFloat(cursor.getString(2));

            try {

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                "d/M/yyyy",
                                Locale.getDefault()
                        );

                Calendar calendar =
                        Calendar.getInstance();

                calendar.setTime(
                        sdf.parse(date)
                );
                Calendar currentCal =
                        Calendar.getInstance();

                currentCal.setFirstDayOfWeek(
                        Calendar.MONDAY
                );

                calendar.setFirstDayOfWeek(
                        Calendar.MONDAY
                );
                Calendar expenseCal =
                        (Calendar) calendar.clone();


                int expenseWeek =
                        expenseCal.get(
                                Calendar.WEEK_OF_YEAR
                        );

                int currentWeek =
                        currentCal.get(
                                Calendar.WEEK_OF_YEAR
                        );

                if(expenseWeek != currentWeek) {
                    continue;
                }

                int expenseYear =
                        expenseCal.get(
                                Calendar.YEAR
                        );

                int currentYear =
                        currentCal.get(
                                Calendar.YEAR
                        );

                if(expenseWeek != currentWeek
                        || expenseYear != currentYear) {

                    continue;

                }

                int dayOfWeek =
                        calendar.get(
                                Calendar.DAY_OF_WEEK
                        );

                switch (dayOfWeek) {

                    case Calendar.MONDAY:
                        mon += amount;
                        break;

                    case Calendar.TUESDAY:
                        tue += amount;
                        break;

                    case Calendar.WEDNESDAY:
                        wed += amount;
                        break;

                    case Calendar.THURSDAY:
                        thu += amount;
                        break;

                    case Calendar.FRIDAY:
                        fri += amount;
                        break;

                    case Calendar.SATURDAY:
                        sat += amount;
                        break;

                    case Calendar.SUNDAY:
                        sun += amount;
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();

        ArrayList<BarEntry> entries =
                new ArrayList<>();

        entries.add(new BarEntry(0, mon));
        entries.add(new BarEntry(1, tue));
        entries.add(new BarEntry(2, wed));
        entries.add(new BarEntry(3, thu));
        entries.add(new BarEntry(4, fri));
        entries.add(new BarEntry(5, sat));
        entries.add(new BarEntry(6, sun));

        BarDataSet dataSet =
                new BarDataSet(
                        entries,
                        "Weekly Expenses"
                );

        dataSet.setColor(
                Color.parseColor("#7B61FF")
        );

        dataSet.setValueTextSize(12f);

        BarData data =
                new BarData(dataSet);

        data.setBarWidth(0.5f);

        barChart.setData(data);

        barChart.getLegend().setEnabled(false);

        barChart.getDescription()
                .setEnabled(false);

        barChart.getAxisRight()
                .setEnabled(false);

        barChart.animateY(1500);

        String[] days = {
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat",
                "Sun"
        };

        XAxis xAxis =
                barChart.getXAxis();

        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(days)
        );

        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        xAxis.setGranularity(1f);

        xAxis.setDrawGridLines(false);

        // Highest Day

        float max = mon;
        String highestDay = "Monday";

        if (tue > max) {
            max = tue;
            highestDay = "Tuesday";
        }

        if (wed > max) {
            max = wed;
            highestDay = "Wednesday";
        }

        if (thu > max) {
            max = thu;
            highestDay = "Thursday";
        }

        if (fri > max) {
            max = fri;
            highestDay = "Friday";
        }

        if (sat > max) {
            max = sat;
            highestDay = "Saturday";
        }

        if (sun > max) {
            max = sun;
            highestDay = "Sunday";
        }

        tvHighestDay.setText(
                highestDay + " - ₹" + (int) max
        );

        barChart.invalidate();
    }
}