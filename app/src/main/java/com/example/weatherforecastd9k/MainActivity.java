package com.example.weatherforecastd9k;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.weatherforecastd9k.ui.MapFragment;
import com.example.weatherforecastd9k.ui.WeatherFragment;
import com.example.weatherforecastd9k.ui.HistoryFragment;
import com.example.weatherforecastd9k.ui.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_map) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.navigation_weather) {
                selectedFragment = new WeatherFragment();
            } else if (itemId == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();
            }
            return true;
        });

        // 修改默认选中的Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new MapFragment())
                .commit();
            // 设置底部导航栏默认选中项
            bottomNav.setSelectedItemId(R.id.navigation_map);
        }
    }
}