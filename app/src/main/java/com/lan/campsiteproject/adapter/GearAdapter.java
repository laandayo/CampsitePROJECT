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
import com.lan.campsiteproject.model.Gear;

import java.util.ArrayList;
import java.util.List;

public class GearAdapter extends RecyclerView.Adapter<GearAdapter.ViewHolder> {
    private static final String TAG = "GearAdapter";

    public interface OnGearClickListener {
        void onBuyGear(Gear gear);
    }

    private final Context context;
    private final List<Gear> gearList;
    private final OnGearClickListener listener;

    public GearAdapter(Context context, List<Gear> gearList, OnGearClickListener listener) {
        this.context = context;
        // ✅ Không cần defensive copy ở constructor vì chúng ta sẽ tạo ở updateGears
        this.gearList = gearList;
        this.listener = listener;

        Log.d(TAG, "GearAdapter created with " + gearList.size() + " items");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_gear, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);

        if (position >= gearList.size()) {
            Log.e(TAG, "Position " + position + " is out of bounds for list size " + gearList.size());
            return;
        }

        Gear gear = gearList.get(position);
        if (gear == null) {
            Log.e(TAG, "Gear at position " + position + " is null");
            return;
        }

        Log.d(TAG, "Binding gear: " + gear.getGearName() + " at position " + position);

        // Set gear name
        if (holder.gearName != null && gear.getGearName() != null) {
            holder.gearName.setText(gear.getGearName());
            Log.d(TAG, "Set gear name: " + gear.getGearName());
        } else {
            Log.w(TAG, "Cannot set gear name - holder.gearName: " + (holder.gearName != null) + ", gear.getGearName(): " + (gear.getGearName() != null));
        }

        // Set gear price
        if (holder.gearPrice != null) {
            holder.gearPrice.setText("$" + gear.getGearPrice());
            Log.d(TAG, "Set gear price: $" + gear.getGearPrice());
        } else {
            Log.w(TAG, "Cannot set gear price - holder.gearPrice is null");
        }

        // ✅ Load hình ảnh Gear bằng Glide
        if (holder.imgGear != null) {
            try {
                String image = gear.getGearImage();
                Log.d(TAG, "Loading image: " + image);

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
                        Log.d(TAG, "Loading image from URL: " + image);
                    } else {
                        int resId = context.getResources().getIdentifier(image.trim(), "drawable", context.getPackageName());
                        if (resId != 0) {
                            holder.imgGear.setImageResource(resId);
                            Log.d(TAG, "Loaded image from resources: " + image);
                        } else {
                            holder.imgGear.setImageResource(R.drawable.default_gear);
                            Log.w(TAG, "Image resource not found: " + image + ", using default");
                        }
                    }
                } else {
                    holder.imgGear.setImageResource(R.drawable.default_gear);
                    Log.d(TAG, "Empty image, using default");
                }

            } catch (Exception e) {
                holder.imgGear.setImageResource(R.drawable.default_gear);
                Log.e(TAG, "Error loading image: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "imgGear is null, cannot load image");
        }

        // Set click listener
        if (holder.btnBuy != null) {
            holder.btnBuy.setOnClickListener(v -> {
                Log.d(TAG, "Buy button clicked for: " + gear.getGearName());
                if (listener != null) {
                    listener.onBuyGear(gear);
                } else {
                    Log.w(TAG, "OnGearClickListener is null");
                }
            });
        } else {
            Log.w(TAG, "btnBuy is null, cannot set click listener");
        }
    }

    @Override
    public int getItemCount() {
        int count = gearList.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    // ✅ FIX 2: Method để update danh sách gear cho search/filter
    public void updateGears(List<Gear> newGears) {
        Log.d(TAG, "updateGears called - old size: " + gearList.size() + ", new size: " + newGears.size());

        // ✅ Tạo defensive copy để tránh reference conflict
        List<Gear> copyList = new ArrayList<>(newGears);

        this.gearList.clear();
        this.gearList.addAll(copyList);
        notifyDataSetChanged();

        Log.d(TAG, "updateGears completed - final size: " + gearList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gearName, gearPrice;
        ImageView imgGear;
        Button btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gearName = itemView.findViewById(R.id.gearName);
            gearPrice = itemView.findViewById(R.id.gearPrice);
            imgGear = itemView.findViewById(R.id.imgGear);
            btnBuy = itemView.findViewById(R.id.btnBuyGear);

            // Log which views were found
            Log.d("ViewHolder", "Views found - gearName: " + (gearName != null) +
                    ", gearPrice: " + (gearPrice != null) +
                    ", imgGear: " + (imgGear != null) +
                    ", btnBuy: " + (btnBuy != null));
        }
    }
}