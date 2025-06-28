package com.lan.campsiteproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Message;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private List<Message> messages;
    private String currentUserId;
    private static final long TIME_GAP_THRESHOLD_MS = 10 * 60 * 1000;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        boolean showTimestamp = false;
        long gap = 0;
        if (position == 0) {
            showTimestamp = true;
        } else {
            Message prev = messages.get(position - 1);
            gap = message.getTimestamp().toDate().getTime() - prev.getTimestamp().toDate().getTime();
            if (gap > TIME_GAP_THRESHOLD_MS) {
                showTimestamp = true;
            }
        }

        String content = message.getContent() != null ? message.getContent() : "";
        String timeText;
        if (gap > 365L * 24 * 60 * 60 * 1000) {
            timeText = new java.text.SimpleDateFormat("HH:mm, dd MMM yyyy", java.util.Locale.getDefault())
                    .format(message.getTimestamp().toDate());
        } else {
            timeText = new java.text.SimpleDateFormat("HH:mm, dd MMM", java.util.Locale.getDefault())
                    .format(message.getTimestamp().toDate());
        }

        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).messageText.setText(content);
            TextView timestampView = holder.itemView.findViewById(R.id.timestamp);
            if (showTimestamp) {
                timestampView.setVisibility(View.VISIBLE);
                timestampView.setText(timeText);
            } else {
                timestampView.setVisibility(View.GONE);
            }

            TextView statusView = holder.itemView.findViewById(R.id.message_status);
            if (message.getStatus() != null && !message.getStatus().isEmpty()) {
                statusView.setVisibility(View.VISIBLE);
                statusView.setText(message.getStatus());
            } else {
                statusView.setVisibility(View.GONE);
            }
        } else if (holder instanceof ReceivedViewHolder) {
            ((ReceivedViewHolder) holder).messageText.setText(content);
            TextView timestampView = holder.itemView.findViewById(R.id.timestamp);
            if (showTimestamp) {
                timestampView.setVisibility(View.VISIBLE);
                timestampView.setText(timeText);
            } else {
                timestampView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        SentViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ReceivedViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }
}