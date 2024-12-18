package com.example.weatherforecastd9k.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.weatherforecastd9k.data.WeatherDatabase;
import com.example.weatherforecastd9k.data.dao.WeatherDao;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;

public class WeatherRepository {
    private WeatherDao weatherDao;
    private LiveData<WeatherEntity> currentWeather;
    
    public WeatherRepository(Application application) {
        WeatherDatabase db = WeatherDatabase.getDatabase(application);
        weatherDao = db.weatherDao();
    }
    
    public LiveData<WeatherEntity> getWeatherByCity(String city) {
        return weatherDao.getWeatherByCity(city);
    }
    
    public void insert(WeatherEntity weather) {
        WeatherDatabase.databaseWriteExecutor.execute(() -> {
            weatherDao.insert(weather);
        });
    }
    
    public void fetchWeatherFromApi(String city) {
        // TODO: 实现从高德API获取天气数据
        // 获取数据后通过insert方法存入数据库
    }
} 