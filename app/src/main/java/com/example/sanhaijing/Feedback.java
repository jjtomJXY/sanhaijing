package com.example.sanhaijing;

public class Feedback {
    private int id;
    private int userId;
    private String content;
    private String email;
    private String date;
    private int status;

    public Feedback(int id, int userId, String content, String email, String date, int status) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.email = email;
        this.date = date;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public String getEmail() { return email; }
    public String getDate() { return date; }
    public int getStatus() { return status; }
}