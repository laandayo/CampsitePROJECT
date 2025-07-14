package com.lan.campsiteproject.controller.campsite;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.GearAdapter;
import com.lan.campsiteproject.model.Gear;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GearListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GearAdapter gearAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);

        recyclerView = findViewById(R.id.recyclerGear);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadGearList();
    }

    private void loadGearList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("gear")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Gear> gears = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Gear gear = doc.toObject(Gear.class);
                            if (gear != null) {
                                gears.add(gear);
                                Log.d("GearListActivity", "Loaded gear: " + gear.getGearName());
                            }
                        }
                        Log.d("GearListActivity", "Total gears loaded: " + gears.size());
                        gearAdapter = new GearAdapter(this, gears, gear -> {
                            if (gear != null) {
                                CartManager.getInstance().addGear(gear, GearListActivity.this);
                                Toast.makeText(GearListActivity.this, "Đã thêm " + gear.getGearName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                Log.d("GearListActivity", "Added gear to cart: " + gear.getGearName());
                            } else {
                                Log.e("GearListActivity", "Attempted to add null gear to cart");
                            }
                        });
                        recyclerView.setAdapter(gearAdapter);
                    } else {
                        Log.e("GearListActivity", "Error loading gears: " + task.getException());
                        Toast.makeText(GearListActivity.this, "Lỗi khi tải danh sách gear", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}