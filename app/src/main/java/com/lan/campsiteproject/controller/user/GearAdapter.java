package com.lan.campsiteproject.controller.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.btnBuy.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBuyGear(gear);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gearList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gearName, gearPrice;
        Button btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gearName = itemView.findViewById(R.id.gearName);
            gearPrice = itemView.findViewById(R.id.gearPrice);
            btnBuy = itemView.findViewById(R.id.btnBuyGear);
        }
    }
}
