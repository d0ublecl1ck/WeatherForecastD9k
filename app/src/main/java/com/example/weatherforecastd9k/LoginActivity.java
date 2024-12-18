package com.example.weatherforecastd9k;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.example.weatherforecastd9k.data.AppDatabase;
import com.example.weatherforecastd9k.data.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialCheckBox rememberPasswordCheckBox;
    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        rememberPasswordCheckBox = findViewById(R.id.rememberPassword);
        MaterialButton loginButton = findViewById(R.id.loginButton);

        // 加载保存的用户信息
        loadSavedCredentials();

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("admin".equals(username) && "password".equals(password)) {
                // 如果选中"记住密码"，保存用户信息
                if (rememberPasswordCheckBox.isChecked()) {
                    saveCredentials(username, password);
                } else {
                    clearSavedCredentials();
                }
                
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 添加注册链接点击事件
        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // 处理注册页面传回的数据
        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            if (username != null && password != null) {
                usernameInput.setText(username);
                passwordInput.setText(password);
            }
        }
    }

    private void loadSavedCredentials() {
        executorService.execute(() -> {
            User user = db.userDao().getUser();
            if (user != null) {
                runOnUiThread(() -> {
                    usernameInput.setText(user.getUsername());
                    passwordInput.setText(user.getPassword());
                    rememberPasswordCheckBox.setChecked(true);
                });
            }
        });
    }

    private void saveCredentials(String username, String password) {
        executorService.execute(() -> {
            User user = new User(username, password);
            db.userDao().deleteAll();
            db.userDao().insert(user);
        });
    }

    private void clearSavedCredentials() {
        executorService.execute(() -> db.userDao().deleteAll());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}