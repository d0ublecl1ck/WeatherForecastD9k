package com.example.weatherforecastd9k.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://restapi.amap.com/";
    private static final String API_KEY = "a7bc6e32796cf9d4858d5cbd6c8296db";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <T> T create(Class<T> serviceClass) {
        return getInstance().create(serviceClass);
    }

    public static String getApiKey() {
        return API_KEY;
    }
} 