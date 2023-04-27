package com.cs240.netzero.data;

        import androidx.room.ColumnInfo;
        import androidx.room.Entity;
        import androidx.room.PrimaryKey;

@Entity(tableName = "cars")
public class Car {
    @PrimaryKey(autoGenerate = true)
    public int carId;

    @ColumnInfo(name = "brand")
    public String brand;

    @ColumnInfo(name = "model")
    public String model;

    @ColumnInfo(name = "fuel_type")
    public String fuelType;

    @ColumnInfo(name = "euro_category")
    public String euroCategory;

    public Car(int carId, String brand, String model, String fuelType, String euroCategory) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.fuelType = fuelType;
        this.euroCategory = euroCategory;
    }
/*
    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getEuroCategory() {
        return euroCategory;
    }

    public void setEuroCategory(String euroCategory) {
        this.euroCategory = euroCategory;
    }
 */
}
