package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Campsite;

public class CampsiteDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, price, address, description;
    Button btnOrderDetail;
    CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite_detail);

        imageView = findViewById(R.id.imgDetail);
        name = findViewById(R.id.txtDetailName);
        price = findViewById(R.id.txtDetailPrice);
        address = findViewById(R.id.txtDetailAddress);
        description = findViewById(R.id.txtDetailDescription);
        btnOrderDetail = findViewById(R.id.btnOrderDetail);

        cartManager = CartManager.getInstance();

        // Nhận dữ liệu từ Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String nameStr = bundle.getString("name");
            int priceVal = bundle.getInt("price");
            String addrStr = bundle.getString("address");
            String descStr = bundle.getString("description");
            String imgUrl = bundle.getString("image");

            name.setText(nameStr);
            price.setText("Giá: " + priceVal);
            address.setText("Địa chỉ: " + addrStr);
            description.setText("Mô tả: " + descStr);

            // Load ảnh bằng Glide
            Glide.with(this)
                    .load(imgUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.default_camp)
                    .into(imageView);

            // Xử lý khi nhấn nút Order
            btnOrderDetail.setOnClickListener(v -> {
                Campsite campsite = new Campsite();
                campsite.setCampName(nameStr);
                campsite.setCampPrice(priceVal);
                campsite.setCampAddress(addrStr);
                campsite.setCampDescription(descStr);
                campsite.setCampImage(imgUrl);

                cartManager.addCampsite(campsite);

                Intent intent = new Intent(CampsiteDetailActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }
    }
}
