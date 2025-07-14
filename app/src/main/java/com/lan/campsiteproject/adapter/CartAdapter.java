package com.lan.campsiteproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
    private OnTotalPriceChangeListener totalPriceChangeListener;

    public interface OnTotalPriceChangeListener {
        void onTotalPriceChanged();
    }

    public CartAdapter(Context context, Map<Gear, Integer> gearMap, CartManager cartManager) {
        this.context = context;
        this.gearList = new ArrayList<>(gearMap.keySet());
        this.cartManager = cartManager;
        Log.d("CartAdapter", "Initialized with gearMap size: " + gearMap.size());
    }

    public void setOnTotalPriceChangeListener(OnTotalPriceChangeListener listener) {
        this.totalPriceChangeListener = listener;
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
        double gearPrice = gear.getGearPrice();
        double totalGearPrice = gearPrice * quantity;

        holder.gearName.setText(gear.getGearName());
        holder.gearPrice.setText(String.format("$%.2f", gearPrice));
        holder.gearQuantity.setText(String.valueOf(quantity));
        holder.totalPrice.setText(String.format("Tổng: $%.2f", totalGearPrice));
        Log.d("CartAdapter", "Binding gear: " + gear.getGearName() + ", quantity: " + quantity);

        loadGearImage(holder, gear);
        setupClickListeners(holder, gear, position);
    }

    private void loadGearImage(ViewHolder holder, Gear gear) {
        String image = gear.getGearImage();
        if (!TextUtils.isEmpty(image)) {
            if (image.startsWith("http")) {
                Glide.with(context)
                        .load(image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.default_gear)
                        .centerCrop()
                        .into(holder.imgGear);
            } else {
                if (image.endsWith(".jpg") || image.endsWith(".png")) {
                    image = image.substring(0, image.lastIndexOf('.'));
                }
                int resId = context.getResources().getIdentifier(image.trim(), "drawable", context.getPackageName());
                holder.imgGear.setImageResource(resId != 0 ? resId : R.drawable.default_gear);
            }
        } else {
            holder.imgGear.setImageResource(R.drawable.default_gear);
        }
    }

    private void setupClickListeners(ViewHolder holder, Gear gear, int position) {
        holder.btnIncrease.setOnClickListener(v -> {
            int newQty = cartManager.getGearMap().getOrDefault(gear, 0) + 1;
            cartManager.updateGearQuantity(gear, newQty, context);
            notifyItemChanged(position);
            if (totalPriceChangeListener != null) totalPriceChangeListener.onTotalPriceChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int oldQty = cartManager.getGearMap().getOrDefault(gear, 0);
            int newQty = oldQty - 1;
            if (newQty <= 0) {
                cartManager.removeGear(gear, context);
                gearList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, gearList.size());
            } else {
                cartManager.updateGearQuantity(gear, newQty, context);
                notifyItemChanged(position);
            }
            if (totalPriceChangeListener != null) totalPriceChangeListener.onTotalPriceChanged();
        });

        setupButtonEffects(holder);
    }

    private void setupButtonEffects(ViewHolder holder) {
        View.OnTouchListener touchEffect = (v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.7f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        };

        holder.btnIncrease.setOnTouchListener(touchEffect);
        holder.btnDecrease.setOnTouchListener(touchEffect);
    }

    @Override
    public int getItemCount() {
        Log.d("CartAdapter", "getItemCount: " + gearList.size());
        return gearList.size();
    }

    public void updateGearList(Map<Gear, Integer> newGearMap) {
        this.gearList.clear();
        this.gearList.addAll(newGearMap.keySet());
        notifyDataSetChanged();
        Log.d("CartAdapter", "updateGearList: New gearMap size: " + newGearMap.size());
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