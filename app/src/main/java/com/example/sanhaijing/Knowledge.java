package com.example.sanhaijing;

public class Knowledge {
    private int id;
    private String name;
    private String image;
    private String describe;

    public Knowledge(int id, String name, String image, String describe) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.describe = describe;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getDescribe() { return describe; }
}