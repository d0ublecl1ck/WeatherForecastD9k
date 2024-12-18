package com.example.weatherforecastd9k.model;

public class WeatherDetail {
    private int iconRes;
    private String title;
    private String value;

    public WeatherDetail(int iconRes, String title, String value) {
        this.iconRes = iconRes;
        this.title = title;
        this.value = value;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
} 