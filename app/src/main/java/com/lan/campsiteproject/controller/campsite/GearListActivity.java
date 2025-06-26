package com.lan.campsiteproject.controller.campsite;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class GearListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GearAdapter gearAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);

        recyclerView = findViewById(R.id.recyclerGear);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 cột


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
                            gears.add(gear);
                        }
                        gearAdapter = new GearAdapter(this, gears, gear -> {
                            CartManager.getInstance().addGear(gear);
                        });
                        recyclerView.setAdapter(gearAdapter);
                    } else {
                        // Handle error
                    }
                });
    }


}
