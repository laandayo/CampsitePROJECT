package com.lan.campsiteproject.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Gear;

import java.util.List;
import java.util.Map;

public class GearOrderAdapter extends RecyclerView.Adapter<GearOrderAdapter.ViewHolder> {
    private final Context context;
    private final List<Map.Entry<Gear, Integer>> gearList;

    public GearOrderAdapter(Context context, List<Map.Entry<Gear, Integer>> gearList) {
        this.context = context;
        this.gearList = gearList;
    }

    @NonNull
    @Override
    public GearOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gear_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GearOrderAdapter.ViewHolder holder, int position) {
        Gear gear = gearList.get(position).getKey();
        int quantity = gearList.get(position).getValue();

        holder.txtName.setText(gear.getGearName());
        holder.txtPrice.setText("Đơn giá: $" + gear.getGearPrice());
        holder.txtQty.setText("Số lượng: " + quantity);

        Glide.with(context)
                .load(gear.getGearImage())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.default_camp)
                .into(holder.imgGear);
    }

    @Override
    public int getItemCount() {
        return gearList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGear;
        TextView txtName, txtPrice, txtQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGear = itemView.findViewById(R.id.imgGearOrder);
            txtName = itemView.findViewById(R.id.txtGearNameOrder);
            txtPrice = itemView.findViewById(R.id.txtGearPriceOrder);
            txtQty = itemView.findViewById(R.id.txtGearQtyOrder);
        }
    }
}
