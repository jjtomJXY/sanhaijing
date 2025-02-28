package com.example.sanhaijing;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Story> bannerStories;
    private List<String> images = List.of(
            "https://img.zcool.cn/community/01qzuor2aytuhswrw9di5k3832.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_1280,limit_1/sharpen,100",
            "https://bpic.588ku.com/element_origin_min_pic/20/07/04/7f42d7d7704f7a1bbc2585c82a1b0672.jpg",
            "https://img.zcool.cn/community/01be855fbdb60c11013fdcc7d2aa46.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_1280,limit_1/sharpen,100",
            "https://pic3.zhimg.com/v2-75fae24051c1b277468c928a3b56827c_1440w.jpg"
    );

    public BannerAdapter(List<Story> bannerStories) {
        this.bannerStories = bannerStories;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Story story = bannerStories.get(position);
        Glide.with(holder.itemView.getContext())
                .load(images.get(position))
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), StoryDetailActivity.class);
//                intent.putExtra("story_id", story.getId());
//                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerStories.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivBanner);
        }
    }
}