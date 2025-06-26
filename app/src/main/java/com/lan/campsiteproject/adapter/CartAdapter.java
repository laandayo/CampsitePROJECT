package com.lan.campsiteproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.campsite.CartManager;
import com.lan.campsiteproject.model.Gear;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private final List<Gear> gearList;
    private final CartManager cartManager;

    public CartAdapter(Context context, Map<Gear, Integer> gearMap, CartManager cartManager) {
        this.context = context;
        this.gearList = new ArrayList<>(gearMap.keySet());
        this.cartManager = cartManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_gear, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gear gear = gearList.get(position);
        int quantity = cartManager.getGearMap().getOrDefault(gear, 0);

        holder.gearName.setText(gear.getGearName());
        holder.gearPrice.setText("$" + gear.getGearPrice());
        holder.gearQuantity.setText(String.valueOf(quantity));
        holder.totalPrice.setText("Tổng: $" + (gear.getGearPrice() * quantity));

        // ✅ Load hình ảnh gear đúng cách
        String image = gear.getGearImage();
        if (!TextUtils.isEmpty(image)) {
            if (image.startsWith("http")) {
                Glide.with(context)
                        .load(image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.default_gear)
                        .into(holder.imgGear);
            } else {
                if (image.endsWith(".jpg") || image.endsWith(".png")) {
                    image = image.substring(0, image.lastIndexOf('.'));
                }
                int resId = context.getResources().getIdentifier(image.trim(), "drawable", context.getPackageName());
                if (resId != 0) {
                    holder.imgGear.setImageResource(resId);
                } else {
                    holder.imgGear.setImageResource(R.drawable.default_gear);
                }
            }
        } else {
            holder.imgGear.setImageResource(R.drawable.default_gear);
        }

        // Tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            cartManager.updateGearQuantity(gear, quantity + 1);
            notifyItemChanged(position);
        });

        // Giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = quantity - 1;
            if (newQty <= 0) {
                cartManager.removeGear(gear);
                gearList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, gearList.size());
            } else {
                cartManager.updateGearQuantity(gear, newQty);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gearList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gearName, gearPrice, totalPrice, gearQuantity;
        ImageView imgGear;
        ImageButton btnIncrease, btnDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gearName = itemView.findViewById(R.id.gearName);
            gearPrice = itemView.findViewById(R.id.gearPrice);
            totalPrice = itemView.findViewById(R.id.totalGearPrice);
            gearQuantity = itemView.findViewById(R.id.gearQuantity);
            imgGear = itemView.findViewById(R.id.imgGear);
            btnIncrease = itemView.findViewById(R.id.btnIncreaseGear);
            btnDecrease = itemView.findViewById(R.id.btnDecreaseGear);
        }
    }
}
