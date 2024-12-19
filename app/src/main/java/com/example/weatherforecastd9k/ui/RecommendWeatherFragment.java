package com.example.weatherforecastd9k.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.network.BingApi;
import com.example.weatherforecastd9k.network.QrCodeApi;
import com.example.weatherforecastd9k.network.RetrofitClient;
import com.example.weatherforecastd9k.util.BingImageCache;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendWeatherFragment extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    private ImageView bingImage;
    private BingImageCache imageCache;
    
    // 添加二维码相关变量
    private TextInputEditText qrContentInput;
    private MaterialButton generateQrButton;
    private ImageView qrCodeImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_weather, container, false);
        
        initViews(view);
        setupQrCodeGeneration();
        imageCache = new BingImageCache(requireContext());
        loadBingImage();
        
        swipeRefresh.setOnRefreshListener(this::loadBingImage);
        
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        bingImage = view.findViewById(R.id.bingImage);
        
        // 二维码相关控件初始化
        qrContentInput = view.findViewById(R.id.qrContentInput);
        generateQrButton = view.findViewById(R.id.generateQrButton);
        qrCodeImage = view.findViewById(R.id.qrCodeImage);
    }

    private void setupQrCodeGeneration() {
        generateQrButton.setOnClickListener(v -> {
            String content = qrContentInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
            generateQrCode(content);
        });
    }

    private void generateQrCode(String content) {
        // URL编码内容
        String encodedContent = Uri.encode(content);
        
        RetrofitClient.createQrCodeApi(QrCodeApi.class)
            .createQrCode(encodedContent)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            byte[] imageData = response.body().bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            qrCodeImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), 
                                "生成二维码失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(requireContext(), 
                        "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadBingImage() {
        if (imageCache.isCacheValid()) {
            // 使用缓存的图片
            Bitmap cachedImage = imageCache.loadImage();
            if (cachedImage != null) {
                bingImage.setImageBitmap(cachedImage);
                swipeRefresh.setRefreshing(false);
                return;
            }
        }

        // 从网络加载新图片
        RetrofitClient.createBingApi(BingApi.class)
            .getWallpaper("1920x1080", 0)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    swipeRefresh.setRefreshing(false);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            byte[] imageData = response.body().bytes();
                            imageCache.saveImage(imageData);
                            Bitmap bitmap = imageCache.loadImage();
                            if (bitmap != null) {
                                bingImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), 
                                "加载图片失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(requireContext(), 
                        "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            });
    }
} 