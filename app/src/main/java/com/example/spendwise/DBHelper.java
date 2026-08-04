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

        // Balance Table (यात कायम फक्त एकच row असेल)

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

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS balance");

        onCreate(db);

    }

    // ==========================
    // REGISTER USER
    // ==========================

    public Boolean insertData(String email, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("users", null, values);

        return result != -1;

    }

    // ==========================
    // CHECK EMAIL EXISTS
    // ==========================

    public Boolean checkEmail(String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=?",
                new String[]{email}
        );

        return cursor.getCount() > 0;

    }

    // ==========================
    // LOGIN CHECK
    // ==========================

    public Boolean checkEmailPassword(String email, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
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

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date);

        long result = db.insert("expenses", null, values);

        return result != -1;

    }

    // ==========================
    // UPDATE EXPENSE
    // ==========================

    public Boolean updateExpense(
            String oldTitle,
            String oldAmount,
            String oldDate,
            String newTitle,
            String newAmount,
            String newCategory,
            String newDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("amount", newAmount);
        values.put("category", newCategory);
        values.put("date", newDate);

        int result = db.update(
                "expenses",
                values,
                "title=? AND amount=? AND date=?",
                new String[]{oldTitle, oldAmount, oldDate}
        );

        return result > 0;
    }

    // ==========================
    // GET ALL EXPENSES
    // ==========================

    public Cursor getExpenses() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM expenses ORDER BY id DESC",
                null
        );

    }

    // ==========================
    // DELETE EXPENSE
    // ==========================

    public void deleteExpense(String title, String amount, String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "expenses",
                "title=? AND amount=? AND date=?",
                new String[]{title, amount, date}
        );

    }

    // ==========================
    // RESET ALL EXPENSES (history हवी असेल तर हे कुठेच call करू नका)
    // ==========================

    public void resetExpenses() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM expenses");

    }

    // ============================================
    // BALANCE — कायम एकच "current balance" number
    // ============================================

    // Current balance वाचा

    public int getTotalBalance() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT amount FROM balance LIMIT 1",
                null
        );

        int total = 0;

        if (cursor.moveToFirst()) {

            total = Integer.parseInt(cursor.getString(0));

        }

        cursor.close();

        return total;

    }

    // Balance ला direct एक विशिष्ट number सेट करा (overwrite)

    public Boolean setBalance(int amount) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM balance");

        ContentValues values = new ContentValues();
        values.put("amount", String.valueOf(amount));

        long result = db.insert("balance", null, values);

        return result != -1;

    }

    // सध्याच्या balance मध्ये रक्कम मिळवा (Add Balance साठी)

    public void addToBalance(int amount) {

        int current = getTotalBalance();

        setBalance(current + amount);

    }

    // सध्याच्या balance मधून रक्कम वजा करा (नवीन Expense साठी)

    public void subtractFromBalance(int amount) {

        int current = getTotalBalance();

        setBalance(current - amount);

    }

    // Balance ला ₹0 वर आणा — Expense history ला हात लावत नाही

    public void resetBalance() {

        setBalance(0);

    }

}