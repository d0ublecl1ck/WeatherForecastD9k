package com.example.weatherforecastd9k.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.network.WeatherResponse;
import com.example.weatherforecastd9k.network.WeatherResponse.Forecast.Cast;

import java.util.List;

public class FutureWeatherAdapter extends RecyclerView.Adapter<FutureWeatherAdapter.ViewHolder> {
    private List<WeatherResponse.Forecast.Cast> weatherCasts;
    private String reportTime;
    private Context context;
    private int expandedPosition = -1;

    public FutureWeatherAdapter(Context context, List<WeatherResponse.Forecast.Cast> weatherCasts, String reportTime) {
        this.context = context;
        this.weatherCasts = weatherCasts;
        this.reportTime = reportTime;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_future_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cast cast = weatherCasts.get(position + 1); // 跳过今天
        
        // 设置日期和基本信息
        holder.dateText.setText(cast.getDate() + " 周" + cast.getWeek());
        holder.weatherSummary.setText(String.format("%s %s°C/%s°C", 
            cast.getDayweather(), cast.getDaytemp(), cast.getNighttemp()));

        // 设置详细信息
        holder.temperature.setText(cast.getDaytemp() + "°C / " + cast.getNighttemp() + "°C");
        holder.weather.setText(cast.getDayweather());
        holder.windDirection.setText(cast.getDaywind());
        holder.windPower.setText(cast.getDaypower() + "级");
        holder.reportTime.setText("更新时间: " + reportTime);

        // 处理展开/收起
        boolean isExpanded = position == expandedPosition;
        holder.detailsContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setRotation(isExpanded ? 180 : 0);

        // 点击事件
        holder.headerContainer.setOnClickListener(v -> {
            int oldExpandedPosition = expandedPosition;
            expandedPosition = isExpanded ? -1 : position;
            
            // 更新动画
            if (oldExpandedPosition >= 0) {
                notifyItemChanged(oldExpandedPosition);
            }
            if (expandedPosition >= 0) {
                notifyItemChanged(expandedPosition);
            }
            
            // 展开/收起动画
            if (!isExpanded) {
                holder.expandIcon.animate().rotation(180).setDuration(200).start();
                holder.detailsContainer.setVisibility(View.VISIBLE);
            } else {
                holder.expandIcon.animate().rotation(0).setDuration(200).start();
                holder.detailsContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weatherCasts != null ? Math.max(weatherCasts.size() - 1, 0) : 0;
    }

    public void updateData(List<Cast> newCasts, String newReportTime) {
        this.weatherCasts = newCasts;
        this.reportTime = newReportTime;
        expandedPosition = -1;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View headerContainer;
        TextView dateText;
        TextView weatherSummary;
        ImageView expandIcon;
        View detailsContainer;
        TextView temperature;
        TextView weather;
        TextView windDirection;
        TextView windPower;
        TextView reportTime;

        ViewHolder(View view) {
            super(view);
            headerContainer = view.findViewById(R.id.headerContainer);
            dateText = view.findViewById(R.id.dateText);
            weatherSummary = view.findViewById(R.id.weatherSummary);
            expandIcon = view.findViewById(R.id.expandIcon);
            detailsContainer = view.findViewById(R.id.detailsContainer);
            temperature = view.findViewById(R.id.temperature);
            weather = view.findViewById(R.id.weather);
            windDirection = view.findViewById(R.id.windDirection);
            windPower = view.findViewById(R.id.windPower);
            reportTime = view.findViewById(R.id.reportTime);
        }
    }
} 