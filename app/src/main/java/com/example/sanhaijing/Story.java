package com.example.sanhaijing;

public class Story {
    private int id;
    private String name;
    private String image;
    private String describe;
    private String type;

    public Story(int id, String name, String image, String describe, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.describe = describe;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescribe() {
        return describe;
    }

    public String getType() {
        return type;
    }
}