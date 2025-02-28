package com.example.sanhaijing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Story> histories;
    private OnHistoryClickListener listener;
    private boolean isSelectionMode = false;
    private Set<Integer> selectedItems = new HashSet<>();

    public interface OnHistoryClickListener {
        void onHistoryClick(Story story);
        void onSelectionChanged(int count);
    }

    public HistoryAdapter(List<Story> histories) {
        this.histories = histories;
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Story story = histories.get(position);
        holder.bind(story);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public void setSelectionMode(boolean selectionMode) {
        if (this.isSelectionMode != selectionMode) {
            this.isSelectionMode = selectionMode;
            if (!selectionMode) {
                selectedItems.clear();
                if (listener != null) {
                    listener.onSelectionChanged(0);
                }
            }
            notifyDataSetChanged();
        }
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public void toggleSelectAll(boolean selectAll) {
        selectedItems.clear();
        if (selectAll) {
            for (int i = 0; i < histories.size(); i++) {
                selectedItems.add(i);
            }
        }
        if (listener != null) {
            listener.onSelectionChanged(selectedItems.size());
        }
        notifyDataSetChanged();
    }

    public List<Story> getSelectedStories() {
        List<Story> selected = new ArrayList<>();
        for (Integer position : selectedItems) {
            if (position < histories.size()) {
                selected.add(histories.get(position));
            }
        }
        return selected;
    }

    public void removeItems(List<Story> itemsToRemove) {
        histories.removeAll(itemsToRemove);
        selectedItems.clear();
        if (listener != null) {
            listener.onSelectionChanged(0);
        }
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkbox;
        private ImageView ivStory;
        private TextView tvTitle;
        private TextView tvType;
        private TextView tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            ivStory = itemView.findViewById(R.id.ivStory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (isSelectionMode) {
                        toggleSelection(position);
                    } else if (listener != null) {
                        listener.onHistoryClick(histories.get(position));
                    }
                }
            });

            checkbox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position);
                }
            });
        }

        private void toggleSelection(int position) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position);
            } else {
                selectedItems.add(position);
            }
            if (listener != null) {
                listener.onSelectionChanged(selectedItems.size());
            }
            notifyItemChanged(position);
        }

        public void bind(Story story) {
            tvTitle.setText(story.getName());
            tvType.setText(story.getType());
            // Set date from your data model
            tvDate.setText("最近浏览：" + "今天"); // You should format this properly

            // Set checkbox visibility and state
            checkbox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
            checkbox.setChecked(selectedItems.contains(getAdapterPosition()));
            
            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(story.getImage())
                    .into(ivStory);
        }
    }
}