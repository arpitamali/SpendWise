package com.example.spendwise;

public class ExpenseModel {

    String title, amount, category, date;

    public ExpenseModel(String title,
                        String amount,
                        String category,
                        String date) {

        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }
}