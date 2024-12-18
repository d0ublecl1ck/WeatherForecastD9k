package com.example.weatherforecastd9k.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import android.app.Application;

import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.adapter.WeatherDetailAdapter;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;
import com.example.weatherforecastd9k.model.WeatherDetail;
import com.example.weatherforecastd9k.viewmodel.WeatherViewModel;
import com.example.weatherforecastd9k.network.RetrofitClient;
import com.example.weatherforecastd9k.network.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

public class TodayWeatherFragment extends Fragment {
    private static final String TAG = "WeatherDebug";
    
    private WeatherViewModel weatherViewModel;
    private WeatherDetailAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvLocation;
    private TextView tvTemperature;
    private TextView tvWeatherDesc;
    private TextView tvUpdateTime;
    private RecyclerView rvWeatherDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);
        
        // 修改为使用 Application 作用域的 ViewModel
        weatherViewModel = new ViewModelProvider(requireActivity())
            .get(WeatherViewModel.class);
        
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        observeWeatherData();
        
        // 添加日志
        Log.d(TAG, "TodayWeatherFragment created");
        
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 移除重复的初始化代码
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String city = prefs.getString("default_city", "330100");
        Log.d(TAG, "Initial weather fetch for city: " + city);
        weatherViewModel.refreshWeather(city);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "TodayWeatherFragment onResume");
        
        // 获取默认城市并刷新天气数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String city = prefs.getString("default_city", "330100");
        Log.d(TAG, "Fetching weather data for city: " + city);
        weatherViewModel.refreshWeather(city);
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvWeatherDesc = view.findViewById(R.id.tvWeatherDesc);
        tvUpdateTime = view.findViewById(R.id.tvUpdateTime);
        rvWeatherDetails = view.findViewById(R.id.rvWeatherDetails);
    }
    
    private void setupRecyclerView() {
        adapter = new WeatherDetailAdapter();
        rvWeatherDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWeatherDetails.setAdapter(adapter);
        
        adapter.setOnItemClickListener(position -> {
            WeatherDetail detail = adapter.getDetails().get(position);
            showDetailDialog(detail);
        });
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String city = prefs.getString("default_city", "330100");
            weatherViewModel.refreshWeather(city);
        });
        
        weatherViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading);
        });
    }
    
    private void showDetailDialog(WeatherDetail detail) {
        new AlertDialog.Builder(requireContext())
            .setTitle(detail.getTitle())
            .setMessage(detail.getValue())
            .setPositiveButton("确定", null)
            .show();
    }

    private void observeWeatherData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String city = prefs.getString("default_city", "320100");
        
        // 添加日志
        Log.d(TAG, "Setting up weather observer for city: " + city);
        
        weatherViewModel.getWeatherByCity(city).observe(getViewLifecycleOwner(), weather -> {
            Log.d(TAG, "Weather data received: " + (weather != null ? weather.toString() : "null"));
            if (weather != null) {
                updateUI(weather);
            }
        });
        
        // 添加加载状态观察
        weatherViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state changed: " + isLoading);
            swipeRefresh.setRefreshing(isLoading);
        });
        
        weatherViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Weather error: " + error);
                new AlertDialog.Builder(requireContext())
                    .setTitle("错误")
                    .setMessage(error)
                    .setPositiveButton("确定", null)
                    .show();
            }
        });
    }

    private void updateUI(WeatherEntity weather) {
        try {
            // 城市名称
            tvLocation.setText(weather.getCity());
            
            // 温度，添加度数符号
            tvTemperature.setText(weather.getTemperature() + "°");
            
            // 天气描述
            tvWeatherDesc.setText(weather.getWeather());
            
            // 更新时间，格式化显示
            String reportTime = weather.getReportTime();
            if (reportTime.length() > 16) { // 如果时间格式是 "2024-12-18 16:04:31"
                reportTime = reportTime.substring(0, 16); // 只保留到分钟
            }
            tvUpdateTime.setText("更新时间: " + reportTime);
            
            // 详细信息列表
            List<WeatherDetail> details = new ArrayList<>();
            details.add(new WeatherDetail(R.drawable.ic_wind_direction, "风向", weather.getWindDirection() + "风"));
            details.add(new WeatherDetail(R.drawable.ic_wind_power, "风力", "风力" + weather.getWindPower()));
            details.add(new WeatherDetail(R.drawable.ic_humidity, "湿度", weather.getHumidity() + "%"));
            details.add(new WeatherDetail(R.drawable.ic_temperature, "温度", weather.getTemperature() + "°"));
            
            adapter.updateDetails(details);
            
            Log.d(TAG, "UI updated with weather data for " + weather.getCity());
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }

    private void testWeatherApi(String city) {
        Log.d(TAG, "Testing Weather API for city: " + city);
        RetrofitClient.getInstance().getWeatherApi()
            .getWeather(
                RetrofitClient.getApiKey(),
                city,
                "base",
                "JSON"
            )
            .enqueue(new retrofit2.Callback<WeatherResponse>() {
                @Override
                public void onResponse(retrofit2.Call<WeatherResponse> call, 
                                     retrofit2.Response<WeatherResponse> response) {
                    Log.d(TAG, "API Response Code: " + response.code());
                    Log.d(TAG, "API Raw Response: " + response.raw().toString());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Response Body: " + response.body().toString());
                        if (response.body().getLives() != null) {
                            Log.d(TAG, "Lives Data: " + response.body().getLives().toString());
                        }
                    } else {
                        Log.e(TAG, "API Error Response: " + response.errorBody());
                        try {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<WeatherResponse> call, Throwable t) {
                    Log.e(TAG, "API Call Failed. URL: " + call.request().url());
                    Log.e(TAG, "Error Details:", t);
                }
            });
    }
} 