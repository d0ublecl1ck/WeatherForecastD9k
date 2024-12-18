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

// 天气详情的 RecyclerView 适配器
public class WeatherDetailAdapter extends RecyclerView.Adapter<WeatherDetailAdapter.ViewHolder> {
    // 存储天气详情数据的列表
    private List<WeatherDetail> details = new ArrayList<>();
    // 点击事件监听器
    private OnItemClickListener listener;
    
    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public List<WeatherDetail> getDetails() {
        return details;
    }
    
    public WeatherDetailAdapter() {
        this.details = new ArrayList<>();
    }
    
    public void updateDetails(List<WeatherDetail> newDetails) {
        this.details = newDetails;
        notifyDataSetChanged();
    }
    
    // ViewHolder 类用于缓存 item 视图中的组件引用
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;    // 天气图标
        TextView tvTitle;     // 详情标题
        TextView tvValue;     // 详情数值
        
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvValue = itemView.findViewById(R.id.tvValue);
            
            // 在 ViewHolder 中设置点击监听器
            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }
        }
    }
    
    // 创建 ViewHolder，加载 item 布局
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather_detail, parent, false);
        return new ViewHolder(view, listener);
    }
    
    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherDetail detail = details.get(position);
        // 设置天气图标、标题和数值
        holder.ivIcon.setImageResource(detail.getIconRes());
        holder.tvTitle.setText(detail.getTitle());
        holder.tvValue.setText(detail.getValue());
    }
    
    // 返回数据列表的大小
    @Override
    public int getItemCount() {
        return details.size();
    }
} 