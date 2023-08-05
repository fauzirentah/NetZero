package com.cs240.netzero;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import java.util.concurrent.Executors;

        import com.cs240.netzero.data.AppDatabase;
        import com.cs240.netzero.data.Car;
        import com.cs240.netzero.data.CarDao;

public class SelectCarPetrolCategoryActivity extends AppCompatActivity {

    private String selectedBrand;
    private String selectedModel;
    private String selectedFuel;
    private String selectedEuro;

    private AppDatabase db;
    private CarDao carDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car_petrol_category);

        selectedBrand = getIntent().getStringExtra("selectedBrand");
        selectedModel = getIntent().getStringExtra("selectedModel");
        // selectedFuel = getIntent().getStringExtra("selectedFuel");
        selectedFuel = "Petrol";
        selectedEuro = "";

        String[] euroCategory = new String[]{
                "RON95",
                "RON97"
        };

        if(selectedModel != null && !selectedModel.isEmpty() && selectedFuel != null && !selectedFuel.isEmpty()){
            TextView tvSelectEuro = findViewById(R.id.textViewSelectEuroCategory);
            tvSelectEuro.setText(tvSelectEuro.getText().toString().replace("auto", selectedBrand + " " + selectedModel));
        }

        ListView listView = findViewById(R.id.listViewEuroCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, euroCategory);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedEuro = adapterView.getItemAtPosition(position).toString();
        });

        db = AppDatabase.getDatabase(getApplicationContext());
        carDao = db.carDao();

        Button btnEuroNext = findViewById(R.id.btnEuroNext);
        btnEuroNext.setOnClickListener(view -> registerCar());
    }

    private void registerCar(){
        SharedPreferences sharedPref = getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE);
        if(selectedEuro != null && !selectedEuro.isEmpty()){
            Car newCar = new Car(0, selectedBrand, selectedModel, selectedFuel, selectedEuro);

            Executors.newSingleThreadExecutor().execute(() -> {
                long carId = carDao.insertCar(newCar);

                new Handler(Looper.getMainLooper()).post(() -> {
                    sharedPref.edit()
                            .putBoolean("firstStart", false)
                            .putString("selectedCarBrand", selectedBrand)
                            .putString("selectedCarModel", selectedModel)
                            .putString("selectedCarFuel", selectedFuel)
                            .putString("selectedCarEuro", selectedEuro)
                            .putLong("selectedCarId", carId)
                            .apply();

                    startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                });
            });
        }else{
            Toast.makeText(this, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
        }
    }
}
