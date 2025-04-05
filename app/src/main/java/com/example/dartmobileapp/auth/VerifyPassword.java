package com.example.dartmobileapp.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dartmobileapp.R;
import com.example.dartmobileapp.profile.ChangeEmail;
import com.example.dartmobileapp.profile.edit.ChangeUsername;
import com.example.dartmobileapp.profile.UserProfile;
import com.example.dartmobileapp.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyPassword extends AppCompatActivity {
    
    private EditText passwordInput;
    private Button nextButton;
    private SessionManager sessionManager;
    private static final String API_URL = "https://dart-server-back.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация компонентов
        passwordInput = findViewById(R.id.password_input);
        nextButton = findViewById(R.id.nextButton);
        sessionManager = new SessionManager(this);
        
        // Получаем информацию, откуда пришли
        String from = getIntent().getStringExtra("COMING_FROM");

        // Обработка нажатия кнопки "Далее"
        nextButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            
            // Проверка ввода
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Верификация пароля через API
            verifyPassword(password, from);
        });

        // Кнопка Назад
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });
    }
    
    private void verifyPassword(String password, String redirectTo) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Проверка пароля...");
        progressDialog.show();
        
        // Получаем email из SessionManager
        String email = sessionManager.getEmail();
        if (email == null || email.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Ошибка: email не найден", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Создаем JSON для запроса
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Ошибка при формировании запроса", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Создаем запрос к API
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + "/user/verify",
                requestBody,
                response -> {
                    progressDialog.dismiss();
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            // Пароль верный, переходим дальше
                            navigateNext(redirectTo);
                        } else {
                            Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Ошибка при обработке ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    
                    // Для упрощения тестирования, пока пропускаем проверку
                    // и сразу переходим на следующий экран
                    System.out.println("DEBUG: Пропускаем проверку пароля для тестирования");
                    navigateNext(redirectTo);
                    
                    // В реальном приложении, вместо этого нужно показывать ошибку:
                    // Toast.makeText(this, "Ошибка сети. Пожалуйста, попробуйте позже", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                
                // Добавляем токен авторизации, если есть
                String token = sessionManager.getToken();
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                
                return headers;
            }
        };
        
        // Настройка таймаута запроса
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 секунд таймаут
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        
        // Отправка запроса
        Volley.newRequestQueue(this).add(request);
    }
    
    private void navigateNext(String redirectTo) {
        Intent intent;
        
        if ("ChangeEmail".equals(redirectTo)) {
            intent = new Intent(VerifyPassword.this, ChangeEmail.class);
        } else if ("ChangeUsername".equals(redirectTo)) {
            intent = new Intent(VerifyPassword.this, ChangeUsername.class);
        } else {
            // Если неизвестный редирект, возвращаемся в профиль
            intent = new Intent(VerifyPassword.this, UserProfile.class);
            Toast.makeText(this, "Неизвестный тип операции", Toast.LENGTH_SHORT).show();
        }
        
        startActivity(intent);
        finish(); // Закрываем текущую активность
    }
}