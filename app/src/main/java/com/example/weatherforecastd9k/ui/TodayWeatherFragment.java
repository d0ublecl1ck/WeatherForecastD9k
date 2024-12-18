package com.example.weatherforecastd9k.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.adapter.WeatherDetailsAdapter;
import com.example.weatherforecastd9k.adapter.FutureWeatherAdapter;
import com.example.weatherforecastd9k.network.RetrofitClient;
import com.example.weatherforecastd9k.network.WeatherApi;
import com.example.weatherforecastd9k.network.WeatherResponse;
import com.example.weatherforecastd9k.util.WeatherUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TodayWeatherFragment extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    private TextView cityName, weatherDesc, temperature;
    private RecyclerView todayDetailsList;
    private RecyclerView futureWeatherList;
    private WeatherDetailsAdapter todayAdapter;
    private FutureWeatherAdapter futureAdapter;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_today_weather, container, false);
        
        initViews(rootView);
        setupRecyclerView();
        fetchWeatherData();
        
        swipeRefresh.setOnRefreshListener(this::fetchWeatherData);
        
        return rootView;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        cityName = view.findViewById(R.id.cityName);
        weatherDesc = view.findViewById(R.id.weatherDesc);
        temperature = view.findViewById(R.id.temperature);
        todayDetailsList = view.findViewById(R.id.todayDetailsList);
        futureWeatherList = view.findViewById(R.id.futureWeatherList);
    }

    private void setupRecyclerView() {
        // 今日详情
        todayDetailsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        todayAdapter = new WeatherDetailsAdapter(null, "");
        todayDetailsList.setAdapter(todayAdapter);

        // 未来天气
        futureWeatherList.setLayoutManager(new LinearLayoutManager(requireContext()));
        futureAdapter = new FutureWeatherAdapter(requireContext(), null, "");
        futureWeatherList.setAdapter(futureAdapter);
    }

    private void updateCurrentWeather(WeatherResponse.Forecast forecast, WeatherResponse.Forecast.Cast todayCast) {
        cityName.setText(forecast.getCity());
        weatherDesc.setText(todayCast.getDayweather());
        temperature.setText(todayCast.getDaytemp() + "°C");
        
        // 设置天气图标
        ImageView weatherIcon = rootView.findViewById(R.id.weatherIcon);
        if (weatherIcon != null) {
            weatherIcon.setImageResource(WeatherUtil.getWeatherIcon(todayCast.getDayweather()));
        }
    }

    private void fetchWeatherData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String cityCode = prefs.getString("city_code", "");
        
        if (cityCode.isEmpty()) {
            Toast.makeText(requireContext(), "请先选择城市", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            return;
        }
        
        RetrofitClient.create(WeatherApi.class)
            .getWeather(RetrofitClient.getApiKey(), cityCode, "all")
            .enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    swipeRefresh.setRefreshing(false);
                    
                    if (response.isSuccessful() && response.body() != null 
                            && response.body().getForecasts() != null 
                            && !response.body().getForecasts().isEmpty()) {
                        
                        WeatherResponse.Forecast forecast = response.body().getForecasts().get(0);
                        
                        // 更新当前天气卡片
                        if (forecast.getCasts() != null && !forecast.getCasts().isEmpty()) {
                            WeatherResponse.Forecast.Cast todayCast = forecast.getCasts().get(0);
                            updateCurrentWeather(forecast, todayCast);
                            
                            // 更新今日天气详情
                            List<WeatherResponse.Forecast.Cast> todayList = new ArrayList<>();
                            todayList.add(todayCast);
                            todayAdapter.updateData(todayList, forecast.getReporttime());
                            
                            // 更新未来天气列表
                            futureAdapter.updateData(forecast.getCasts(), forecast.getReporttime());
                        }
                    } else {
                        Toast.makeText(requireContext(), 
                            "获取天气数据失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(requireContext(), 
                        "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
} 