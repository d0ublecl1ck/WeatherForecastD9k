package com.example.weatherforecastd9k;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.weatherforecastd9k.data.AppDatabase;
import com.example.weatherforecastd9k.data.User;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private TextInputEditText phoneInput;
    private TextInputEditText verificationCodeInput;
    private MaterialButton sendCodeButton;
    private MaterialButton registerButton;
    private TextView loginLink;
    private String verificationCode;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getInstance(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirmPassword);
        phoneInput = findViewById(R.id.phone);
        verificationCodeInput = findViewById(R.id.verificationCode);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
    }

    private void setupListeners() {
        sendCodeButton.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            if (phone.isEmpty() || phone.length() != 11) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVerificationCode(phone);
        });

        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                registerUser();
            }
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void sendVerificationCode(String phone) {
        // 生成6位随机验证码
        verificationCode = String.format("%06d", new Random().nextInt(999999));
        // 模拟发送验证码
        Toast.makeText(this, "验证码: " + verificationCode, Toast.LENGTH_LONG).show();
        
        // 开始倒计时
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendCodeButton.setEnabled(false);
                sendCodeButton.setText(millisUntilFinished / 1000 + "秒后重试");
            }

            public void onFinish() {
                sendCodeButton.setEnabled(true);
                sendCodeButton.setText("发送验证码");
            }
        }.start();
    }

    private boolean validateInputs() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String inputCode = verificationCodeInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() 
            || phone.isEmpty() || inputCode.isEmpty()) {
            Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (verificationCode == null || !verificationCode.equals(inputCode)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // 注册成功后,返回登录页并自动填充账号密码
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }
} 