package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.api.ApiService;
import com.lan.campsiteproject.model.Campsite;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCampsiteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CampsiteAdapter adapter;
    private CartManager cartManager;
    private TextView cartBadge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcampsite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerViewCampsite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartManager = CartManager.getInstance();

        loadCampsites();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_cart);

        // Inflate thủ công layout action view (khắc phục lỗi null)
        View actionView = getLayoutInflater().inflate(R.layout.cart_action_view, null);
        menuItem.setActionView(actionView);  // Gắn lại layout

        cartBadge = actionView.findViewById(R.id.cart_badge);
        updateCartBadge();

        // Bắt sự kiện click cho actionView
        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }



    private void loadCampsites() {
        // Gắn adapter rỗng trước khi load
        adapter = new CampsiteAdapter(this, new ArrayList<>(), cartManager);
        recyclerView.setAdapter(adapter);

        // Gọi API lấy dữ liệu
        ApiService.campsiteApi.getAllCampsites().enqueue(new Callback<List<Campsite>>() {
            @Override
            public void onResponse(Call<List<Campsite>> call, Response<List<Campsite>> response) {
                Log.d("GearDebug", "URL gọi: " + ApiService.gearApi.getAllGear().request().url());

                if (!isFinishing() && !isDestroyed()) {
                    if (response.isSuccessful() && response.body() != null) {
                        // ✅ Đây là dòng Log khi gọi API thành công
                        Log.d("DEBUG_Campsite", "Số lượng campsite: " + response.body().size());

                        // Gán lại adapter với dữ liệu
                        adapter = new CampsiteAdapter(ListCampsiteActivity.this, response.body(), cartManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        try {
                            // ✅ Đây là dòng Log khi gọi API nhưng trả về lỗi (status 4xx, 5xx)
                            Log.e("DEBUG_Campsite", "Lỗi: " + response.code() + " - " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ListCampsiteActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Campsite>> call, Throwable t) {
                // ✅ Đây là dòng log khi gọi API thất bại hoàn toàn (mất mạng, sai IP...)
                t.printStackTrace(); // In ra lỗi chi tiết
                Toast.makeText(ListCampsiteActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void updateCartBadge() {
        int count = cartManager.getCampsiteQuantity();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}