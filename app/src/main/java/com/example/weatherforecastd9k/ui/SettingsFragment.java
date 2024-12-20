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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.weatherforecastd9k.LoginActivity;
import com.example.weatherforecastd9k.R;
import com.example.weatherforecastd9k.db.AppDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lljjcoder.citypickerview.widget.CityPicker;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.pm.PackageManager;
import android.Manifest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import com.example.weatherforecastd9k.network.DistrictApi;
import com.example.weatherforecastd9k.network.DistrictResponse;
import com.example.weatherforecastd9k.network.RetrofitClient;
import com.example.weatherforecastd9k.util.HistoryCityManager;
import com.example.weatherforecastd9k.repository.UserRepository;
import com.example.weatherforecastd9k.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    
    private static final int PICK_IMAGE = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private SharedPreferences prefs;
    private CircleImageView avatarImage;
    private static final String TAG = "SettingsFragment";
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        userRepository = new UserRepository(requireContext());
        sessionManager = new SessionManager(requireContext());
        
        // 移除用户名设置
        Preference usernamePref = findPreference("username");
        if (usernamePref != null) {
            getPreferenceScreen().removePreference(usernamePref);
        }

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

        // 添加退出登录点击事件
        findPreference("logout").setOnPreferenceClickListener(preference -> {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 清除会话和记住的密码
                    sessionManager.clearSession();
                    clearSavedCredentials();
                    
                    // 跳转到登录页面
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
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
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        dialog.setContentView(dialogView);

        TextInputEditText oldPasswordInput = dialogView.findViewById(R.id.oldPassword);
        TextInputEditText newPasswordInput = dialogView.findViewById(R.id.newPassword);
        TextInputEditText confirmPasswordInput = dialogView.findViewById(R.id.confirmPassword);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirmButton);

        // 设置对话框宽度为屏幕宽度的90%
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentUsername = sessionManager.getUsername();
            userRepository.changePassword(currentUsername, oldPassword, newPassword)
                .observe(this, success -> {
                    if (success) {
                        Toast.makeText(requireContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        sessionManager.clearSession();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), "原密码错误", Toast.LENGTH_SHORT).show();
                    }
                });
        });

        dialog.show();
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

                // 获取城市编码
                RetrofitClient.create(DistrictApi.class)
                    .getDistrict(RetrofitClient.getApiKey(), city, 1, "base")
                    .enqueue(new Callback<DistrictResponse>() {
                        @Override
                        public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                            if (response.isSuccessful() && response.body() != null 
                                && response.body().getDistricts() != null 
                                && !response.body().getDistricts().isEmpty()) {

                                Log.d(TAG, "Response json: \n" + new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(response.body()));

                                String adcode = response.body().getDistricts().get(0).getAdcode();
                                
                                // 保存城市信息编码
                                prefs.edit()
                                    .putString("default_city", fullLocation)
                                    .putString("city_code", adcode)
                                    .putBoolean("auto_location", false)
                                    .apply();
                                
                                // 更新UI
                                findPreference("default_city").setSummary(fullLocation);
                                ((SwitchPreferenceCompat) findPreference("auto_location")).setChecked(false);
                                Toast.makeText(requireContext(), 
                                    "已设置默认城市: " + fullLocation, Toast.LENGTH_SHORT).show();

                                // 在这里添加历史记录
                                HistoryCityManager historyManager = new HistoryCityManager(requireContext());
                                historyManager.addCity(fullLocation, adcode);
                            } else {
                                Toast.makeText(requireContext(), 
                                    "获取城市编码失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DistrictResponse> call, Throwable t) {
                            Toast.makeText(requireContext(), 
                                "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
        // 只更新默认城市摘要
        String defaultCity = prefs.getString("default_city", "");
        Preference cityPreference = findPreference("default_city");
        if (cityPreference != null) {
            cityPreference.setSummary(defaultCity.isEmpty() ? "点击选择" : defaultCity);
        }
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

    private void clearSavedCredentials() {
        // 清除记住的密码
        executorService.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                db.userDao().deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 清除其他相关的SharedPreferences数据
        prefs.edit()
            .remove("username")
            .remove("password")
            .remove("remember_password")
            .apply();
    }

    // 在onDestroy中关闭ExecutorService
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 