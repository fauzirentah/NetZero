package com.cs240.netzero.data;

public class ExpenseView {
    public final long expenseId;
    public final int iconId;
    public final String title;
    public final double spent;

    public ExpenseView(long expenseId, int iconId, String title, double spent) {
        this.expenseId = expenseId;
        this.iconId = iconId;
        this.title = title;
        this.spent = spent;
    }

    public int getIconId() {
        return this.iconId;
    }
    public long getExpenseId() {
        return this.expenseId;
    }
    public String getTitle() {
        return this.title;
    }
    public double getSpent() {
        return this.spent;
    }
}
