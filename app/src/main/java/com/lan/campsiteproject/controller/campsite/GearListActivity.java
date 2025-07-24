package com.lan.campsiteproject.controller.campsite;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.GearAdapter;
import com.lan.campsiteproject.controller.campsite.CartManager;
import com.lan.campsiteproject.model.Gear;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GearListActivity extends AppCompatActivity {
    private static final String TAG = "GearListActivity";

    private RecyclerView recyclerView;
    private GearAdapter gearAdapter;
    private EditText searchInput;
    private LinearLayout emptyStateView;

    // ✅ Chỉ cần 2 list đơn giản
    private List<Gear> allGears = new ArrayList<>();
    private List<Gear> displayGears = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_list);

        initViews();
        setupSearch();
        loadGearList();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerGear);
        searchInput = findViewById(R.id.searchInput);
        emptyStateView = findViewById(R.id.emptyStateView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Hide category scroll view
        View categoryScrollView = findViewById(R.id.categoryScrollView);
        if (categoryScrollView != null) {
            categoryScrollView.setVisibility(View.GONE);
        }
    }

    private void setupSearch() {
        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterGears(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterGears(String query) {
        Log.d(TAG, "Filtering with query: '" + query + "'");

        displayGears.clear();

        if (query.isEmpty()) {
            // Nếu không có search, hiển thị tất cả
            displayGears.addAll(allGears);
        } else {
            // Filter theo tên và mô tả
            for (Gear gear : allGears) {
                if (gear != null) {
                    String gearName = gear.getGearName();
                    String gearDesc = gear.getGearDescription();

                    boolean matches = false;
                    if (gearName != null && gearName.toLowerCase().contains(query.toLowerCase())) {
                        matches = true;
                    }
                    if (!matches && gearDesc != null && gearDesc.toLowerCase().contains(query.toLowerCase())) {
                        matches = true;
                    }

                    if (matches) {
                        displayGears.add(gear);
                    }
                }
            }
        }

        Log.d(TAG, "Filtered results: " + displayGears.size() + " items");
        updateAdapter();
    }

    private void updateAdapter() {
        if (gearAdapter == null) {
            // ✅ Tạo adapter với displayGears
            gearAdapter = new GearAdapter(this, displayGears, gear -> {
                if (gear != null) {
                    CartManager.getInstance().addGear(gear, GearListActivity.this);
                    Toast.makeText(GearListActivity.this, "Đã thêm " + gear.getGearName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Added gear to cart: " + gear.getGearName());
                } else {
                    Log.e(TAG, "Attempted to add null gear to cart");
                }
            });
            recyclerView.setAdapter(gearAdapter);
        } else {
            // ✅ Update adapter với list mới
            gearAdapter.updateGears(displayGears);
        }

        // Show/hide empty state
        if (displayGears.isEmpty() && !allGears.isEmpty()) {
            if (emptyStateView != null) emptyStateView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadGearList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("gear")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allGears.clear();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Gear gear = doc.toObject(Gear.class);
                            if (gear != null) {
                                allGears.add(gear);
                                Log.d(TAG, "Loaded gear: " + gear.getGearName());
                            }
                        }

                        Log.d(TAG, "Total gears loaded: " + allGears.size());

                        // Apply current filter (hoặc hiển thị tất cả nếu chưa search)
                        String currentQuery = searchInput != null ? searchInput.getText().toString().trim() : "";
                        filterGears(currentQuery);

                    } else {
                        Log.e(TAG, "Error loading gears: " + task.getException());
                        Toast.makeText(GearListActivity.this, "Lỗi khi tải danh sách gear", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}