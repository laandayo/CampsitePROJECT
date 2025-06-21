package com.lan.campsiteproject.controller.user;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Campsite;
import java.util.List;

public class CampsiteAdapter extends RecyclerView.Adapter<CampsiteAdapter.ViewHolder> {
    private List<Campsite> campsiteList;

    public CampsiteAdapter(List<Campsite> campsiteList) {
        this.campsiteList = campsiteList;
    }

    @NonNull
    @Override
    public CampsiteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_campsite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampsiteAdapter.ViewHolder holder, int position) {
        Campsite campsite = campsiteList.get(position);
        holder.name.setText(campsite.getCampName());
        holder.price.setText("Giá: " + campsite.getCampPrice());
        holder.address.setText("Địa chỉ: " + campsite.getCampAddress());

        try {
            String imageValue = campsite.getCampImage();

            if (!TextUtils.isEmpty(imageValue)) {
                if (imageValue.endsWith(".jpg") || imageValue.endsWith(".png")) {
                    imageValue = imageValue.substring(0, imageValue.lastIndexOf('.'));
                }

                if (imageValue.startsWith("http")) {
                    Glide.with(holder.itemView.getContext())
                            .load(imageValue)
                            .placeholder(R.drawable.default_camp)
                            .error(R.drawable.default_camp)
                            .into(holder.imageView);
                } else {
                    int resId = holder.itemView.getContext().getResources()
                            .getIdentifier(imageValue.trim(), "drawable",
                                    holder.itemView.getContext().getPackageName());

                    if (resId != 0) {
                        holder.imageView.setImageResource(resId);
                    } else {
                        holder.imageView.setImageResource(R.drawable.default_camp);
                    }
                }
            } else {
                holder.imageView.setImageResource(R.drawable.default_camp);
            }

        } catch (Exception e) {
            Log.e("AdapterError", "Lỗi khi load ảnh: " + e.getMessage());
            holder.imageView.setImageResource(R.drawable.default_camp);
        }

        // 👉 Bổ sung phần click để mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(),
                    com.lan.campsiteproject.controller.user.CampsiteDetailActivity.class);
            intent.putExtra("name", campsite.getCampName());
            intent.putExtra("price", campsite.getCampPrice());
            intent.putExtra("address", campsite.getCampAddress());
            intent.putExtra("description", campsite.getCampDescription());
            intent.putExtra("image", campsite.getCampImage());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return campsiteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, address;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.txtCampName);
            price = view.findViewById(R.id.txtCampPrice);
            address = view.findViewById(R.id.txtCampAddress);
            imageView = view.findViewById(R.id.imgCamp);
        }
    }
}
