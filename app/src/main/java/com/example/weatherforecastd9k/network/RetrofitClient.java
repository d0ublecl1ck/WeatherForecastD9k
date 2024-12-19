package com.example.weatherforecastd9k.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static Retrofit bingRetrofit;
    private static final String BASE_URL = "https://restapi.amap.com/";
    private static final String BING_BASE_URL = "https://ipgeo-bingpic.hf.space/";
    private static final String API_KEY = "a7bc6e32796cf9d4858d5cbd6c8296db";
    private static Retrofit qrCodeRetrofit;
    private static final String QRCODE_BASE_URL = "https://api.pwmqr.com/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getBingInstance() {
        if (bingRetrofit == null) {
            bingRetrofit = new Retrofit.Builder()
                    .baseUrl(BING_BASE_URL)
                    .build();
        }
        return bingRetrofit;
    }

    public static <T> T create(Class<T> serviceClass) {
        return getInstance().create(serviceClass);
    }

    public static <T> T createBingApi(Class<T> serviceClass) {
        return getBingInstance().create(serviceClass);
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static Retrofit getQrCodeInstance() {
        if (qrCodeRetrofit == null) {
            qrCodeRetrofit = new Retrofit.Builder()
                    .baseUrl(QRCODE_BASE_URL)
                    .build();
        }
        return qrCodeRetrofit;
    }

    public static <T> T createQrCodeApi(Class<T> serviceClass) {
        return getQrCodeInstance().create(serviceClass);
    }
} 