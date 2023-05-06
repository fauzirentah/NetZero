package com.cs240.netzero;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetReadingsActivity extends AppCompatActivity {

    private static final String TAG = "GetReadingsActivity";
    private static final String API_URL = "https://www.random.org/integers/?num=1&min=0&max=100&col=1&base=10&format=plain&rnd=new";

    private OkHttpClient okHttpClient;
    private Handler handler;
    private Handler mainHandler;
    private TextView singleNumberTextView;
    private TextView sumTextView;
    private TextView countTextView;
    private TextView averageTextView;
    private ArrayList<Double> numbersList;
    private int sum;
    private int count;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_readings);

        mainHandler = new Handler(Looper.getMainLooper());
        okHttpClient = new OkHttpClient();
        handler = new Handler();
        singleNumberTextView = findViewById(R.id.single_number_text_view);
        sumTextView = findViewById(R.id.sum_text_view);
        countTextView = findViewById(R.id.count_text_view);
        averageTextView = findViewById(R.id.average_text_view);
        numbersList = new ArrayList<>();
        sum = 0;
        count = 0;

        //Auto-start
/*
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getNumberFromAPI();
            }
        }, 0, 5000);
*/
        // Set up the start button
        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.pause_button);
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
                            singleNumberTextView.setText(String.valueOf(number.intValue()));
                            addNumberToList(number.intValue());
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
}