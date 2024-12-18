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
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;
import com.example.weatherforecastd9k.viewmodel.WeatherViewModel;
import androidx.annotation.Nullable;
import com.example.weatherforecastd9k.model.WeatherDetail;

public class TodayWeatherFragment extends Fragment {
    private TextView tvLocation;
    private TextView tvTemperature;
    private TextView tvWeatherDesc;
    private TextView tvUpdateTime;
    private RecyclerView rvWeatherDetails;
    private WeatherDetailAdapter adapter;
    private FloatingActionButton fabRefresh;
    private WeatherViewModel weatherViewModel;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);
        
        initViews(view);
        setupRecyclerView();
        observeWeatherData();
        
        fabRefresh.setOnClickListener(v -> refreshWeatherData());
        
        return view;
    }
    
    private void initViews(View view) {
        tvLocation = view.findViewById(R.id.tvLocation);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvWeatherDesc = view.findViewById(R.id.tvWeatherDesc);
        tvUpdateTime = view.findViewById(R.id.tvUpdateTime);
        rvWeatherDetails = view.findViewById(R.id.rvWeatherDetails);
        fabRefresh = view.findViewById(R.id.fabRefresh);
    }
    
    private void setupRecyclerView() {
        adapter = new WeatherDetailAdapter();
        rvWeatherDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWeatherDetails.setAdapter(adapter);
    }
    
    private void observeWeatherData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String city = prefs.getString("default_city", "");
        boolean autoLocation = prefs.getBoolean("auto_location", true);
        
        if (!autoLocation && !city.isEmpty()) {
            weatherViewModel.getWeatherByCity(city).observe(getViewLifecycleOwner(), weather -> {
                if (weather != null) {
                    updateUI(weather);
                }
            });
        }
        
        weatherViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // TODO: 显示/隐藏加载进度
        });
        
        weatherViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateUI(WeatherEntity weather) {
        tvLocation.setText(weather.getCity());
        tvTemperature.setText(weather.getTemperature() + "°C");
        tvWeatherDesc.setText(weather.getWeather());
        tvUpdateTime.setText("更新时间: " + weather.getReportTime());
        
        List<WeatherDetail> details = new ArrayList<>();
        details.add(new WeatherDetail(R.drawable.ic_wind_direction, "风向", weather.getWindDirection()));
        details.add(new WeatherDetail(R.drawable.ic_wind_power, "风力", weather.getWindPower()));
        details.add(new WeatherDetail(R.drawable.ic_humidity, "湿度", weather.getHumidity() + "%"));
        
        adapter.updateDetails(details);
    }
    
    private void refreshWeatherData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String city = prefs.getString("default_city", "");
        boolean autoLocation = prefs.getBoolean("auto_location", true);
        
        if (!autoLocation && !city.isEmpty()) {
            weatherViewModel.refreshWeather(city);
        }
    }
} 