package com.example.weatherforecastd9k.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://restapi.amap.com/v3/";
    private static final String API_KEY = "a7bc6e32796cf9d4858d5cbd6c8296db";

    private static RetrofitClient instance;
    private final WeatherApi weatherApi;
    private final GeoApi geoApi;
    
    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();
            
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        weatherApi = retrofit.create(WeatherApi.class);
        geoApi = retrofit.create(GeoApi.class);
    }
    
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    public WeatherApi getWeatherApi() {
        return weatherApi;
    }
    
    public static String getApiKey() {
        return API_KEY;
    }
    
    public GeoApi getGeoApi() {
        return geoApi;
    }
} 