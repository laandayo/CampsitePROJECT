package com.lan.campsiteproject.controller.user;

import android.content.Context;
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

        Glide.with(context)
                .load(campsite.getCampImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgCampsite);

        holder.btnOrder.setOnClickListener(v -> {
            cartManager.addCampsite(campsite);
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