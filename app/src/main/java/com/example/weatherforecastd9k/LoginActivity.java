package com.example.weatherforecastd9k;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.example.weatherforecastd9k.db.AppDatabase;
import com.example.weatherforecastd9k.db.entity.User;
import com.example.weatherforecastd9k.util.SessionManager;
import com.example.weatherforecastd9k.viewmodel.UserViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialCheckBox rememberPasswordCheckBox;
    private MaterialButton loginButton;
    private TextView registerLink;
    private AppDatabase db;
    private ExecutorService executorService;
    private UserViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        
        // 检查会话是否有效
        if (sessionManager.isSessionValid()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        initViews();
        setupListeners();
        observeViewModel();
        loadSavedCredentials();

        // 处理注册页面传回的数据
        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            boolean autoLogin = intent.getBooleanExtra("auto_login", false);
            
            if (username != null && password != null) {
                usernameInput.setText(username);
                passwordInput.setText(password);
                
                // 如果是注册后跳转来的，自动登录
                if (autoLogin) {
                    performLogin(username, password);
                }
            }
        }
    }

    private void initViews() {
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        rememberPasswordCheckBox = findViewById(R.id.rememberPassword);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            performLogin(username, password);
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void performLogin(String username, String password) {
        LiveData<User> result = viewModel.login(username, password);
        if (result != null) {
            result.observe(this, user -> {
                if (user != null) {
                    // 保存会话
                    sessionManager.saveSession(username);
                    
                    // 如果选中"记住密码"，保存用户信息
                    if (rememberPasswordCheckBox.isChecked()) {
                        saveCredentials(username, password);
                    } else {
                        clearSavedCredentials();
                    }
                    
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            });
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

    private void observeViewModel() {
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}