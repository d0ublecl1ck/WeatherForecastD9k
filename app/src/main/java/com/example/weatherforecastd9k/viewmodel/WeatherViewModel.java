package com.example.weatherforecastd9k.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecastd9k.data.entity.WeatherEntity;
import com.example.weatherforecastd9k.repository.WeatherRepository;

public class WeatherViewModel extends AndroidViewModel {
    private WeatherRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> error = new MutableLiveData<>();
    
    public WeatherViewModel(Application application) {
        super(application);
        repository = new WeatherRepository(application);
    }
    
    public LiveData<WeatherEntity> getWeatherByCity(String city) {
        return repository.getWeatherByCity(city);
    }
    
    public void refreshWeather(String city) {
        isLoading.setValue(true);
        repository.fetchWeatherFromApi(city);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getError() {
        return error;
    }
} 