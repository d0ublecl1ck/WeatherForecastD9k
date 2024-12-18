package com.example.weatherforecastd9k.util;

import android.content.Context;
import com.example.weatherforecastd9k.R;

public class WeatherUtil {
    public static String convertWeekDay(String week) {
        switch (week) {
            case "1": return "星期一";
            case "2": return "星期二";
            case "3": return "星期三";
            case "4": return "星期四";
            case "5": return "星期五";
            case "6": return "星期六";
            case "7": return "星期日";
            default: return "未知";
        }
    }

    public static int getWeatherIcon(String weatherDesc) {
        if (weatherDesc == null) return R.drawable.ic_weather;
        
        if (weatherDesc.contains("晴")) {
            return R.drawable.ic_weather_sunny;
        } else if (weatherDesc.contains("多云")) {
            return R.drawable.ic_weather_partly_cloudy;
        } else if (weatherDesc.contains("阴")) {
            return R.drawable.ic_weather_overcast;
        } else if (weatherDesc.contains("雨")) {
            if (weatherDesc.contains("雷")) {
                return R.drawable.ic_weather_thunder;
            }
            return R.drawable.ic_weather_heavy_rain;
        } else if (weatherDesc.contains("雪")) {
            return R.drawable.ic_weather_snow;
        } else if (weatherDesc.contains("雾")) {
            return R.drawable.ic_weather_heavy_fog;
        }
        
        return R.drawable.ic_weather;
    }
} 