package com.example.weatherforecastd9k.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WeatherPagerAdapter extends FragmentStateAdapter {
    public WeatherPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CurrentWeatherFragment();
            case 1:
                return new ForecastWeatherFragment();
            case 2:
                return new LifeIndexFragment();
            default:
                return new CurrentWeatherFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
} 