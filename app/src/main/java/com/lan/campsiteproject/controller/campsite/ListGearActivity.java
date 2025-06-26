package com.lan.campsiteproject.controller.campsite;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.GearAdapter;
import com.lan.campsiteproject.api.ApiService;
import com.lan.campsiteproject.model.Gear;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListGearActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGear;
    private GearAdapter gearAdapter;
    private List<Gear> gearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);

        recyclerViewGear = findViewById(R.id.recyclerGear);
        gearList = new ArrayList<>();
        gearAdapter = new GearAdapter(this, gearList, gear -> {
            CartManager.getInstance().addGear(gear);
            Toast.makeText(this, "Gear added to cart", Toast.LENGTH_SHORT).show();
        });

        recyclerViewGear.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGear.setAdapter(gearAdapter);

        fetchGearFromApi();
    }

    private void fetchGearFromApi() {
        ApiService.gearApi.getAllGear().enqueue(new Callback<List<Gear>>() {
            @Override
            public void onResponse(Call<List<Gear>> call, Response<List<Gear>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gearList.clear();
                    gearList.addAll(response.body());
                    gearAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Gear>> call, Throwable t) {
                Toast.makeText(ListGearActivity.this, "Failed to load gear", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
