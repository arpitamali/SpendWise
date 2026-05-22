package com.example.spendwise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "SpendWise.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Users Table

        db.execSQL(
                "CREATE TABLE users(" +
                        "email TEXT PRIMARY KEY," +
                        "password TEXT)"
        );

        // Expenses Table

        db.execSQL(
                "CREATE TABLE expenses(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "amount TEXT," +
                        "category TEXT," +
                        "date TEXT)"
        );

        // Balance Table

        db.execSQL(
                "CREATE TABLE balance(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "amount TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS users"
        );

        db.execSQL(
                "DROP TABLE IF EXISTS expenses"
        );

        db.execSQL(
                "DROP TABLE IF EXISTS balance"
        );

        onCreate(db);

    }

    // ==========================
    // REGISTER USER
    // ==========================

    public Boolean insertData(String email,
                              String password) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("email", email);

        values.put("password", password);

        long result =
                db.insert(
                        "users",
                        null,
                        values
                );

        return result != -1;

    }

    // ==========================
    // CHECK EMAIL EXISTS
    // ==========================

    public Boolean checkEmail(String email) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM users WHERE email=?",
                        new String[]{email}
                );

        return cursor.getCount() > 0;

    }

    // ==========================
    // LOGIN CHECK
    // ==========================

    public Boolean checkEmailPassword(String email,
                                      String password) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM users WHERE email=? AND password=?",
                        new String[]{email, password}
                );

        return cursor.getCount() > 0;

    }

    // ==========================
    // INSERT EXPENSE
    // ==========================

    public Boolean insertExpense(String title,
                                 String amount,
                                 String category,
                                 String date) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("title", title);

        values.put("amount", amount);

        values.put("category", category);

        values.put("date", date);

        long result =
                db.insert(
                        "expenses",
                        null,
                        values
                );

        return result != -1;

    }

    // ==========================
    // GET ALL EXPENSES
    // ==========================

    public Cursor getExpenses() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM expenses ORDER BY id DESC",
                null
        );

    }

    // ==========================
    // SET BALANCE
    // ==========================

    public Boolean insertBalance(String amount) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        // Remove old balance

        db.execSQL(
                "DELETE FROM balance"
        );

        ContentValues values =
                new ContentValues();

        values.put(
                "amount",
                amount
        );

        long result =
                db.insert(
                        "balance",
                        null,
                        values
                );

        return result != -1;

    }

    // ==========================
    // GET TOTAL BALANCE
    // ==========================

    public int getTotalBalance() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM balance",
                        null
                );

        int total = 0;

        while(cursor.moveToNext()) {

            total =
                    total +
                            Integer.parseInt(
                                    cursor.getString(1)
                            );

        }

        return total;

    }

    // ==========================
    // DELETE EXPENSE
    // ==========================

    public void deleteExpense(String title,
                              String amount,
                              String date) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.delete(
                "expenses",
                "title=? AND amount=? AND date=?",
                new String[]{
                        title,
                        amount,
                        date
                }
        );

    }

    // ==========================
    // RESET BALANCE
    // ==========================

    public void resetBalance() {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.execSQL(
                "DELETE FROM balance"
        );

    }

    // ==========================
    // RESET ALL EXPENSES
    // ==========================

    public void resetExpenses() {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.execSQL(
                "DELETE FROM expenses"
        );

    }

}