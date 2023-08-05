package com.cs240.netzero;

        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import com.google.gson.GsonBuilder;
        import com.google.gson.annotations.SerializedName;

        import java.util.Arrays;
        import java.util.HashSet;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import okhttp3.OkHttpClient;
        import okhttp3.Request;

public class SelectCarModelActivity extends AppCompatActivity {

    private String selectedModel = "";
    private String[] models = new String[]{};
    private String url = "https://car-data.p.rapidapi.com/cars?limit=50&make=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car_model);

        String selectedBrand = getIntent().getStringExtra("selectedBrand");
        if (selectedBrand != null && !selectedBrand.isEmpty()) {
            TextView tvSelectCarModel = findViewById(R.id.textViewSelectCarModel);
            url += selectedBrand + "&page=0";
            tvSelectCarModel.setText(tvSelectCarModel.getText().toString().replace("auto", selectedBrand));
        }

        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(() -> {
            getModels();
            handler.post(this::showListView);
        });

        Button btnNext = findViewById(R.id.btnModelNext);
        btnNext.setOnClickListener(view -> {
            if (selectedBrand != null && selectedBrand.length() > 0 && selectedModel != null && selectedModel.length() > 0) {
                Intent intent = new Intent(this, SelectCarPetrolCategoryActivity.class);
                intent.putExtra("selectedBrand", selectedBrand);
                intent.putExtra("selectedModel", selectedModel);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getModels() {
        boolean responseResult = true;
        HashSet<String> pageModels = new HashSet<>();
        int pageIndex = 0;
        String key = BuildConfig.RAPIDAPI_KEY;

        try {
            while (responseResult) {
                OkHttpClient client = new OkHttpClient();
                url = url.substring(0, url.length() - 1) + pageIndex;
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                        .addHeader("X-RapidAPI-Key", key)
                        .build();
                String body = client.newCall(request).execute().body().string();

                if (!body.equals("[]")) {
                    CarInfo[] parsedData = new GsonBuilder().create().fromJson(body, CarInfo[].class);

                    for (CarInfo element : parsedData) {
                        pageModels.add(element.model);
                    }
                } else {
                    responseResult = false;
                }

                pageIndex++;
                Thread.sleep(700); //API requests per seconds restrictions on free plan
            }

            this.models = pageModels.toArray(new String[0]); //clears duplicates
            Arrays.sort(this.models);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CarInfo {
        @SerializedName("model")
        private String model;

        public CarInfo(String model) {
            this.model = model;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }
    private void showListView() {
        ProgressBar progressBar = findViewById(R.id.progressBarCarModels);
        progressBar.setVisibility(View.GONE);

        ListView listView = findViewById(R.id.listViewCarModels);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, models);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setVisibility(View.VISIBLE);

        EditText textFilter = findViewById(R.id.modelFilter);
        textFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedModel = adapterView.getItemAtPosition(position).toString();
        });
    }
}