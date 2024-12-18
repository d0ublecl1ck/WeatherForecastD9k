package com.example.weatherforecastd9k.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.model.WeatherDetail;

import java.util.ArrayList;
import java.util.List;

public class WeatherDetailAdapter extends RecyclerView.Adapter<WeatherDetailAdapter.ViewHolder> {
    private List<WeatherDetail> details;
    
    public WeatherDetailAdapter() {
        this.details = new ArrayList<>();
    }
    
    public void updateDetails(List<WeatherDetail> newDetails) {
        this.details = newDetails;
        notifyDataSetChanged();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWeatherIcon;
        TextView tvDetailTitle;
        TextView tvDetailValue;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvDetailTitle = itemView.findViewById(R.id.tvDetailTitle);
            tvDetailValue = itemView.findViewById(R.id.tvDetailValue);
        }
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather_detail, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherDetail detail = details.get(position);
        holder.ivWeatherIcon.setImageResource(detail.getIconRes());
        holder.tvDetailTitle.setText(detail.getTitle());
        holder.tvDetailValue.setText(detail.getValue());
    }
    
    @Override
    public int getItemCount() {
        return details != null ? details.size() : 0;
    }
} 