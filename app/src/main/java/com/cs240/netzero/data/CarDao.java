package com.cs240.netzero.data;

        import androidx.room.Dao;
        import androidx.room.Delete;
        import androidx.room.Insert;
        import androidx.room.OnConflictStrategy;
        import androidx.room.Query;

        import java.util.List;

@Dao
public interface CarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCar(Car car);

    @Query("SELECT * FROM cars")
    List<Car> getAll();

    @Delete
    void delete(Car car);
}
