package com.cs240.netzero;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import java.util.concurrent.Executors;
        import java.util.concurrent.ExecutorService;

        import com.cs240.netzero.data.AppDatabase;
        import com.cs240.netzero.data.Expense;
        import com.cs240.netzero.data.ExpenseDao;
        import com.cs240.netzero.data.ExpenseView;

public class SingleExpenseActivity extends AppCompatActivity {

    private LinearLayout llPricePerLiter;
    private LinearLayout llKm;
    private LinearLayout llYear;
    private LinearLayout llDescription;

    private TextView tvTitle;
    private TextView tvSpent;
    private TextView tvDate;
    private TextView tvPricePerLiter;
    private TextView tvYear;
    private TextView tvKm;
    private TextView tvDescription;
    private Button btnDelete;
    private Button btnUpdate;

    private AppDatabase db;
    private ExpenseDao expenseDao;
    private SharedPreferences sharedPreferences;

    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_expense);

        llPricePerLiter = findViewById(R.id.containerSingleExpensePricePerLiter);
        llKm = findViewById(R.id.containerSingleExpenseKm);
        llYear = findViewById(R.id.containerSingleExpenseYear);
        llDescription = findViewById(R.id.containerSingleExpenseDescription);

        tvTitle = findViewById(R.id.tvSingleExpenseTitle);
        tvDate = findViewById(R.id.tvSingleExpenseDate);
        tvPricePerLiter = findViewById(R.id.tvSingleExpensePricePerLiter);
        tvYear = findViewById(R.id.tvSingleExpenseYear);
        tvKm = findViewById(R.id.tvSingleExpenseKm);
        tvDescription = findViewById(R.id.tvSingleExpenseDescription);
        tvSpent = findViewById(R.id.tvSingleExpenseTotalSpent);
        btnDelete = findViewById(R.id.btnSingleExpenseDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
        btnUpdate = findViewById(R.id.btnSingleExpenseUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateExpense();
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);
        db = AppDatabase.getDatabase(getApplicationContext());
        expenseDao = db.expenseDao();
        getExpense();
    }

    private void updateExpense() {
        Intent intent = new Intent(this, AddExpenseActivity.class)
                .putExtra("expenseId", expense.getExpenseId())
                .putExtra("expenseType", expense.getType());
        startActivity(intent);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete) + expense.getTitle() + "?");
        builder.setMessage(getString(R.string.confirm_delete));
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            dialog.dismiss();
            deleteExpense();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteExpense() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);
        long expenseId = getIntent().getLongExtra("expenseId", -1L);

        if (selectedCarId != -1L && expenseId != -1L) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            service.execute(new Runnable() {
                @Override
                public void run() {
                    expenseDao.deleteFromId(expenseId);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SingleExpenseActivity.this, DashboardActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
    }

    private void getExpense() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs240.netzero", MODE_PRIVATE);
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);
        long expenseId = getIntent().getLongExtra("expenseId", -1L);

        if (selectedCarId != -1L && expenseId != -1L) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            service.execute(new Runnable() {
                @Override
                public void run() {
                    expense = expenseDao.getExpenseFromId(selectedCarId, expenseId);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setupViews();
                            setupData();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.db_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupData() {
    //    TextView tvTitle = findViewById(R.id.tvTitle);
    //    TextView tvDate = findViewById(R.id.tvDate);
    //    TextView tvSpent = findViewById(R.id.tvSpent);
    //    TextView tvPricePerLiter = findViewById(R.id.tvPricePerLiter);
    //    TextView tvKm = findViewById(R.id.tvKm);
    //    TextView tvYear = findViewById(R.id.tvYear);
    //    TextView tvDescription = findViewById(R.id.tvDescription);

        tvTitle.setText(expense.getTitle());
        tvDate.setText(expense.getDate());
        tvSpent.setText(String.valueOf(expense.getSpent()));

        if (expense.getType().equals("REFUEL")) {
            tvPricePerLiter.setText(String.valueOf(expense.getPricePerLiter()));
            tvKm.setText(String.valueOf(expense.getTotalKm()));
        } else if (expense.getType().equals("INSURANCE")) {
            tvYear.setText(expense.getTitle().split(" ")[1]);
        }

        tvDescription.setText(expense.getDescription());
    }

    private void setupViews() {
    //    LinearLayout llPricePerLiter = findViewById(R.id.llPricePerLiter);
    //    LinearLayout llKm = findViewById(R.id.llKm);
    //    LinearLayout llYear = findViewById(R.id.llYear);
    //    LinearLayout llDescription = findViewById(R.id.llDescription);

        if (!expense.getType().equals("REFUEL")) {
            llPricePerLiter.setVisibility(View.GONE);
            llKm.setVisibility(View.GONE);
        } else {
            llDescription.setVisibility(View.GONE);
        }

        if (!expense.getType().equals("INSURANCE")) {
            llYear.setVisibility(View.GONE);
        }
    }
}