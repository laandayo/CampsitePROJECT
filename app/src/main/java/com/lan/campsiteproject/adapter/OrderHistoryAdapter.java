package com.lan.campsiteproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Gear;
import com.lan.campsiteproject.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final OnOrderClickListener listener;
    private final FirebaseFirestore db;
    private static final String TAG = "OrderHistoryAdapter";

    public OrderHistoryAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.txtCampName.setText(order.getCampsite().getCampName());
        holder.txtStatus.setText("Trạng thái: " + order.getStatus());
        holder.txtTotal.setText("Tổng tiền: $" + order.getTotalAmount());

        // Load campsite image
        String image = order.getCampsite().getCampImage();
        if (!TextUtils.isEmpty(image)) {
            if (image.endsWith(".jpg") || image.endsWith(".png")) {
                image = image.substring(0, image.lastIndexOf('.'));
            }

            if (image.startsWith("http")) {
                Glide.with(context)
                        .load(image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.default_camp)
                        .into(holder.imgCamp);
            } else {
                int resId = context.getResources().getIdentifier(image.trim(), "drawable", context.getPackageName());
                if (resId != 0) holder.imgCamp.setImageResource(resId);
                else holder.imgCamp.setImageResource(R.drawable.default_camp);
            }
        } else {
            holder.imgCamp.setImageResource(R.drawable.default_camp);
        }

        // Fetch gear details from Firestore
        List<GearEntry> gearEntries = new ArrayList<>();
        Map<String, Integer> gearMap = order.getGearMap();
        int totalGears = gearMap.size();
        int[] fetchCount = {0}; // Counter for async calls

        if (gearMap.isEmpty()) {
            // If no gears, set adapter immediately
            GearInOrderAdapter gearAdapter = new GearInOrderAdapter(context, gearEntries);
            holder.recyclerGear.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerGear.setAdapter(gearAdapter);
        } else {
            for (Map.Entry<String, Integer> entry : gearMap.entrySet()) {
                String gearId = entry.getKey();
                int quantity = entry.getValue();

                db.collection("gears").document(gearId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Gear gear = documentSnapshot.toObject(Gear.class);
                            if (gear != null) {
                                gearEntries.add(new GearEntry(gear, quantity));
                            }
                            fetchCount[0]++;
                            if (fetchCount[0] == totalGears) {
                                // All gears fetched, set adapter
                                GearInOrderAdapter gearAdapter = new GearInOrderAdapter(context, gearEntries);
                                holder.recyclerGear.setLayoutManager(new LinearLayoutManager(context));
                                holder.recyclerGear.setAdapter(gearAdapter);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to fetch gear: " + gearId + ", error: " + e.getMessage());
                            fetchCount[0]++;
                            if (fetchCount[0] == totalGears) {
                                // Even if some fetches fail, set adapter with available data
                                GearInOrderAdapter gearAdapter = new GearInOrderAdapter(context, gearEntries);
                                holder.recyclerGear.setLayoutManager(new LinearLayoutManager(context));
                                holder.recyclerGear.setAdapter(gearAdapter);
                            }
                        });
            }
        }

        // Handle click to view details
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCamp;
        TextView txtCampName, txtStatus, txtTotal;
        RecyclerView recyclerGear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCamp = itemView.findViewById(R.id.imgOrderCamp);
            txtCampName = itemView.findViewById(R.id.txtOrderCampName);
            txtStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtTotal = itemView.findViewById(R.id.txtOrderTotal);
            recyclerGear = itemView.findViewById(R.id.recyclerGearInOrder);
        }
    }

    public static class GearEntry {
        Gear gear;
        int quantity;

        public GearEntry(Gear gear, int quantity) {
            this.gear = gear;
            this.quantity = quantity;
        }

        public Gear getGear() {
            return gear;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}