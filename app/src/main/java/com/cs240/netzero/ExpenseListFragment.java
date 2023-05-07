package com.cs240.netzero;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import androidx.fragment.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.cs240.netzero.data.ExpenseView;

        import java.time.LocalDate;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;
        import java.util.stream.Collectors;

        import com.cs240.netzero.data.AppDatabase;
        import com.cs240.netzero.data.Expense;
        import com.cs240.netzero.data.ExpenseDao;
        import com.cs240.netzero.data.ExpenseView;

public class ExpenseListFragment extends Fragment {
    private View requiredView;
    private ListView listView;
    private ArrayList<ExpenseView> arrayList = new ArrayList<>();
    private CustomAdapter adapter = null;
    private AppDatabase db;
    private ExpenseDao expenseDao;
    private SharedPreferences sharedPreferences;
    private TextView tvWelcomeName;
    private TextView tvCarName;
    private ImageView ivUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requiredView = requireView();
        listView = requiredView.findViewById(R.id.lvExpenseList);
        sharedPreferences = this.requireContext().getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);

        tvWelcomeName = requiredView.findViewById(R.id.tvExpensesListWelcomeUser);
        tvCarName = requiredView.findViewById(R.id.tvCarNameExpensesList);
        ivUser = requiredView.findViewById(R.id.imgUserExpensesList);
        ivUser.setOnClickListener(v -> startActivity(new Intent(this.requireContext(), UserActivity.class)));

        db = AppDatabase.getDatabase(this.requireContext());
        expenseDao = db.expenseDao();
        setupUIData();
        setupListView();
    }

    private void setupUIData() {
        String userName = sharedPreferences.getString("userName", "");
        String carBrand = sharedPreferences.getString("selectedCarBrand", "");
        String carModel = sharedPreferences.getString("selectedCarModel", "");

        tvWelcomeName.setText(getString(R.string.welcome_user, userName));
        tvCarName.setText(getString(R.string.car_label, carBrand, carModel));
    }

    private void setupListView() {
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);

        if (selectedCarId != -1L) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            service.execute(new Runnable() {
                @Override
                public void run() {
                    List<Expense> expensesList;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        expensesList = expenseDao.getExpensesFromCarId(selectedCarId)
                                .stream()
                                .sorted(Comparator.comparing(expense -> LocalDate.parse(expense.getDate(), dateTimeFormatter)))
                                .collect(Collectors.toList());
                    } else {
                        expensesList = expenseDao.getExpensesFromCarId(selectedCarId)
                                .stream()
                                .sorted(Comparator.comparing(Expense::getDate))
                                .collect(Collectors.toList());
                    }

                    ArrayList<ExpenseView> arrayList = new ArrayList<>();
                    for (Expense expense : expensesList) {
                        int iconId = R.drawable.ic_bi_shield_check;
                        switch (expense.getType()) {
                            case "REFUEL":
                                iconId = R.drawable.ic_green_local_gas_station_for_list;
                                break;
                            case "DAILIES":
                                iconId = R.drawable.ic_baseline_alarm_dailies_for_list;
                                break;
                            case "MAINTENANCE":
                                iconId = R.drawable.ic_tabler_engine;
                                break;
                            case "TRAVELS":
                                iconId = R.drawable.ic_baseline_travel_luggage_for_list;
                                break;
                        }

                        arrayList.add(new ExpenseView(expense.getExpenseId(), iconId, expense.getTitle(), expense.getSpent()));
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Collections.reverse(arrayList);
                            CustomAdapter adapter = new CustomAdapter(getActivity(), arrayList);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    long expenseId = arrayList.get(position).getExpenseId();
                                    Intent intent = new Intent(getActivity(), SingleExpenseActivity.class);
                                    intent.putExtra("expenseId", expenseId);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.db_error), Toast.LENGTH_SHORT).show();
        }
    }
}