package com.cs240.netzero;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cs240.netzero.data.AppDatabase;
import com.cs240.netzero.data.Expense;
import com.cs240.netzero.data.ExpenseDao;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetReadingsActivity extends AppCompatActivity {

    private static final String TAG = "GetReadingsActivity";
    //private static final String API_URL = "https://www.random.org/integers/?num=1&min=100&max=200&col=1&base=10&format=plain&rnd=new";
    private static final String API_URL = "http://172.20.10.2/";

    private String expenseType;
    private long expenseId = 0L;
    private OkHttpClient okHttpClient;
    private Handler handler;
    private Handler mainHandler;
    private TextView singleNumberTextView;
    private TextView sumTextView;
    private TextView countTextView;
    private TextView averageTextView;
    private TextView redAlertTextView;
    private String title;
    private ArrayList<Double> numbersList;
    private List<String> items = Arrays.asList();
    private ArrayAdapter<String> adapter;
    private Spinner mySpinner;
    private int sum;
    private int count;
    private int co2max = 100;
    private int sumNotificationId = -1;
    private static final String CHANNEL_ID = "KarboTrek_CO2";
    private Timer timer;

    private AppDatabase db;
    private ExpenseDao expenseDao;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_readings);
        expenseType = getIntent().getStringExtra("expenseType");
        sharedPreferences = getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);

        mainHandler = new Handler(Looper.getMainLooper());
        okHttpClient = new OkHttpClient();
        handler = new Handler();
        singleNumberTextView = findViewById(R.id.single_number_text_view);
        sumTextView = findViewById(R.id.sum_text_view);
        countTextView = findViewById(R.id.count_text_view);
        averageTextView = findViewById(R.id.average_text_view);
        redAlertTextView = findViewById(R.id.red_alert);
        numbersList = new ArrayList<>();
        sum = 0;
        count = 0;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mySpinner = findViewById(R.id.dailies_type_text);

        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.pause_button);
        Button saveButton = findViewById(R.id.save_button);

        // Set up the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGettingData();
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            }
        });

// Set up the stop button
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGettingData();
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { registerExpense(); }
        });

        db = AppDatabase.getDatabase(getApplicationContext());
        expenseDao = db.expenseDao();

        setupViews();

    }

    private void setupViews() {
        if (expenseType != null && expenseType.equals("DAILIES")) {
            items = Arrays.asList("Home", "Work", "School", "Train/ Bus Station", "Malls");
        } else if (expenseType != null && expenseType.equals("TRAVELS")) {
            items = Arrays.asList("Hometown", "Business Travel", "Holidays", "Site Visit");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mySpinner.setAdapter(adapter);
    }


    private void startGettingData() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getNumberFromAPI();
            }
        }, 0, 5000);
    }

    private void stopGettingData() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void getNumberFromAPI() {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Failed to get number from API: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string().trim();
                    Double number = Double.parseDouble(responseBody);

                    // Create a new Runnable to update the UI on the main thread
                    Runnable updateUIRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // singleNumberTextView.setText(String.valueOf(number.intValue()));
                            singleNumberTextView.setText("Normal");
                            addNumberToList(number.intValue());
                            redAlertTextView.setVisibility(View.INVISIBLE);

                            if (number > co2max) {
                                singleNumberTextView.setText("High");
                                singleNumberTextView.setTextColor(Color.RED);
                                redAlertTextView.setVisibility(View.VISIBLE);

                                // Send notification
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    createNotificationChannel();
                                }

                                if (sumNotificationId == -1) {
                                    sumNotificationId = 1;
                                } else {
                                    sumNotificationId++;
                                }

                                if (!isGetReadingsActivityActive()) {

                                // Create an explicit intent to open the GetReadingsActivity
                                Intent intent = new Intent(getApplicationContext(), GetReadingsActivity.class);
                                intent.setAction(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // Get the PendingIntent from the TaskStackBuilder
                                PendingIntent pendingIntent = PendingIntent.getActivity(GetReadingsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                // Create the notification
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(GetReadingsActivity.this, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_co2_logo)
                                        .setContentTitle("CO2 alert")
                                        .setContentText("CO2 emission exceeds recommended value: " + number)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(pendingIntent) // Set the pending intent
                                        .setAutoCancel(true);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GetReadingsActivity.this);
                                notificationManager.notify(sumNotificationId, builder.build());
                                }

                            } else {
                                singleNumberTextView.setTextColor(Color.GRAY); // Set the default color here
                            }

                        }
                    };

                    // Post the Runnable to the main thread using the mainHandler
                    mainHandler.post(updateUIRunnable);

                } else {
                    Log.e(TAG, "Failed to get number from API: " + response.code());
                }
            }
        });
    }

    private void addNumberToList(Integer number) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                numbersList.add(number.doubleValue());
                updateSum();
                updateCount();
                updateAverage();
            }
        });
    }


    private void updateSum() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Double sum = 0.0;
                for (Double number : numbersList) {
                    sum += number;
                }
                int sumInt = (int) sum.doubleValue();
                sumTextView.setText(String.valueOf(sumInt));

            }
        });
    }

    private void updateCount() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                double count = (double) numbersList.size() * 5 / 60;
                String countString = String.format("%.2f", count);
                countTextView.setText(countString);
            }
        });
    }

    private void updateAverage() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Double sum = 0.0;
                int count = numbersList.size();
                for (Double number : numbersList) {
                    sum += number;
                }
                DecimalFormat df = new DecimalFormat("#.##");
                Double average = count > 0 ? sum / count : 0;
                averageTextView.setText(df.format(average));
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sum Notification Channel";
            String description = "Channel for displaying sum notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isGetReadingsActivityActive() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
            if (!runningTasks.isEmpty()) {
                ComponentName topActivity = runningTasks.get(0).topActivity;
                if (topActivity != null && topActivity.getClassName().equals(GetReadingsActivity.class.getName())) {
                    return true; // GetReadingsActivity is active
                }
            }
        }
        return false; // GetReadingsActivity is not active
    }

    private void registerExpense() {

        boolean errorFlag = false;
        long selectedCarId = sharedPreferences.getLong("selectedCarId", -1L);

        double avgTravelTimePerKm = 0; // Average travel time in city drive or expressway
        double fuelConsumption = 0; // Fuel consumption in city drive or expressway
        double co2ePerLitre = 2.31; // Co2e per litre of petrol burned
        double treeCapacity = 21.8; // Carbon sequestration capacity of one tree (kg)
        double distanceEst;
        double litresBurnedEst;
        double co2eEst;
        double numberOfTrees;

        title = mySpinner.getSelectedItem().toString();
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                title = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(date);

        if (dateString.equals("")) {
            dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()).toString();
        }

        double totalSpent;
        try {
            totalSpent = Double.parseDouble(averageTextView.getText().toString());
        } catch (NumberFormatException e) {
            totalSpent = -1.0;
        }

        // String description = etDescription.getText().toString();
        if (expenseType.equals("DAILIES")) {
            avgTravelTimePerKm = 97;
            fuelConsumption =  12.3;

        } else if (expenseType.equals("TRAVELS")) {
            avgTravelTimePerKm = 45;
            fuelConsumption = 7;
        }

        distanceEst = Double.parseDouble(countTextView.getText().toString()) * 60 / avgTravelTimePerKm;
        litresBurnedEst = distanceEst / fuelConsumption;
        co2eEst = litresBurnedEst * co2ePerLitre;
        numberOfTrees = co2eEst / treeCapacity;

        Expense newExpense = new Expense(
                expenseId,
                title,
                expenseType,
                dateString,
                totalSpent,
                String.valueOf(co2eEst),
                Double.parseDouble(countTextView.getText().toString()),
                Double.parseDouble(String.valueOf(numberOfTrees)),
                selectedCarId
        );

        // Expense newExpense = new Expense(expenseId,title,expenseType,dateString,totalSpent,null,Double.parseDouble(countTextView.getText().toString()),Integer.parseInt(sumTextView.getText().toString()),selectedCarId);

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
                            startActivity(new Intent(GetReadingsActivity.this, DashboardActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
    }

}