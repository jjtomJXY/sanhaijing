package com.example.sanhaijing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categories;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    // Define the preferred category order
    private static final List<String> CATEGORY_ORDER = Arrays.asList(
            "南山经", "西山经", "北山经", "东山经", "中山经", "其他"
    );

    public interface OnCategoryClickListener {
        void onCategoryClick(String category, int position);
    }

    public CategoryAdapter(List<String> categories) {
        // Sort categories according to the predefined order
        List<String> sortedCategories = new ArrayList<>();

        // First add categories in the preferred order
        for (String orderCategory : CATEGORY_ORDER) {
            if (categories.contains(orderCategory)) {
                sortedCategories.add(orderCategory);
            }
        }

        // Add any remaining categories
        for (String category : categories) {
            if (!sortedCategories.contains(category)) {
                sortedCategories.add(category);
            }
        }

        this.categories = sortedCategories;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;
        private LinearLayout categoryContainer;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            categoryContainer = itemView.findViewById(R.id.categoryContainer);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categories.get(position), position);
                }
            });
        }

        public void bind(String category, boolean isSelected) {
            // Convert category text to vertical text
            StringBuilder verticalText = new StringBuilder();
            for (int i = 0; i < category.length(); i++) {
                verticalText.append(category.charAt(i));
                if (i < category.length() - 1) {
                    verticalText.append("\n");
                }
            }
            tvCategory.setText(verticalText.toString());

            // Update selection state
            categoryContainer.setSelected(isSelected);
            tvCategory.setTextColor(isSelected ? 0xFF888A72 : 0xFF666666);
        }
    }
}