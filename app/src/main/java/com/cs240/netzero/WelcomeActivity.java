package com.cs240.netzero;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btnWelcomeNext).setOnClickListener(view -> {
            String userName = ((EditText) findViewById(R.id.editTextUserName)).getText().toString();
            if (!userName.isEmpty()) {
                getSharedPreferences("com.cs240.netzero", Context.MODE_PRIVATE)
                        .edit()
                        .putString("userName", userName)
                        .apply();
                Intent intent = new Intent(this, SelectCarBrandActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
