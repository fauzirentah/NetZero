package com.cs240.netzero;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TrackActivity extends AppCompatActivity {

    private EditText co2EmissionInput;
    private Button convertButton;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        co2EmissionInput = findViewById(R.id.co2_emission_input);
        convertButton = findViewById(R.id.convert_button);
        resultView = findViewById(R.id.result_view);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double co2Emission = Double.parseDouble(co2EmissionInput.getText().toString());
                double co2e = co2Emission * 44 / 12; // conversion factor from CO2 to CO2e
                int trees = (int) (co2e / 22); // number of trees needed to neutralize the CO2e emissions
                int cars = (int) (co2e / 8.9); // number of cars driven for the same distance
                int flights = (int) (co2e / 0.11); // number of flights taken for the same distance
                int energy = (int) (co2e / 1163); // amount of energy consumed in kilowatt hours
                int waste = (int) (co2e / 102); // amount of waste generated in kilograms

                String result = "To neutralize your CO2 emissions, you need to plant " + trees + " trees.\n\n"
                        + "This is equivalent to driving " + cars + " cars for the same distance, "
                        + "taking " + flights + " flights for the same distance, "
                        + "consuming " + energy + " kWh of energy, or "
                        + "generating " + waste + " kg of waste.";
                resultView.setText(result);
            }
        });
    }
}
