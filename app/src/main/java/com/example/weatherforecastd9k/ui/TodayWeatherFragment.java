package com.example.weatherforecastd9k.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.weatherforecastd9k.R;

public class TodayWeatherFragment extends Fragment {
    private static final String TAG = "TodayWeatherFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_weather, container, false);
    }
} 