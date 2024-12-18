package com.example.weatherforecastd9k.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoApi {
    @GET("geocode/geo")
    Call<GeoResponse> getGeocode(
        @Query("key") String key,
        @Query("address") String address,
        @Query("output") String output
    );
} 