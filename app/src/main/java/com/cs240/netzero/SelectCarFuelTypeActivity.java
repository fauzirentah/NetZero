package com.cs240.netzero;

        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import com.google.android.material.button.MaterialButtonToggleGroup;

        import java.util.concurrent.atomic.AtomicReference;

public class SelectCarFuelTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car_fuel_type);

        String selectedBrand = getIntent().getStringExtra("selectedBrand");
        String selectedModel = getIntent().getStringExtra("selectedModel");
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleButtonGroup);
        AtomicReference<String> selectedFuel = new AtomicReference<>("");

        if (selectedModel != null && !selectedModel.isEmpty()) {
            TextView tvSelectFuelType = findViewById(R.id.textViewSelectFuelType);
            tvSelectFuelType.setText(tvSelectFuelType.getText().toString().replace("auto", selectedBrand + " " + selectedModel));
        }
        selectedFuel.set("petrol");

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.diesel:
                        selectedFuel.set("diesel");
                        break;
                    case R.id.petrol:
                        selectedFuel.set("petrol");
                        break;
                    case R.id.hybrid:
                        selectedFuel.set("hybrid");
                        break;
                    default:
                        selectedFuel.set("");
                        break;
                }
            } else {
                selectedFuel.set("");
            }
        });

        Button btnFuelNext = findViewById(R.id.btnFuelNext);
        btnFuelNext.setOnClickListener(v -> {
            if (!selectedFuel.get().isEmpty()) {
                Intent intent = new Intent(this, SelectCarPetrolCategoryActivity.class);
                intent.putExtra("selectedBrand", selectedBrand);
                intent.putExtra("selectedModel", selectedModel);
                intent.putExtra("selectedFuel", selectedFuel.get());
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
