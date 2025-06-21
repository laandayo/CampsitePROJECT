package com.lan.campsiteproject.controller.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;

public class CampsiteDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, price, address, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite_detail);

        imageView = findViewById(R.id.imgDetail);
        name = findViewById(R.id.txtDetailName);
        price = findViewById(R.id.txtDetailPrice);
        address = findViewById(R.id.txtDetailAddress);
        description = findViewById(R.id.txtDetailDescription);

        // Nhận dữ liệu từ Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name.setText(bundle.getString("name"));
            price.setText("Giá: " + bundle.getInt("price"));
            address.setText("Địa chỉ: " + bundle.getString("address"));
            description.setText("Mô tả: " + bundle.getString("description"));

            String imageValue = bundle.getString("image");
            if (imageValue != null && imageValue.endsWith(".jpg"))
                imageValue = imageValue.substring(0, imageValue.lastIndexOf('.'));

            int resId = getResources().getIdentifier(imageValue, "drawable", getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(R.drawable.default_camp);
            }
        }
    }
}
