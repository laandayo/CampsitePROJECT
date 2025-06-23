package com.lan.campsiteproject.controller.order;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Order;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final Context context;
    private final List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
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
        holder.txtStatus.setText("Trạng thái: " + order.getStatus());
        holder.txtTotal.setText("Tổng tiền: $" + order.getTotal());

        if (order.getCampsite() != null) {
            holder.txtCampName.setText("Campsite: " + order.getCampsite().getCampName());
            Glide.with(context)
                    .load(order.getCampsite().getCampImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgCamp);
        }

        // Nếu muốn hiển thị thêm Gear trong mỗi order, có thể mở rộng tại đây
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStatus, txtTotal, txtCampName;
        ImageView imgCamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtTotal = itemView.findViewById(R.id.txtOrderTotal);
            txtCampName = itemView.findViewById(R.id.txtOrderCampName);
            imgCamp = itemView.findViewById(R.id.imgOrderCamp);
        }
    }
}
