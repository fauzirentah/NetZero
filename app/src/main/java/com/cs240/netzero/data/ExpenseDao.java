package com.cs240.netzero.data;

        import androidx.room.Dao;
        import androidx.room.Delete;
        import androidx.room.Insert;
        import androidx.room.OnConflictStrategy;
        import androidx.room.Query;

        import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpense(Expense expense);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpenses(List<Expense> expenses);

    @Query("SELECT * FROM expenses")
    List<Expense> getAll();

    @Query("SELECT * FROM expenses WHERE carId == :carId")
    List<Expense> getExpensesFromCarId(long carId);

    @Query("SELECT * FROM expenses WHERE carId == :carId AND expenseId == :expenseId")
    Expense getExpenseFromId(long carId, long expenseId);

    @Delete
    void delete(Expense expense);

    @Query("DELETE FROM expenses WHERE carId == :cardId")
    void deleteCarExpenses(long cardId);

    @Query("DELETE FROM expenses WHERE expenseId == :expenseId")
    void deleteFromId(long expenseId);
}
