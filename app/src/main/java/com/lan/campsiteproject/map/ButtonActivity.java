package com.lan.campsiteproject.map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lan.campsiteproject.R;

public class ButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttonmap); // Layout chứa nút

        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(ButtonActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }
}
