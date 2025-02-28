package com.example.sanhaijing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VisitRankingAdapter extends RecyclerView.Adapter<VisitRankingAdapter.ViewHolder> {
    private List<StoryVisitStat> ranking;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int storyId);
    }

    public VisitRankingAdapter(List<StoryVisitStat> ranking) {
        this.ranking = ranking;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visit_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryVisitStat stat = ranking.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(stat.getStoryName());
        holder.tvVisitCount.setText(String.format("阅读 %d 次", stat.getTotalVisits()));

        // Set background colors for top 3
        if (position == 0) {
            holder.tvRank.setBackgroundResource(R.drawable.bg_rank_first);
        } else if (position == 1) {
            holder.tvRank.setBackgroundResource(R.drawable.bg_rank_second);
        } else if (position == 2) {
            holder.tvRank.setBackgroundResource(R.drawable.bg_rank_third);
        } else {
            holder.tvRank.setBackgroundResource(R.drawable.bg_rank_normal);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(stat.getStoryId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ranking.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvTitle;
        TextView tvVisitCount;

        ViewHolder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tvRank);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvVisitCount = view.findViewById(R.id.tvVisitCount);
        }
    }
}