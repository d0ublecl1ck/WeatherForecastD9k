package com.example.weatherforecastd9k.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.data.entity.WeatherEntity;
import com.example.weatherforecastd9k.model.WeatherDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CityWeatherExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> cityGroups;
    private Map<String, WeatherEntity> cityWeatherMap;
    private LayoutInflater inflater;

    public CityWeatherExpandableAdapter(Context context, List<String> cityGroups,
                                      Map<String, WeatherEntity> cityWeatherMap) {
        this.context = context;
        this.cityGroups = cityGroups;
        this.cityWeatherMap = cityWeatherMap;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(cityName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                           View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_weather_detail, parent, false);
        }

        WeatherEntity weather = (WeatherEntity) getChild(groupPosition, childPosition);
        
        ImageView ivIcon = convertView.findViewById(R.id.ivIcon);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvValue = convertView.findViewById(R.id.tvValue);

        List<WeatherDetail> details = new ArrayList<>();
        details.add(new WeatherDetail(R.drawable.ic_wind_direction, "风向", weather.getWindDirection() + "风"));
        details.add(new WeatherDetail(R.drawable.ic_wind_power, "风力", "风力" + weather.getWindPower()));
        details.add(new WeatherDetail(R.drawable.ic_humidity, "湿度", weather.getHumidity() + "%"));
        details.add(new WeatherDetail(R.drawable.ic_temperature, "温度", weather.getTemperature() + "°"));

        WeatherDetail detail = details.get(childPosition % details.size());
        ivIcon.setImageResource(detail.getIconRes());
        tvTitle.setText(detail.getTitle());
        tvValue.setText(detail.getValue());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
} 