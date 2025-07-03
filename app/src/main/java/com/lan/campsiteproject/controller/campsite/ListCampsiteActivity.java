package com.lan.campsiteproject.controller.campsite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.CampsiteAdapter;
import com.lan.campsiteproject.controller.user.ChatListActivity;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.controller.user.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class ListCampsiteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CampsiteAdapter adapter;
    private CartManager cartManager;
    private TextView cartBadge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcampsite);

        Button multiActionButton = findViewById(R.id.multiActionButton);
        multiActionButton.setOnLongClickListener(v -> {
            View popupView = LayoutInflater.from(this).inflate(R.layout.popup_multi_action, null);
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // Set up each button
            popupView.findViewById(R.id.btnChat).setOnClickListener(btn -> {
                startActivity(new Intent(this, ChatListActivity.class));
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnMap).setOnClickListener(btn -> {
                startActivity(new Intent(this, com.lan.campsiteproject.map.MapActivity.class));
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnCart).setOnClickListener(btn -> {
                startActivity(new Intent(this, CartActivity.class));
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnOrder).setOnClickListener(btn -> {
                startActivity(new Intent(this, com.lan.campsiteproject.controller.orders.OrderHistoryActivity.class));
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnProfile).setOnClickListener(btn -> {
                startActivity(new Intent(this, com.lan.campsiteproject.controller.user.ProfileActivity.class));
            });
            popupView.findViewById(R.id.btnLogout).setOnClickListener(btn -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnSettings).setOnClickListener(btn -> {
                startActivity(new Intent(this, SettingsActivity.class));
                popupWindow.dismiss();
            });
            popupWindow.showAsDropDown(multiActionButton, -100, -200);
            return true;
        });

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



    // Replace loadCampsites() in ListCampsiteActivity
    private void loadCampsites() {
        adapter = new CampsiteAdapter(this, new ArrayList<>(), cartManager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("campsites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Campsite> campsites = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Campsite campsite = doc.toObject(Campsite.class);
                            campsites.add(campsite);
                        }
                        adapter.updateCampsites(campsites);
                    } else {
                        Toast.makeText(this, "Failed to load campsites", Toast.LENGTH_SHORT).show();
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