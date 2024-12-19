package com.example.weatherforecastd9k.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecastd9k.db.entity.User;
import com.example.weatherforecastd9k.repository.UserRepository;

import java.util.Random;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<String> verificationCode = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<User> login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("用户名和密码不能为空");
            return null;
        }
        return repository.login(username, password);
    }

    public void sendVerificationCode(String phone) {
        if (phone.isEmpty() || phone.length() != 11) {
            errorMessage.setValue("请输入正确的手机号");
            return;
        }

        repository.checkPhone(phone).observeForever(isAvailable -> {
            if (isAvailable) {
                // 生成6位随机验证码
                String code = String.format("%06d", new Random().nextInt(999999));
                verificationCode.setValue(code);
                errorMessage.setValue("验证码: " + code);
            } else {
                errorMessage.setValue("该手机号已被注册");
            }
        });
    }

    public LiveData<Boolean> register(String username, String password, String phone, String code) {
        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || code.isEmpty()) {
            errorMessage.setValue("请填写所有字段");
            return null;
        }

        if (!code.equals(verificationCode.getValue())) {
            errorMessage.setValue("验证码错误");
            return null;
        }

        return repository.register(username, password, phone);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getVerificationCode() {
        return verificationCode;
    }
} 