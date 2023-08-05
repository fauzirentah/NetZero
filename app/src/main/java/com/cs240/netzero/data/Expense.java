package com.cs240.netzero.data;

        import androidx.room.ColumnInfo;
        import androidx.room.Entity;
        import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public final long expenseId;

    @ColumnInfo(name = "title")
    public final String title;

    @ColumnInfo(name = "type")
    public final String type;

    @ColumnInfo(name = "date")
    public final String date;

    @ColumnInfo(name = "spent")
    public final double spent;

    @ColumnInfo(name = "co2e")
    public final String co2e;

    @ColumnInfo(name = "duration")
    public final Double duration;

    @ColumnInfo(name = "total_tree")
    public final Double totalTree;

    @ColumnInfo(name = "carId")
    public final long carId;

    public Expense(long expenseId, String title, String type, String date, double spent, String co2e,
                   Double duration, Double totalTree, long carId) {
        this.expenseId = expenseId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.spent = spent;
        this.co2e = co2e;
        this.duration = duration;
        this.totalTree = totalTree;
        this.carId = carId;
    }

    public long getExpenseId() {
        return this.expenseId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() { return this.co2e; }

    public String getType() {
        return this.type;
    }

    public double getSpent() {
        return this.spent;
    }

    public double getTotalKm() {
        return this.totalTree;
    }

    public String getDate() {
        return this.date;
    }

    public Double getPricePerLiter() {
        return this.duration;
    }
}
