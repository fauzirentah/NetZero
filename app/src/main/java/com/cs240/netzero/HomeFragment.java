package com.cs240.netzero;

        import static androidx.core.content.ContextCompat.getColor;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.core.content.ContextCompat;
        import androidx.fragment.app.Fragment;

        import com.github.mikephil.charting.animation.Easing;
        import com.github.mikephil.charting.charts.LineChart;
        import com.github.mikephil.charting.charts.PieChart;
        import com.github.mikephil.charting.data.Entry;
        import com.github.mikephil.charting.data.LineData;
        import com.github.mikephil.charting.data.LineDataSet;
        import com.github.mikephil.charting.data.PieData;
        import com.github.mikephil.charting.data.PieEntry;
        import com.github.mikephil.charting.data.PieDataSet;
        import com.github.mikephil.charting.formatter.PercentFormatter;
        import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
        import com.github.mikephil.charting.utils.ColorTemplate;
        import com.google.android.material.button.MaterialButton;

        import java.time.LocalDate;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;
        import java.util.Objects;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import com.cs240.netzero.data.AppDatabase;
        import com.cs240.netzero.data.Expense;
        import com.cs240.netzero.data.ExpenseView;
        import com.cs240.netzero.data.ExpenseDao;
        import com.cs240.netzero.data.Utilities;

public class HomeFragment extends Fragment {
    private TextView tvWelcomeName;
    private TextView tvCarName;
    private TextView tvSpentThisMonth;
    private TextView tvEmittedThisMonth;
    private TextView tvAvgConsumptionThisMonth;
    private ImageView imgUser;

    private PieChart pieChart;
    private LineChart lineChart;
    private ListView listView;
    private ArrayList<ExpenseView> arrayList = new ArrayList<>();
    private CustomAdapter adapter;
    private MaterialButton btnPieChart;
    private MaterialButton btnLineChart;
    private View requiredView;
    private String selectedChart = "PIE";

    private AppDatabase db;
    private ExpenseDao expenseDao;
    private List<Expense> expensesList;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requiredView = requireView();

        tvWelcomeName = requiredView.findViewById(R.id.tvWelcomeName);
        tvCarName = requiredView.findViewById(R.id.tvCarName);
        tvSpentThisMonth = requiredView.findViewById(R.id.tvSpentThisMonth);
        tvEmittedThisMonth = requiredView.findViewById(R.id.tvEmittedThisMonth);
        tvAvgConsumptionThisMonth = requiredView.findViewById(R.id.tvAvgConsumptionThisMonth);
        imgUser = requiredView.findViewById(R.id.imgUser);
        imgUser.setOnClickListener(v -> startActivity(new Intent(requireContext(), UserActivity.class)));

        pieChart = requiredView.findViewById(R.id.homePieChart);
        lineChart = requiredView.findViewById(R.id.homeLineChart);
        listView = requiredView.findViewById(R.id.lvLastExpenses);
        btnPieChart = requiredView.findViewById(R.id.btnDistribution);
        btnLineChart = requiredView.findViewById(R.id.btnTrend);
        db = AppDatabase.getDatabase(requireContext());
        expenseDao = db.expenseDao();
        sharedPreferences = requireContext().getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);

        setupListView();

        btnPieChart.setOnClickListener(v -> {
            if (selectedChart != "PIE") {
                pieChart.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.GONE);

                btnPieChart.setBackgroundColor(getColor(requireContext(), R.color.gray_bg));
                btnPieChart.setTextColor(getColor(requireContext(), R.color.black));
                btnPieChart.setIconTintResource(R.color.black);

                btnLineChart.setBackgroundColor(getColor(requireContext(), R.color.white));
                btnLineChart.setTextColor(getColor(requireContext(), R.color.lightgray));
                btnLineChart.setIconTintResource(R.color.lightgray);

                selectedChart = "PIE";
            }
        });

        btnLineChart.setOnClickListener(v -> {
            if (selectedChart != "LINE" && !expensesList.isEmpty()) {
                pieChart.setVisibility(View.GONE);
                lineChart.setVisibility(View.VISIBLE);

                btnLineChart.setBackgroundColor(getColor(requireContext(), R.color.gray_bg));
                btnLineChart.setTextColor(getColor(requireContext(), R.color.black));
                btnLineChart.setIconTintResource(R.color.black);

                btnPieChart.setBackgroundColor(getColor(requireContext(), R.color.white));
                btnPieChart.setTextColor(getColor(requireContext(), R.color.lightgray));
                btnPieChart.setIconTintResource(R.color.lightgray);

                selectedChart = "LINE";
            }
        });
    }

    private void setupListView() {
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);

        if (selectedCarId != -1L) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            service.execute(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    expensesList = expenseDao.getExpensesFromCarId(selectedCarId);
                    Collections.sort(expensesList, Comparator.comparing(i -> LocalDate.parse(i.date, dateTimeFormatter)));
                } else {
                    expensesList = expenseDao.getExpensesFromCarId(selectedCarId);
                    Collections.sort(expensesList, Comparator.comparing(i -> i.date));
                }


                for (Expense i : expensesList) {
                    int iconId;
                    switch (i.type) {
                        case "REFUEL":
                            iconId = R.drawable.ic_green_local_gas_station_for_list;
                            break;
                        case "CO2":
                            iconId = R.drawable.ic_baseline_carbon;
                            break;
                        case "MAINTENANCE":
                            iconId = R.drawable.ic_tabler_engine;
                            break;
                        case "TAX":
                            iconId = R.drawable.ic_baseline_article_24;
                            break;
                        default:
                            iconId = R.drawable.ic_bi_shield_check;
                            break;
                    }

                    arrayList.add(new ExpenseView(i.expenseId, iconId, i.title, i.spent));
                }

                handler.post(() -> {
                    Collections.reverse(arrayList);
                    setupUIData();
                    setupPieChart();
                    setupLineChart();

                    adapter = new CustomAdapter(requireContext(), arrayList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        long expenseId = arrayList.get(position).expenseId;
                        startActivity(new Intent(requireContext(), SingleExpenseActivity.class)
                                .putExtra("expenseId", expenseId));
                    });
                });
            });
        } else {
            Toast.makeText(requireContext(), getString(R.string.db_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupUIData() {
        String userName = sharedPreferences.getString("userName", "");
        String carBrand = sharedPreferences.getString("selectedCarBrand", "");
        String carModel = sharedPreferences.getString("selectedCarModel", "");
        String fuelType = Objects.requireNonNull(sharedPreferences.getString("selectedCarFuel", ""));
        String euroCategory = Objects.requireNonNull(sharedPreferences.getString("selectedCarEuro", ""));

        tvWelcomeName.setText(getString(R.string.welcome_user, userName));
        tvCarName.setText(getString(R.string.car_label, carBrand, carModel));

        List<Expense> thisMonthExpenses = Utilities.getThisMonthExpenses(expensesList);
        double spent = Utilities.getTotalSpent(thisMonthExpenses);
        double emitted = Utilities.getEmitted(thisMonthExpenses, fuelType, euroCategory);
        double consumption = Utilities.getAvgConsumption(thisMonthExpenses);

        tvSpentThisMonth.setText(String.valueOf(spent));
        tvEmittedThisMonth.setText(String.valueOf(emitted));
        tvAvgConsumptionThisMonth.setText(String.valueOf(consumption));

        sharedPreferences.edit()
                .putFloat("spentThisMonth", (float) spent)
                .putFloat("emittedThisMonth", (float) emitted)
                .putFloat("consumptionThisMonth", (float) consumption)
                .apply();

        List<Expense> prevMonthExpenses = Utilities.getPrevMonthExpenses(expensesList);
        if (prevMonthExpenses.stream().anyMatch(expense -> expense.getType().equals("REFUEL"))) {
            double consumptionPrevMonth = Utilities.getAvgConsumption(prevMonthExpenses);
            sharedPreferences.edit()
                    .putFloat("consumptionPrevMonth", (float) consumptionPrevMonth)
                    .apply();
        }
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(48f);
        pieChart.setCenterText(expensesList.isEmpty() ? getString(R.string.no_expense_registered_warning) : Utilities.getThisMonthYear());
        pieChart.setCenterTextSize(16f);
        pieChart.setDrawCenterText(true);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> entries = getPieEntries();

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#A7FB9C"));
        colors.add(Color.parseColor("#A36BFF"));
        colors.add(Color.parseColor("#FF885C"));
        colors.add(ColorTemplate.getHoloBlue());

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private ArrayList<PieEntry> getPieEntries() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Expense> thisMonthExpenses = Utilities.getThisMonthExpenses(expensesList);
        double spentRefuel = 0.0;
        double spentCo2 = 0.0;
        double spentInsurance = 0.0;
        double spentTax = 0.0;
        double spentMaintenance = 0.0;

        for (Expense e : thisMonthExpenses) {
            switch (e.getType()) {
                case "REFUEL":
                    spentRefuel += e.getSpent();
                    break;
                case "CO2":
                    spentCo2 += e.getSpent();
                    break;
                case "MAINTENANCE":
                    spentMaintenance += e.getSpent();
                    break;
                case "TAX":
                    spentTax += e.getSpent();
                    break;
                default:
                    spentInsurance += e.getSpent();
                    break;
            }
        }

        double total = spentRefuel + spentCo2 + spentMaintenance + spentInsurance + spentTax;
        if (spentRefuel > 0.0) entries.add(new PieEntry((float)((spentRefuel / total) * 100), getString(R.string.refuel_title)));
        if (spentCo2 > 0.0) entries.add(new PieEntry((float)((spentCo2 / total) * 100), getString(R.string.co2_title)));
        if (spentMaintenance > 0.0) entries.add(new PieEntry((float)((spentMaintenance / total) * 100), getString(R.string.maintenance_title)));
        if (spentInsurance > 0.0) entries.add(new PieEntry((float)((spentInsurance / total) * 100), getString(R.string.insurance_title)));
        if (spentTax > 0.0) entries.add(new PieEntry((float)((spentTax / total) * 100), getString(R.string.tax_title)));

        return entries;
    }

    private void setupLineChart() {
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        List<Entry> values = getLineEntries();

        LineDataSet set1;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, getString(R.string.expenses_trend) + Utilities.getThisMonthYear());
            set1.setDrawIcons(false);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(3f);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private List<Entry> getLineEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        double spent = 0.0;
        List<Expense> thisMonthExpenses = Utilities.getThisMonthExpenses(expensesList);

        for (Expense e : thisMonthExpenses) {
            String[] dateParts = e.date.split("/");
            String day = dateParts[0];
            spent += e.spent;
            entries.add(new Entry(Float.parseFloat(day), (float)spent));
        }

        return entries;
    }

}