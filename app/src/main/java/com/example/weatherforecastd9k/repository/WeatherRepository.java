package com.example.weatherforecastd9k.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.weatherforecastd9k.data.WeatherDatabase;
import com.example.weatherforecastd9k.data.dao.WeatherDao;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;
import com.example.weatherforecastd9k.network.GeoApi;
import com.example.weatherforecastd9k.network.RetrofitClient;
import com.example.weatherforecastd9k.network.WeatherApi;
import com.example.weatherforecastd9k.network.WeatherResponse;
import com.example.weatherforecastd9k.network.GeoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class WeatherRepository {
    private static final String TAG = "WeatherRepository";
    private final WeatherDao weatherDao;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final WeatherApi weatherApi;
    private final GeoApi geoApi;

    public WeatherRepository(Application application) {
        WeatherDatabase db = WeatherDatabase.getDatabase(application);
        weatherDao = db.weatherDao();
        weatherApi = RetrofitClient.getInstance().getWeatherApi();
        geoApi = RetrofitClient.getInstance().getGeoApi();
        Log.d(TAG, "Repository initialized");
    }

    public LiveData<WeatherEntity> getWeatherByCity(String city) {
        Log.d(TAG, "Getting weather from DB for city: " + city);
        return weatherDao.getWeatherByCity(city);
    }

    public void fetchWeatherFromApi(String cityName, FetchCallback callback) {
        Log.d(TAG, "Converting city name to code: " + cityName);
        
        // 先通过地理编码API获取城市编码
        geoApi.getGeocode(RetrofitClient.getApiKey(), cityName, "JSON")
            .enqueue(new Callback<GeoResponse>() {
                @Override
                public void onResponse(Call<GeoResponse> call, Response<GeoResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<GeoResponse.Geocode> geocodes = response.body().getGeocodes();
                        if (geocodes != null && !geocodes.isEmpty()) {
                            String adcode = geocodes.get(0).getAdcode();
                            Log.d(TAG, "Got city code: " + adcode + " for city: " + cityName);
                            fetchWeatherByCode(adcode, callback);
                        } else {
                            error.postValue("未找到城市: " + cityName);
                            callback.onComplete();
                        }
                    } else {
                        error.postValue("地理编码失败");
                        callback.onComplete();
                    }
                }

                @Override
                public void onFailure(Call<GeoResponse> call, Throwable t) {
                    error.postValue("网络请求失败: " + t.getMessage());
                    callback.onComplete();
                }
            });
    }

    private void fetchWeatherByCode(String cityCode, FetchCallback callback) {
        Log.d(TAG, "Fetching weather for city code: " + cityCode);
        weatherApi.getWeather(RetrofitClient.getApiKey(), cityCode, "base", "JSON")
            .enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    Log.d(TAG, "API response received for city: " + cityCode);
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Raw response: " + response.raw().toString());
                    
                    if (response.isSuccessful()) {
                        WeatherResponse weatherResponse = response.body();
                        Log.d(TAG, "Response body: " + (weatherResponse != null ? weatherResponse.toString() : "null"));
                        
                        if (weatherResponse != null) {
                            Log.d(TAG, "Status: " + weatherResponse.getStatus());
                            Log.d(TAG, "Lives: " + (weatherResponse.getLives() != null ? weatherResponse.getLives().toString() : "null"));
                            
                            if (weatherResponse.isSuccess() && weatherResponse.getLives() != null 
                                && !weatherResponse.getLives().isEmpty()) {
                                WeatherResponse.Live live = weatherResponse.getLives().get(0);
                                WeatherEntity weatherEntity = new WeatherEntity(
                                    cityCode,
                                    live.getProvince(),
                                    live.getWeather(),
                                    live.getTemperature(),
                                    live.getWinddirection(),
                                    live.getWindpower(),
                                    live.getHumidity(),
                                    live.getReporttime()
                                );
                                
                                // 在后台线程中保存数据
                                WeatherDatabase.databaseWriteExecutor.execute(() -> {
                                    weatherDao.insert(weatherEntity);
                                    Log.d(TAG, "Weather data saved to DB for city: " + cityCode);
                                });
                            } else {
                                String errorMsg = "未找到城市 " + cityCode + " 的天气数据";
                                Log.e(TAG, errorMsg + " (Status: " + weatherResponse.getStatus() + 
                                      ", Lives: " + weatherResponse.getLives() + ")");
                                error.postValue(errorMsg);
                            }
                        } else {
                            String errorMsg = "API 返回数据为空";
                            Log.e(TAG, errorMsg);
                            error.postValue(errorMsg);
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "API error response: " + errorBody);
                            error.postValue("获取天气数据失败: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                            error.postValue("获取天气数据失败");
                        }
                    }
                    callback.onComplete();
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    String errorMsg = "网络请求失败：" + t.getMessage();
                    Log.e(TAG, errorMsg);
                    Log.e(TAG, "Failed URL: " + call.request().url());
                    t.printStackTrace();
                    error.postValue(errorMsg);
                    callback.onComplete();
                }
            });
    }

    public LiveData<String> getError() {
        return error;
    }

    public interface FetchCallback {
        void onComplete();
    }
} 