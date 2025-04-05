package com.example.dartmobileapp.auth;

import android.content.Intent;
import android.os.Bundle;
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

        // Инициализация SessionManager
        sessionManager = new SessionManager(this);
        
        // Инициализация поля ввода пароля
        passwordInput = findViewById(R.id.password_input);

        // Получение информации о том, откуда был выполнен переход
        String from = getIntent().getStringExtra("COMING_FROM");

        // Установка обработчика нажатия на кнопку "Дальше"
        findViewById(R.id.nextButton).setOnClickListener(v -> {
            // Получаем введенный пароль
            String password = passwordInput.getText().toString().trim();
            
            // Проверка на пустой пароль
            if (password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Проверяем пароль
            verifyPassword(password, from);
        });

        // Кнопка Назад
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });
    }
    
    private void verifyPassword(String password, String destination) {
        // Проверяем пароль сначала локально, если он сохранен в SessionManager
        String savedPassword = sessionManager.getSharedPreferences().getString("password", null);
        
        if (savedPassword != null && savedPassword.equals(password)) {
            // Если пароль совпадает с локально сохраненным, сразу переходим к нужному экрану
            navigateToDestination(destination);
            return;
        }
        
        // Если локальный пароль не совпал или его нет, проверяем через API
        // Получаем текущий email пользователя из SessionManager
        String email = sessionManager.getEmail();
        
        // Если email не найден, выводим сообщение об ошибке
        if (email == null) {
            Toast.makeText(this, "Ошибка: Email не найден", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Создаем JSON объект для отправки на сервер
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при формировании запроса", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Создание запроса на проверку пароля
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                API_URL + "/user/login", // Используем тот же эндпоинт, что и для входа
                jsonBody,
                response -> {
                    // Если получили ответ, значит пароль верный
                    // Сохраняем пароль локально, чтобы не делать лишних запросов в будущем
                    sessionManager.updatePassword(password);
                    
                    // Переходим к нужному экрану
                    navigateToDestination(destination);
                },
                error -> {
                    // Если произошла ошибка, значит пароль неверный
                    Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        
        // Устанавливаем политику повторных попыток
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        
        // Отправляем запрос
        Volley.newRequestQueue(this).add(request);
    }
    
    private void navigateToDestination(String destination) {
        Intent intent;
        
        if ("ChangeEmail".equals(destination)) {
            intent = new Intent(VerifyPassword.this, ChangeEmail.class);
        } else if ("ChangeUsername".equals(destination)) {
            intent = new Intent(VerifyPassword.this, ChangeUsername.class);
        } else {
            // По умолчанию возвращаемся к профилю
            intent = new Intent(VerifyPassword.this, UserProfile.class);
        }
        
        startActivity(intent);
    }
}