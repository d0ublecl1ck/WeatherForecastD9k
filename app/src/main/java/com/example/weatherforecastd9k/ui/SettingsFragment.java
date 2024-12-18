package com.example.weatherforecastd9k.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.weatherforecastd9k.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends Fragment {
    private static final String PREFS_NAME = "WeatherSettings";
    private static final String DEFAULT_CITY_KEY = "default_city";
    
    private TextInputEditText defaultCityInput;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        defaultCityInput = view.findViewById(R.id.defaultCityInput);
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        
        // 加载已保存的默认城市
        String savedCity = prefs.getString(DEFAULT_CITY_KEY, "");
        defaultCityInput.setText(savedCity);
        
        saveButton.setOnClickListener(v -> saveDefaultCity());
        
        return view;
    }

    private void saveDefaultCity() {
        String city = defaultCityInput.getText().toString().trim();
        prefs.edit().putString(DEFAULT_CITY_KEY, city).apply();
        Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show();
    }
} 