package com.example.sanhaijing;

public class StoryVisitStat {
    private int storyId;
    private String storyName;
    private int totalVisits;

    public StoryVisitStat(int storyId, String storyName, int totalVisits) {
        this.storyId = storyId;
        this.storyName = storyName;
        this.totalVisits = totalVisits;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getStoryName() {
        return storyName;
    }

    public int getTotalVisits() {
        return totalVisits;
    }
}