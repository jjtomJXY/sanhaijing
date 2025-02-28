package com.example.sanhaijing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.tvContent.setText(feedback.getContent());
        holder.tvDate.setText(feedback.getDate());
//        holder.tvStatus.setText(feedback.getStatus() == 0 ? "待处理" : "已处理");
//        holder.tvStatus.setTextColor(holder.itemView.getContext().getResources()
//                .getColor(feedback.getStatus() == 0 ? R.color.pending : R.color.processed));
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvDate;
//        TextView tvStatus;

        ViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tvContent);
            tvDate = view.findViewById(R.id.tvDate);
//            tvStatus = view.findViewById(R.id.tvStatus);
        }
    }
}