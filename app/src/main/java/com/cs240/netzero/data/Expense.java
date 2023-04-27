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

    @ColumnInfo(name = "description")
    public final String description;

    @ColumnInfo(name = "price_per_liter")
    public final Double pricePerLiter;

    @ColumnInfo(name = "total_km")
    public final Integer totalKm;

    @ColumnInfo(name = "carId")
    public final long carId;

    public Expense(long expenseId, String title, String type, String date, double spent, String description,
                   Double pricePerLiter, Integer totalKm, long carId) {
        this.expenseId = expenseId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.spent = spent;
        this.description = description;
        this.pricePerLiter = pricePerLiter;
        this.totalKm = totalKm;
        this.carId = carId;
    }

    public String getType() {
        return this.type;
    }

    public double getSpent() {
        return this.spent;
    }

    public int getTotalKm() {
        return this.totalKm;
    }

    public String getDate() {
        return this.date;
    }

    public Double getPricePerLiter() {
        return this.pricePerLiter;
    }
}
