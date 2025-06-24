package com.lan.campsiteproject.controller.user;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.user.CartManager;
import com.lan.campsiteproject.model.Campsite;

import java.util.List;

public class CampsiteAdapter extends RecyclerView.Adapter<CampsiteAdapter.ViewHolder> {
    private Context context;
    private List<Campsite> campsiteList;
    private CartManager cartManager;

    public CampsiteAdapter(Context context, List<Campsite> campsiteList, CartManager cartManager) {
        this.context = context;
        this.campsiteList = campsiteList;
        this.cartManager = cartManager;
    }
    public void updateCampsites(List<Campsite> campsites) {
        this.campsiteList.clear();
        this.campsiteList.addAll(campsites);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_campsite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Campsite campsite = campsiteList.get(position);

        holder.txtName.setText(campsite.getCampName());
        holder.txtPrice.setText("$" + campsite.getCampPrice());
        holder.txtAddress.setText(campsite.getCampAddress());

        try {
            String imageValue = campsite.getCampImage();

            if (!TextUtils.isEmpty(imageValue)) {
                if (imageValue.endsWith(".jpg") || imageValue.endsWith(".png")) {
                    imageValue = imageValue.substring(0, imageValue.lastIndexOf('.'));
                }

                if (imageValue.startsWith("http")) {
                    Glide.with(holder.itemView.getContext())
                            .load(imageValue)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.default_camp)
                            .into(holder.imgCampsite);
                } else {
                    int resId = holder.itemView.getContext().getResources()
                            .getIdentifier(imageValue.trim(), "drawable",
                                    holder.itemView.getContext().getPackageName());

                    if (resId != 0) {
                        holder.imgCampsite.setImageResource(resId);
                    } else {
                        holder.imgCampsite.setImageResource(R.drawable.default_camp);
                    }
                }
            } else {
                holder.imgCampsite.setImageResource(R.drawable.default_camp);
            }
        } catch (Exception e) {
            Log.e("AdapterError", "Lỗi khi load ảnh: " + e.getMessage());
            holder.imgCampsite.setImageResource(R.drawable.default_camp);
        }

        // 👉 Mở chi tiết khi click vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CampsiteDetailActivity.class);
            intent.putExtra("name", campsite.getCampName());
            intent.putExtra("price", campsite.getCampPrice());
            intent.putExtra("address", campsite.getCampAddress());
            intent.putExtra("description", campsite.getCampDescription());
            intent.putExtra("image", campsite.getCampImage());
            context.startActivity(intent);
        });

        // 👉 Khi click nút "Order"
        holder.btnOrder.setOnClickListener(v -> {
            cartManager.addCampsite(campsite);
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        });
    }




    @Override
    public int getItemCount() {
        return campsiteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtAddress;
        ImageView imgCampsite;
        Button btnOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCampName);
            txtPrice = itemView.findViewById(R.id.txtCampPrice);
            txtAddress = itemView.findViewById(R.id.txtCampAddress);
            imgCampsite = itemView.findViewById(R.id.imgCampImage);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}