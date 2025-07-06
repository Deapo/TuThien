package com.example.tuibikho.data;

import androidx.room.Entity;

public class NutritionInfo {
    private String name;
    private String value;

    // Constructor, getters, and setters

    public NutritionInfo() {}


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
