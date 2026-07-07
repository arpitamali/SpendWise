package com.example.spendwise;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DBHelper DB = new DBHelper(context);

        boolean hasExpenseToday = false;

        String today = new SimpleDateFormat(
                "d/M/yyyy",
                Locale.getDefault()
        ).format(new Date());

        Cursor cursor = DB.getExpenses();

        while (cursor.moveToNext()) {

            if (today.equals(cursor.getString(4))) {

                hasExpenseToday = true;
                break;
            }
        }

        cursor.close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            "expense_channel",
                            "Expense Reminder",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription("Daily Expense Reminder");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }

        Intent openIntent =
                new Intent(context, AddExpenseActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        String title;
        String message;
        String bigMessage;

        if (hasExpenseToday) {

            title = "🤔 Expense Check";

            message = "Is that all you spent today?";

            bigMessage =
                    "You've already recorded today's expenses.\n\n"
                            + "Is that all you spent today?\n\n"
                            + "Tap here if you have more expenses to add.";

        } else {

            title = "💰 SpendWise Reminder";

            message = "Don't forget to record today's expenses.";

            bigMessage =
                    "Don't forget to record today's expenses.\n\n"
                            + "Tap to open SpendWise and save your expense.";
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        "expense_channel"
                )
                        .setSmallIcon(R.drawable.outline_notifications_active_24)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(bigMessage)
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        NotificationManagerCompat.from(context)
                .notify(101, builder.build());
    }
}