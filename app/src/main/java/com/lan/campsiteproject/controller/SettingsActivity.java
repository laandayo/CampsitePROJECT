package com.lan.campsiteproject.controller;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.lan.campsiteproject.controller.ProfileSettingsFragment;
import com.lan.campsiteproject.controller.GeneralSettingsFragment;
import com.lan.campsiteproject.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnGeneral = findViewById(R.id.btnGeneral);
        ImageButton btnReturn = findViewById(R.id.btnReturn);

        btnProfile.setOnClickListener(v -> getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new ProfileSettingsFragment())
                .commit());

        btnGeneral.setOnClickListener(v -> getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new GeneralSettingsFragment())
                .commit());

        btnReturn.setOnClickListener(v -> finish());

        // Load default fragment (Profile)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_content, new ProfileSettingsFragment())
                    .commit();
        }
    }
}