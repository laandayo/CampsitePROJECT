package com.lan.campsiteproject.controller;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.lan.campsiteproject.R;

public class SettingsActivity extends AppCompatActivity {

    private String[] menuItems = {"Profile", "General"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListView menu = findViewById(R.id.settings_menu);
        menu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, menuItems));
        menu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        menu.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            Fragment fragment = position == 0 ? new ProfileSettingsFragment() : new GeneralSettingsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_content, fragment)
                    .commit();
        });

        // Default selection
        menu.performItemClick(menu.getAdapter().getView(0, null, null), 0, menu.getAdapter().getItemId(0));
    }
}