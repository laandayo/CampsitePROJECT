package com.lan.campsiteproject.controller.user;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.user.CartManager;
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
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_gear, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Gear gear = gearList.get(position);
        int quantity = cartManager.getGearMap().get(gear);

        holder.gearName.setText(gear.getGearName());
        holder.gearPrice.setText("$" + gear.getGearPrice());
        holder.gearQuantity.setText(String.valueOf(quantity));
        holder.totalPrice.setText("Total: $" + (gear.getGearPrice() * quantity));

        holder.btnIncrease.setOnClickListener(v -> {
            cartManager.updateGearQuantity(gear, quantity + 1);
            notifyItemChanged(position);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = quantity - 1;
            cartManager.updateGearQuantity(gear, newQty);
            if (newQty <= 0) {
                gearList.remove(position);
                notifyItemRemoved(position);
            } else {
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gearList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gearName, gearPrice, totalPrice;
        TextView gearQuantity;
        ImageButton btnIncrease, btnDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gearName = itemView.findViewById(R.id.gearName);
            gearPrice = itemView.findViewById(R.id.gearPrice);
            gearQuantity = itemView.findViewById(R.id.gearQuantity);
            totalPrice = itemView.findViewById(R.id.totalGearPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncreaseGear);
            btnDecrease = itemView.findViewById(R.id.btnDecreaseGear);
        }
    }
}
