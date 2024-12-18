package com.example.weatherforecastd9k.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather/weatherInfo")
    Call<WeatherResponse> getWeather(
        @Query("key") String key,
        @Query("city") String city,
        @Query("extensions") String extensions,
        @Query("output") String output
    );
} 