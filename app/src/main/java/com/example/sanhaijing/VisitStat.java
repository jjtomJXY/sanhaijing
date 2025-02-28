package com.example.sanhaijing;

public class VisitStat {
    private int userId;
    private int storyId;
    private String visitDate;
    private int visitCount;

    public VisitStat(int userId, int storyId, String visitDate, int visitCount) {
        this.userId = userId;
        this.storyId = storyId;
        this.visitDate = visitDate;
        this.visitCount = visitCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}