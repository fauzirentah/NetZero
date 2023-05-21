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
                new Expense(0, getString(R.string.air_filter), "MAINTENANCE", "03/" + previousMonth + "/" + previousYear, 60.0, null, null, null, selectedCarId),
                new Expense(0, getString(R.string.refuel_title), "REFUEL", "05/" + previousMonth + "/" + previousYear, 20.0, null, 2.0, 1000.0, selectedCarId),
                new Expense(0, getString(R.string.refuel_title), "REFUEL", "13/" + previousMonth + "/" + previousYear, 20.0, null, 2.0, 1150.0, selectedCarId),
                new Expense(0, getString(R.string.tax_title) + " " + previousYear, "TAX", "20/" + previousMonth + "/" + previousYear, 50.0, getString(R.string.example_tax_description1), null, null, selectedCarId),
                new Expense(0, getString(R.string.brake_pads), "MAINTENANCE", "25/" + previousMonth + "/" + previousYear, 73.0, getString(R.string.example_maint_description1), null, null, selectedCarId),
                new Expense(0, getString(R.string.refuel_title), "REFUEL", "29/" + previousMonth + "/" + previousYear, 50.0, null, 2.0, 1290.0, selectedCarId),
                new Expense(0, getString(R.string.oil_change), "MAINTENANCE", "02/" + currentMonth + "/" + currentYear, 25.0, getString(R.string.example_maint_description2), null, null, selectedCarId),
                new Expense(0, getString(R.string.refuel_title), "REFUEL", "05/" + currentMonth + "/" + currentYear, 30.0, null, 2.0, 1640.0, selectedCarId),
                new Expense(0, getString(R.string.refuel_title), "REFUEL", "14/" + currentMonth + "/" + currentYear, 20.0, null, 2.0, 1820.0, selectedCarId),
                new Expense(0, getString(R.string.insurance_title) + " " + currentYear, "INSURANCE", "18/" + currentMonth + "/" + currentYear, 130.0, getString(R.string.example_ins_description1), null, null, selectedCarId),
                new Expense(0, getString(R.string.tax_title) + " " + currentYear, "TAX", "20/" + currentMonth + "/" + currentYear, 50.0, getString(R.string.example_tax_description1), null, null, selectedCarId),
                new Expense(0, getString(R.string.refuel_title), "REFUEL", "21/" + currentMonth + "/" + currentYear, 20.0, null, 2.0, 2050.0, selectedCarId)
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
