package com.lan.campsiteproject.controller.user;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.api.ApiService;
import com.lan.campsiteproject.api.GearApi;
import com.lan.campsiteproject.model.Gear;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GearListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GearAdapter gearAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);

        recyclerView = findViewById(R.id.recyclerGear);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadGearList();
    }

    private void loadGearList() {
        Log.d("GearDebug", "Bắt đầu gọi API...");
        ApiService.gearApi.getAllGear().enqueue(new Callback<List<Gear>>() {
            @Override
            public void onResponse(Call<List<Gear>> call, Response<List<Gear>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Gear> gears = response.body();

                    // In ra log từng phần tử để kiểm tra dữ liệu
                    for (Gear gear : gears) {
                        Log.d("GearDebug", "Gear từ API: " +
                                "ID = " + gear.getGearId() +
                                ", Name = " + gear.getGearName() +
                                ", Price = " + gear.getGearPrice());
                    }

                    gearAdapter = new GearAdapter(GearListActivity.this, gears, gear -> {
                        Log.d("GearDebug", "Gear đã thêm vào cart: " + gear.getGearName());
                        CartManager.getInstance().addGear(gear);
                    });

                    recyclerView.setAdapter(gearAdapter);
                } else {
                    Log.e("GearDebug", "API lỗi hoặc body null. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Gear>> call, Throwable t) {
                Log.e("GearDebug", "Lỗi gọi API: " + t.getMessage());
            }
        });
    }


}
