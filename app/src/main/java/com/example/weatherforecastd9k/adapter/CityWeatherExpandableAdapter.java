package com.example.weatherforecastd9k.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;

import java.util.List;
import java.util.Map;

public class CityWeatherExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> cityGroups;
    private Map<String, WeatherEntity> cityWeatherMap;

    public CityWeatherExpandableAdapter(Context context, List<String> cityGroups,
                                      Map<String, WeatherEntity> cityWeatherMap) {
        this.context = context;
        this.cityGroups = cityGroups;
        this.cityWeatherMap = cityWeatherMap;
    }

    @Override
    public int getGroupCount() {
        return cityGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return cityGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String city = cityGroups.get(groupPosition);
        return cityWeatherMap.get(city);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String cityName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(cityName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                           View convertView, ViewGroup parent) {
        WeatherEntity weather = (WeatherEntity) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_weather_detail, null);
        }

        TextView tvDetailTitle = convertView.findViewById(R.id.tvDetailTitle);
        TextView tvDetailValue = convertView.findViewById(R.id.tvDetailValue);

        tvDetailTitle.setText(weather.getWeather());
        tvDetailValue.setText(weather.getTemperature() + "°");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
} 