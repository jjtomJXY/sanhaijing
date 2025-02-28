package com.example.sanhaijing;

public class WeeklyVisitStat {
    private String date;
    private int visitCount;

    public WeeklyVisitStat(String date, int visitCount) {
        this.date = date;
        this.visitCount = visitCount;
    }

    public String getDate() {
        return date;
    }

    public int getVisitCount() {
        return visitCount;
    }
}