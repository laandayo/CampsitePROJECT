package com.lan.campsiteproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Gear;

import java.util.List;

public class GearInOrderAdapter extends RecyclerView.Adapter<GearInOrderAdapter.GearViewHolder> {

    private final Context context;
    private final List<OrderHistoryAdapter.GearEntry> gearEntries;

    public GearInOrderAdapter(Context context, List<OrderHistoryAdapter.GearEntry> gearEntries) {
        this.context = context;
        this.gearEntries = gearEntries;
    }

    @NonNull
    @Override
    public GearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gear_in_order, parent, false);
        return new GearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GearViewHolder holder, int position) {
        OrderHistoryAdapter.GearEntry entry = gearEntries.get(position);
        Gear gear = entry.gear;
        int quantity = entry.quantity;

        holder.gearName.setText(gear.getGearName());
        holder.gearPrice.setText("Giá: $" + gear.getGearPrice());
        holder.gearQuantity.setText("Số lượng: " + quantity);

        try {
            String image = gear.getGearImage();
            if (!TextUtils.isEmpty(image)) {
                if (image.endsWith(".jpg") || image.endsWith(".png")) {
                    image = image.substring(0, image.lastIndexOf('.'));
                }

                if (image.startsWith("http")) {
                    Glide.with(context)
                            .load(image)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.default_gear)
                            .into(holder.imgGear);
                } else {
                    int resId = context.getResources().getIdentifier(image.trim(), "drawable", context.getPackageName());
                    if (resId != 0) holder.imgGear.setImageResource(resId);
                    else holder.imgGear.setImageResource(R.drawable.default_gear);
                }
            } else {
                holder.imgGear.setImageResource(R.drawable.default_gear);
            }
        } catch (Exception e) {
            holder.imgGear.setImageResource(R.drawable.default_gear);
        }
    }

    @Override
    public int getItemCount() {
        return gearEntries.size();
    }

    public static class GearViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGear;
        TextView gearName, gearPrice, gearQuantity;

        public GearViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGear = itemView.findViewById(R.id.imgGearInOrder);
            gearName = itemView.findViewById(R.id.txtGearNameInOrder);
            gearPrice = itemView.findViewById(R.id.txtGearPriceInOrder);
            gearQuantity = itemView.findViewById(R.id.txtGearQuantityInOrder);
        }
    }
}