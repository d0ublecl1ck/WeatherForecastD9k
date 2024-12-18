package com.example.weatherforecastd9k.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.weatherforecastd9k.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 天气Fragment,用于显示天气相关信息
 * 包含今日天气和天气推荐两个子页面
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    private ViewPager2 viewPager;           // 用于左右滑动切换页面
    private TabLayout tabLayout;            // 顶部标签栏
    private WeatherPagerAdapter pagerAdapter;// ViewPager的适配器

    /**
     * 创建Fragment视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        
        initViews(view);
        setupViewPager();
        
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
} 