package com.lan.campsiteproject.controller.campsite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.lan.campsiteproject.R;

import java.util.ArrayList;

public class FilterCampsiteActivity extends AppCompatActivity {

    private CheckBox checkboxHanoi, checkboxHCM, checkboxDanang;
    private SeekBar seekbarGuests, seekbarPrice;
    private Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_filter);

        // Ánh xạ view

        seekbarGuests = findViewById(R.id.seekbar_quantity);
        seekbarPrice = findViewById(R.id.seekbar_price);
        btnApply = findViewById(R.id.btnApplyFilter);

        // Xử lý khi nhấn "Áp dụng bộ lọc"
        btnApply.setOnClickListener(v -> {
            ArrayList<String> selectedCities = new ArrayList<>();

            if (checkboxHanoi.isChecked()) selectedCities.add("Hà Nội");
            if (checkboxHCM.isChecked()) selectedCities.add("TP. Hồ Chí Minh");
            if (checkboxDanang.isChecked()) selectedCities.add("Đà Nẵng");

            int guests = seekbarGuests.getProgress();
            int minPrice = seekbarPrice.getProgress();
            Intent intent = new Intent(this, FilterCampsiteActivity.class);
            startActivityForResult(intent, 100);


            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selectedCities", selectedCities);
            resultIntent.putExtra("selectedGuests", guests);
            resultIntent.putExtra("selectedMinPrice", minPrice);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
