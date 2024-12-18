package com.example.weatherforecastd9k.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weatherforecastd9k.data.entity.WeatherEntity;

@Dao
public interface WeatherDao {
    @Query("SELECT * FROM weather WHERE city = :city")
    LiveData<WeatherEntity> getWeatherByCity(String city);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherEntity weather);

    @Query("DELETE FROM weather WHERE city = :city")
    void deleteByCity(String city);

    @Query("DELETE FROM weather_table WHERE city = :city")
    void deleteWeatherByCity(String city);

    @Query("DELETE FROM weather_table WHERE cityCode = :cityCode")
    void deleteWeatherByCode(String cityCode);
} 