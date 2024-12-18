package com.example.weatherforecastd9k.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.weatherforecastd9k.R;

public class MapFragment extends Fragment implements AMapLocationListener {
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private TextView cityNameText;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // 在初始化地图前先进行隐私合规设置
        try {
            // 更新隐私合规状态
            com.amap.api.maps.MapsInitializer.updatePrivacyShow(requireContext(), true, true);
            com.amap.api.maps.MapsInitializer.updatePrivacyAgree(requireContext(), true);
            // 定位隐私合规
            AMapLocationClient.updatePrivacyShow(requireContext(), true, true);
            AMapLocationClient.updatePrivacyAgree(requireContext(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        
        // 初始化视图
        mapView = view.findViewById(R.id.map);
        cityNameText = view.findViewById(R.id.cityName);
        mapView.onCreate(savedInstanceState);
        
        // 初始化地图
        initMap();
        
        // 检查权限
        checkPermissions();
        
        return view;
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 设置定位蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
            myLocationStyle.interval(2000);
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);
            // 设置地图UI控件
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocation();
        }
    }

    private void startLocation() {
        try {
            // 初始化定位
            AMapLocationClient.updatePrivacyShow(requireContext(), true, true);
            AMapLocationClient.updatePrivacyAgree(requireContext(), true);
            locationClient = new AMapLocationClient(requireContext());
            locationClient.setLocationListener(this);
            
            // 配置定位参数
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
            locationClient.setLocationOption(option);
            
            // 开始定位
            locationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                // 定位成功
                cityNameText.setText(aMapLocation.getCity());
                // 移动地图到当前位置
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                // 定位失败
                Toast.makeText(requireContext(), 
                    "定位失败: " + aMapLocation.getErrorInfo(), 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            } else {
                Toast.makeText(requireContext(), "需要定位权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
} 