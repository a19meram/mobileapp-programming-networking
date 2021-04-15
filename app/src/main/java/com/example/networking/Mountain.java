package com.example.networking;

public class Mountain {
    private String name;
    private int size;
    private String location;

    public Mountain(String name, int size, String location) {
        this.name = name;
        this.size = size;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }
}
