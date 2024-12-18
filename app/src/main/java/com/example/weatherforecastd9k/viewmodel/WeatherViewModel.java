package com.example.weatherforecastd9k.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;
import com.example.weatherforecastd9k.repository.WeatherRepository;

public class WeatherViewModel extends AndroidViewModel {
    private WeatherRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    public WeatherViewModel(Application application) {
        super(application);
        repository = new WeatherRepository(application);
        Log.d("WeatherViewModel", "ViewModel initialized");
    }
    
    public LiveData<WeatherEntity> getWeatherByCity(String city) {
        Log.d("WeatherViewModel", "Getting weather for city: " + city);
        return repository.getWeatherByCity(city);
    }
    
    public void refreshWeather(String city) {
        Log.d("WeatherViewModel", "Refreshing weather for city: " + city);
        isLoading.setValue(true);
        repository.fetchWeatherFromApi(city, new WeatherRepository.FetchCallback() {
            @Override
            public void onComplete() {
                isLoading.setValue(false);
                Log.d("WeatherViewModel", "Weather refresh completed for city: " + city);
            }
        });
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getError() {
        return repository.getError();
    }
} 