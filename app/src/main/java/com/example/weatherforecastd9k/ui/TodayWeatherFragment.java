package com.example.weatherforecastd9k.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.adapter.WeatherDetailAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.TextView;

public class TodayWeatherFragment extends Fragment {
    private TextView tvLocation;
    private TextView tvTemperature;
    private TextView tvWeatherDesc;
    private TextView tvUpdateTime;
    private RecyclerView rvWeatherDetails;
    private WeatherDetailAdapter adapter;
    private FloatingActionButton fabRefresh;
    private SharedPreferences prefs;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadWeatherData();
        
        fabRefresh.setOnClickListener(v -> loadWeatherData());
        
        return view;
    }
    
    private void initViews(View view) {
        tvLocation = view.findViewById(R.id.tvLocation);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvWeatherDesc = view.findViewById(R.id.tvWeatherDesc);
        tvUpdateTime = view.findViewById(R.id.tvUpdateTime);
        rvWeatherDetails = view.findViewById(R.id.rvWeatherDetails);
        fabRefresh = view.findViewById(R.id.fabRefresh);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }
    
    private void setupRecyclerView() {
        adapter = new WeatherDetailAdapter();
        rvWeatherDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWeatherDetails.setAdapter(adapter);
    }
    
    private void loadWeatherData() {
        String city = prefs.getString("default_city", "");
        boolean autoLocation = prefs.getBoolean("auto_location", true);
        
        if (autoLocation) {
            // TODO: 实现自动定位
        } else if (!city.isEmpty()) {
            // TODO: 使用高德天气API获取天气数据
            String url = String.format("https://restapi.amap.com/v3/weather/weatherInfo?key=%s&city=%s",
                    getString(R.string.amap_web_key), city);
            
            // 使用OkHttp或Retrofit发起网络请求
        }
    }
} 