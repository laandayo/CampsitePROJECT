package com.lan.campsiteproject.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageText;
    public MessageViewHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.message_text);
    }
}