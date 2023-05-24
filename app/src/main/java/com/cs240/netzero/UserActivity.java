package com.cs240.netzero;

        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.widget.Button;
        import android.widget.EditText;

        import androidx.appcompat.app.AppCompatActivity;

        import java.text.SimpleDateFormat;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Locale;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import com.cs240.netzero.R;
        import com.cs240.netzero.data.AppDatabase;
        import com.cs240.netzero.data.Expense;
        import com.cs240.netzero.data.ExpenseDao;

public class UserActivity extends AppCompatActivity {
    private EditText etUserName;
    private Button btnSaveChanges;
    private Button btnSendNotification;
    private Button btnClearExpenses;
    private Button btnGenerateExpenses;
    private String userName;

    private AppDatabase db;
    private ExpenseDao expenseDao;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        db = AppDatabase.getDatabase(getApplicationContext());
        expenseDao = db.expenseDao();
        sharedPreferences = getApplicationContext().getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "");

        etUserName = findViewById(R.id.etUserName);
        etUserName.setHint(userName);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(v -> updateUsername());

        btnSendNotification = findViewById(R.id.btnSendNotification);
        btnSendNotification.setOnClickListener(v -> sendNotification());

        btnGenerateExpenses = findViewById(R.id.btnGenerateExpenses);
        btnGenerateExpenses.setOnClickListener(v -> generateExpenses());

        btnClearExpenses = findViewById(R.id.btnClearExpenses);
        btnClearExpenses.setOnClickListener(v -> clearExpenses());
    }

    private void generateExpenses() {
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);
        Calendar calendar = Calendar.getInstance();
        String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());
        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String previousMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());
        String previousYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());

        List<Expense> expenses = Arrays.asList(
                new Expense(0, "Business Travel", "TRAVELS", "03/" + previousMonth + "/" + previousYear, 783.5, "0.713", null, 0.1025, selectedCarId),
                new Expense(0, "Work", "DAILIES", "05/" + previousMonth + "/" + previousYear, 820.3, "0.643", 2.0, 0.10, selectedCarId),
                new Expense(0, "Home", "DAILIES", "13/" + previousMonth + "/" + previousYear, 927.7, "0.541", 2.0, 0.10, selectedCarId),
                new Expense(0, "Site Visit", "TRAVELS", "20/" + previousMonth + "/" + previousYear, 858.2, "0.815", null, 0.12035, selectedCarId),
                new Expense(0, "Holidays", "TRAVELS", "25/" + previousMonth + "/" + previousYear, 737.9, "0.616", null, 0.07125, selectedCarId),
                new Expense(0, "School", "DAILIES", "29/" + previousMonth + "/" + previousYear, 919.5, "0.875", 2.0, 0.0129, selectedCarId),
                new Expense(0, "Hometown", "TRAVELS", "02/" + currentMonth + "/" + currentYear, 825.4, "0.782", null, 0.1525, selectedCarId),
                new Expense(0, "Malls", "DAILIES", "05/" + currentMonth + "/" + currentYear, 730.9, "0.715", 2.0, 0.04, selectedCarId),
                new Expense(0, "Work", "DAILIES", "14/" + currentMonth + "/" + currentYear, 826.2, "0.743", 2.0, 0.182, selectedCarId),
                new Expense(0, "Business Travel", "TRAVELS", "18/" + currentMonth + "/" + currentYear, 1130.8, "1.548", null, 0.4525, selectedCarId),
                new Expense(0, "Train/ Bus Station", "DAILIES", "20/" + currentMonth + "/" + currentYear, 1050.7, "5.216", null, 0.212, selectedCarId),
                new Expense(0, "Home", "DAILIES", "21/" + currentMonth + "/" + currentYear, 627.8, "5.816", 2.0, 2.05, selectedCarId)
        );

        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(new Runnable() {
            @Override
            public void run() {
                expenseDao.deleteCarExpenses(selectedCarId);
                expenseDao.insertExpenses(expenses);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(UserActivity.this, DashboardActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    private void clearExpenses() {
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(new Runnable() {
            @Override
            public void run() {
                if(selectedCarId != -1L) expenseDao.deleteCarExpenses(selectedCarId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(UserActivity.this, DashboardActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    private void sendNotification() {
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                100,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    private void updateUsername() {
        String text = etUserName.getText().toString();
        if(!text.equals(userName) && !text.equals("")) {
            sharedPreferences.edit().putString("userName", text).apply();
            startActivity(new Intent(UserActivity.this, DashboardActivity.class));
            finish();
        }
    }

}
