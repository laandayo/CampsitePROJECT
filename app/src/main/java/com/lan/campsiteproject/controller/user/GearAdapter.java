package com.lan.campsiteproject.controller.user;

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
import com.lan.campsiteproject.model.Gear;

import java.util.List;

public class GearAdapter extends RecyclerView.Adapter<GearAdapter.ViewHolder> {

    public interface OnGearClickListener {
        void onBuyGear(Gear gear);
    }

    private final Context context;
    private final List<Gear> gearList;
    private final OnGearClickListener listener;

    public GearAdapter(Context context, List<Gear> gearList, OnGearClickListener listener) {
        this.context = context;
        this.gearList = gearList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gear, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gear gear = gearList.get(position);
        holder.gearName.setText(gear.getGearName());
        holder.gearPrice.setText("$" + gear.getGearPrice());

        // ✅ Load hình ảnh Gear bằng Glide
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
                    if (resId != 0) {
                        holder.imgGear.setImageResource(resId);
                    } else {
                        holder.imgGear.setImageResource(R.drawable.default_gear);
                    }
                }
            } else {
                holder.imgGear.setImageResource(R.drawable.default_gear);
            }

        } catch (Exception e) {
            holder.imgGear.setImageResource(R.drawable.default_gear);
        }

        holder.btnBuy.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBuyGear(gear);
                Toast.makeText(context, "Đã thêm " + gear.getGearName() + " vào giỏ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gearList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gearName, gearPrice;
        ImageView imgGear;
        Button btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gearName = itemView.findViewById(R.id.gearName);
            gearPrice = itemView.findViewById(R.id.gearPrice);
            imgGear = itemView.findViewById(R.id.imgGear); // cần có trong item_gear.xml
            btnBuy = itemView.findViewById(R.id.btnBuyGear);
        }
    }
}
