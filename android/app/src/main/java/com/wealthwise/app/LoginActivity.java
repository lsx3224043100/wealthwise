package com.wealthwise.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.wealthwise.app.api.LoginRequest;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.TokenResponse;
import com.wealthwise.app.api.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RetrofitClient.init(this);

        // 自动登录：检查本地是否已有 Token
        String savedToken = SharedPreferencesManager.getToken(this);
        if (savedToken != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(username, password);

        Call<Result<TokenResponse>> call = RetrofitClient.getInstance().login(request);

        call.enqueue(new Callback<Result<TokenResponse>>() {
            @Override
            public void onResponse(Call<Result<TokenResponse>> call, Response<Result<TokenResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    TokenResponse tokenResponse = response.body().getData();
                    // 保存token到SharedPreferences
                    SharedPreferencesManager.saveToken(LoginActivity.this, tokenResponse.getToken());
                    SharedPreferencesManager.saveUsername(LoginActivity.this, tokenResponse.getUsername());
                    SharedPreferencesManager.saveUserId(LoginActivity.this, tokenResponse.getUserId());

                    // 跳转到主页面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.body() != null && response.body().getMsg() != null) {
                    Toast.makeText(LoginActivity.this, "登录失败: " + response.body().getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<TokenResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}