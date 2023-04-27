package com.cs240.netzero;

        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.View;
        import android.widget.*;
        import androidx.appcompat.app.AppCompatActivity;
        import com.google.gson.GsonBuilder;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;

        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.Arrays;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

public class SelectCarBrandActivity extends AppCompatActivity {
    private String selectedBrand = "";
    private final URL url = new URL("https://car-data.p.rapidapi.com/cars/makes");
    private String[] brands = new String[0];

    public SelectCarBrandActivity() throws MalformedURLException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car_brand);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            getBrands();
            handler.post(this::showListView);
        });

        findViewById(R.id.btnProducerNext).setOnClickListener((view) -> {
            if (selectedBrand != "") {
                final Intent intent = new Intent(this, SelectCarModelActivity.class);
                intent.putExtra("selectedBrand", selectedBrand);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBrands(){
        final String key = BuildConfig.RAPIDAPI_KEY;
        try {
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                    .addHeader("X-RapidAPI-Key", key)
                    .build();

            final String body = client.newCall(request).execute().body().string();
            brands = new GsonBuilder().create().fromJson(body, String[].class);
            Arrays.sort(brands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showListView(){
        findViewById(R.id.progressBarCarBrands).setVisibility(View.GONE);

        final ListView listView = findViewById(R.id.listViewCarBrands);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, brands);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setVisibility(View.VISIBLE);

        final EditText textFilter = findViewById(R.id.producerFilter);
        textFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        listView.setOnItemClickListener((adapterView, view, position, id) -> selectedBrand = adapterView.getItemAtPosition(position).toString());
    }
}
