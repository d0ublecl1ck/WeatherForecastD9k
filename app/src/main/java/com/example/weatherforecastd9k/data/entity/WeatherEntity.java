package com.example.weatherforecastd9k.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "weather")
public class WeatherEntity {
    @PrimaryKey
    @NonNull
    private String city;
    private String weather;
    private String temperature;
    private String windDirection;
    private String windPower;
    private String humidity;
    private String reportTime;
    private long timestamp;

    // 构造函数
    public WeatherEntity(String city, String weather, String temperature, 
                        String windDirection, String windPower, 
                        String humidity, String reportTime) {
        this.city = city;
        this.weather = weather;
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.windPower = windPower;
        this.humidity = humidity;
        this.reportTime = reportTime;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getCity() {
        return city;
    }

    public String getWeather() {
        return weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindPower() {
        return windPower;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getReportTime() {
        return reportTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setCity(String city) {
        this.city = city;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 