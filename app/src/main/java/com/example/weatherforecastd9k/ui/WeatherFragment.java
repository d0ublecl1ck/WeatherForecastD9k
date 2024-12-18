package com.example.weatherforecastd9k.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;
import com.example.weatherforecastd9k.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.weatherforecastd9k.viewmodel.WeatherViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * 天气Fragment,用于显示天气相关信息
 * 包含今日天气和天气推荐两个子页面
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    private ViewPager2 viewPager;           // 用于左右滑动切换页面
    private TabLayout tabLayout;            // 顶部标签栏
    private WeatherPagerAdapter pagerAdapter;// ViewPager的适配器
    private WeatherViewModel weatherViewModel;// 天气数据的ViewModel

    /**
     * 创建Fragment视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        
        // 初始化ViewModel
        initViewModel();
        
        initViews(view);
        setupViewPager();
        loadWeatherData();
        
        return view;
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
    }
    
    /**
     * 设置ViewPager和TabLayout
     * 配置页面切换和标签显示
     */
    private void setupViewPager() {
        pagerAdapter = new WeatherPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // 将TabLayout与ViewPager关联
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("今日");
                        break;
                    case 1:
                        tab.setText("推荐");
                        break;
                }
            }
        ).attach();
    }
    
    /**
     * 加载天气数据
     * 从SharedPreferences获取配置的城市信息
     * 通过ViewModel刷新天气数据
     */
    private void loadWeatherData() {
        Log.d(TAG, "loadWeatherData called");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String city = prefs.getString("default_city", "360923"); // 使用上高县的城市编码
        boolean autoLocation = prefs.getBoolean("auto_location", true);
        
        Log.d(TAG, "Starting weather refresh for city: " + city);
        weatherViewModel.refreshWeather(city);
        
        // 观察错误信息
        weatherViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Weather error: " + error);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWeatherData(); // 确保每次Fragment可见时都刷新数据
    }

    // 修改 ViewModel 初始化，使用 requireActivity() 确保作用域是 Activity 级别
    private void initViewModel() {
        weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
    }
} 