package com.cs240.netzero;

        import android.app.DatePickerDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Locale;
        import java.util.concurrent.Executors;
        import java.util.concurrent.ExecutorService;

        import com.cs240.netzero.data.AppDatabase;
        import com.cs240.netzero.data.Expense;
        import com.cs240.netzero.data.ExpenseDao;

public class AddExpenseActivity extends AppCompatActivity {
    private String expenseType;
    private long expenseId = 0L;
    private TextView tvTitleLabel;
    private LinearLayout llDescription;
    private LinearLayout llPricePerLiter;
    private LinearLayout llTotalKm;
    private LinearLayout llTaxTitle;
    private LinearLayout llMaintenanceTitle;
    private LinearLayout llYear;

    private EditText etDate;
    private EditText etTotalSpent;
    private EditText etPricePerLiter;
    private EditText etTotalKm;
    private EditText etMaintenanceTitle;
    private EditText etTaxTitle;
    private EditText etDescription;
    private EditText etYear;
    private Button btnRegister;

    private AppDatabase db;
    private ExpenseDao expenseDao;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        expenseType = getIntent().getStringExtra("expenseType");
        sharedPreferences = getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);

        tvTitleLabel = findViewById(R.id.addExpenseTitleLabel);

        llPricePerLiter = findViewById(R.id.etPricePerLiterContainer);
        llTotalKm = findViewById(R.id.etTotalKmContainer);
        llTaxTitle = findViewById(R.id.etTaxTitleContainer);
        llDescription = findViewById(R.id.etOptionalDescriptionContainer);
        llMaintenanceTitle = findViewById(R.id.etMaintenanceTitleContainer);
        llYear = findViewById(R.id.etYearContainer);

        etDate = findViewById(R.id.etRefDate);
        etTotalSpent = findViewById(R.id.etRefTotalSpent);
        etPricePerLiter = findViewById(R.id.etRefPricePerLiter);
        etTotalKm = findViewById(R.id.etRefTotalKm);
        etMaintenanceTitle = findViewById(R.id.etMaintenanceTitle);
        etTaxTitle = findViewById(R.id.etTaxTitle);
        etDescription = findViewById(R.id.etOptionalDescription);
        etYear = findViewById(R.id.etYear);
        transformIntoDatePicker(etDate, this, "dd/MM/yyyy", new Date());
        etDate.setHint(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerExpense();
            }
        });

        setupViews();

        db = AppDatabase.getDatabase(getApplicationContext());
        expenseDao = db.expenseDao();
    }

    private void setupViews() {
        if (!expenseType.equals("REFUEL")) {
            llPricePerLiter.setVisibility(View.GONE);
            llTotalKm.setVisibility(View.GONE);
            llDescription.setVisibility(View.VISIBLE);
        }

        switch (expenseType) {
            case "REFUEL":
                break;
            case "TAX":
                llTaxTitle.setVisibility(View.VISIBLE);
                break;
            case "MAINTENANCE":
                llMaintenanceTitle.setVisibility(View.VISIBLE);
                break;
            default:
                llYear.setVisibility(View.VISIBLE);
        }

        long expenseIdExtra = getIntent().getLongExtra("expenseId", -1L);
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);
        if (expenseIdExtra != -1L) { //We are modifying an existing expense
            expenseId = expenseIdExtra;
            tvTitleLabel.setText(getText(expenseType.equals("REFUEL") ? R.string.update_refuel :
                    expenseType.equals("TAX") ? R.string.update_tax :
                            expenseType.equals("MAINTENANCE") ? R.string.update_maintenance :
                                    R.string.update_insurance));

            Executors.newSingleThreadExecutor().execute(() -> {
                Expense expense = expenseDao.getExpenseFromId(selectedCarId, expenseIdExtra);
                new Handler(Looper.getMainLooper()).post(() -> {
                    etDate.setText(expense.getDate());
                    etTotalSpent.setText(String.valueOf(expense.getSpent()));
                    etDescription.setText(expense.getDescription());

                    switch (expense.getType()) {
                        case "REFUEL":
                            etPricePerLiter.setText(String.valueOf(expense.getPricePerLiter()));
                            etTotalKm.setText(String.valueOf(expense.getTotalKm()));
                            break;
                        case "TAX":
                            etTaxTitle.setText(expense.getTitle());
                            break;
                        case "MAINTENANCE":
                            etMaintenanceTitle.setText(expense.getTitle());
                            break;
                        default:
                            etYear.setText(expense.getTitle().split(" ")[1]);
                    }
                });
            });
        } else {
            tvTitleLabel.setText(getText(expenseType.equals("REFUEL") ? R.string.label_add_refuel :
                    expenseType.equals("TAX") ? R.string.label_add_tax :
                            expenseType.equals("MAINTENANCE") ? R.string.label_add_maintenance :
                                    R.string.label_add_insurance));
        }
    }


    private void registerExpense() {

        boolean errorFlag = false;
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);

        String dateString = etDate.getText().toString();
        if (dateString.equals("")) {
            dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()).toString();
        }

        double totalSpent;
        try {
            totalSpent = Double.parseDouble(etTotalSpent.getText().toString());
        } catch (NumberFormatException e) {
            totalSpent = -1.0;
        }

        String description = etDescription.getText().toString();

        Expense newExpense = new Expense(
                expenseId,
                getString(R.string.refuel_title),
                expenseType,
                dateString,
                totalSpent,
                null,
                null,
                null,
                selectedCarId
        );

        switch (expenseType) {
            case "REFUEL":
                double pricePerLiter;
                try {
                    pricePerLiter = Double.parseDouble(etPricePerLiter.getText().toString());
                } catch (NumberFormatException e) {
                    pricePerLiter = -1.0;
                }

                int totalKm;
                try {
                    totalKm = Integer.parseInt(etTotalKm.getText().toString());
                } catch (NumberFormatException e) {
                    totalKm = -1;
                }

                if (totalSpent < 0 || totalKm < 0 || pricePerLiter <= 0) {
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show();
                    errorFlag = true;
                } else {
                    newExpense = new Expense(
                            expenseId,
                            getString(R.string.refuel_title),
                            expenseType,
                            dateString,
                            totalSpent,
                            null,
                            pricePerLiter,
                            totalKm,
                            selectedCarId
                    );
                }
                break;
            case "MAINTENANCE":
                String mTitle = etMaintenanceTitle.getText().toString();

                if (totalSpent < 0 || mTitle.equals("")) {
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show();
                    errorFlag = true;
                } else {
                    newExpense = new Expense(
                            expenseId,
                            mTitle,
                            expenseType,
                            dateString,
                            totalSpent,
                            description,
                            null,
                            null,
                            selectedCarId
                    );
                }
                break;
            case "TAX":
                String tTitle = etTaxTitle.getText().toString();

                if (totalSpent < 0 || tTitle.equals("")) {
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show();
                    errorFlag = true;
                } else {
                    newExpense = new Expense(
                            expenseId,
                            tTitle,
                            expenseType,
                            dateString,
                            totalSpent,
                            description,
                            null,
                            null,
                            selectedCarId
                    );
                }
                break;
            default:
                int year;
                try {
                    year = Integer.parseInt(etYear.getText().toString());
                } catch (NumberFormatException e) {
                    year = -1;
                }

                if (year < 1900 || totalSpent < 0) {
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show();
                    errorFlag = true;
                } else {
                    String title = getString(R.string.insurance_title) + year;
                    newExpense = new Expense(
                            expenseId,
                            title,
                            expenseType,
                            dateString,
                            totalSpent,
                            description,
                            null,
                            null,
                            selectedCarId
                    );
                }
        }

        if (!errorFlag) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            Expense finalNewExpense = newExpense;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    expenseDao.insertExpense(finalNewExpense);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(AddExpenseActivity.this, DashboardActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
    }

    private void transformIntoDatePicker(EditText editText, Context context, String format, Date maxDate) {
        editText.setFocusableInTouchMode(false);
        editText.setClickable(true);
        editText.setFocusable(false);

        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePickerOnDataSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            editText.setText(sdf.format(myCalendar.getTime()));
        };

        editText.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    datePickerOnDataSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            );

            if (maxDate != null) {
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
            }

            datePickerDialog.show();
        });
    }

}