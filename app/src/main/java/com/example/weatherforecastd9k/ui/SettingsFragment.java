package com.example.weatherforecastd9k.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.weatherforecastd9k.R;
import com.lljjcoder.citypickerview.widget.CityPicker;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.pm.PackageManager;
import android.Manifest;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    
    private static final int PICK_IMAGE = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private SharedPreferences prefs;
    private CircleImageView avatarImage;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        // 设置头像点击事件
        findPreference("avatar").setOnPreferenceClickListener(preference -> {
            openGallery();
            return true;
        });

        // 设置修改密码点击事件
        findPreference("change_password").setOnPreferenceClickListener(preference -> {
            showChangePasswordDialog();
            return true;
        });

        // 设置选择城市点击事件
        findPreference("default_city").setOnPreferenceClickListener(preference -> {
            showCityPicker();
            return true;
        });

        // 监听自动定位开关
        findPreference("auto_location").setOnPreferenceChangeListener((preference, newValue) -> {
            boolean autoLocation = (Boolean) newValue;
            if (autoLocation) {
                // 开启自动定位时，清空默认城市
                prefs.edit().putString("default_city", "").apply();
                findPreference("default_city").setSummary("自动定位已开启");
            }
            return true;
        });

        // 更新摘要
        updateSummaries();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        // 获取父布局
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        
        // 加载自定义布局
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        
        // 找到list_container并添加preferences视图
        ViewGroup listContainer = rootView.findViewById(android.R.id.list_container);
        listContainer.addView(view);
        
        // 初始化头像
        avatarImage = rootView.findViewById(R.id.avatarImage);
        String avatarUri = prefs.getString("avatar_uri", "");
        if (!avatarUri.isEmpty()) {
            try {
                // 检查文件是否存在
                File avatarFile = new File(Uri.parse(avatarUri).getPath());
                if (avatarFile.exists()) {
                    avatarImage.setImageURI(Uri.parse(avatarUri));
                } else {
                    // 如果文件不存在，清除保存的URI并使用默认头像
                    prefs.edit().remove("avatar_uri").apply();
                    avatarImage.setImageResource(R.drawable.default_avatar);
                }
            } catch (Exception e) {
                // 如果出现任何错误，清除保存的URI并使用默认头像
                prefs.edit().remove("avatar_uri").apply();
                avatarImage.setImageResource(R.drawable.default_avatar);
                e.printStackTrace();
            }
        }
        
        avatarImage.setOnClickListener(v -> openGallery());
        
        return rootView;
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13及以上版本
            if (ContextCompat.checkSelfPermission(requireContext(), 
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 
                    PERMISSION_REQUEST_CODE);
                return;
            }
        } else {
            // Android 13以下版本
            if (ContextCompat.checkSelfPermission(requireContext(), 
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                    PERMISSION_REQUEST_CODE);
                return;
            }
        }

        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), "需要存储权限来选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog();
        dialog.show(getParentFragmentManager(), "ChangePasswordDialog");
    }

    private void showCityPicker() {
        CityPicker cityPicker = new CityPicker.Builder(requireActivity())
            .textSize(14)
            .title("地址选择")
            .titleBackgroundColor("#FFFFFF")
            .confirTextColor("#696969")
            .cancelTextColor("#696969")
            .province("江苏省")
            .city("南京市")
            .district("玄武区")
            .textColor(Color.parseColor("#000000"))
            .provinceCyclic(true)
            .cityCyclic(false)
            .districtCyclic(false)
            .visibleItemsCount(7)
            .itemPadding(10)
            .onlyShowProvinceAndCity(false)
            .build();
        
        cityPicker.show();
        
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                String province = citySelected[0];
                String city = citySelected[1];
                String district = citySelected[2];
                
                // 保存选择的城市，包含完整信息
                String fullLocation = city + district;
                prefs.edit()
                    .putString("default_city", fullLocation)
                    .putBoolean("auto_location", false)  // 选择城市时关闭自动定位
                    .apply();
                
                // 更新UI
                findPreference("default_city").setSummary(fullLocation);
                ((SwitchPreferenceCompat) findPreference("auto_location")).setChecked(false);
                Toast.makeText(requireContext(), "已设置默认城市: " + fullLocation, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                try {
                    // 使用ContentResolver复制图片到应用私有目录
                    File avatarDir = new File(requireContext().getFilesDir(), "avatars");
                    if (!avatarDir.exists()) {
                        avatarDir.mkdirs();
                    }
                    File avatarFile = new File(avatarDir, "avatar_" + System.currentTimeMillis() + ".jpg");
                    
                    // 复制文件
                    InputStream is = requireContext().getContentResolver().openInputStream(sourceUri);
                    OutputStream os = new FileOutputStream(avatarFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    
                    // 保存新的URI并更新显示
                    Uri newUri = Uri.fromFile(avatarFile);
                    prefs.edit().putString("avatar_uri", newUri.toString()).apply();
                    avatarImage.setImageURI(newUri);
                    
                    Toast.makeText(requireContext(), "头像已更新", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "头像更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateSummaries() {
        // 更新用户名摘要
        String username = prefs.getString("username", "");
        findPreference("username").setSummary(username.isEmpty() ? "未设置" : username);

        // 更新默认城市摘要
        String defaultCity = prefs.getString("default_city", "");
        findPreference("default_city").setSummary(defaultCity.isEmpty() ? "点击选择" : defaultCity);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummaries();
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
} 