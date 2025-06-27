package com.example.dartmobileapp.profile.edit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.example.dartmobileapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUsername extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private SessionManager sessionManager;
    private static final String API_URL = "https://dart-server-back.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_username);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализируем SessionManager
        sessionManager = new SessionManager(this);

        // Инициализируем поле ввода
        usernameInput = findViewById(R.id.usernameChangeInput);
        
        // Устанавливаем текущее имя пользователя в поле ввода
        usernameInput.setText(sessionManager.getUsername());

        // Обрабатываем нажатие на кнопку "Назад"
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Получаем кнопку обновления
        Button updateButton = findViewById(R.id.updateButton);

        // Устанавливаем обработчик нажатия
        updateButton.setOnClickListener(v -> {
            String newUsername = usernameInput.getText().toString().trim();
            
            // Проверка на пустое имя пользователя
            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите имя пользователя", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Если использовать API, тогда вызвать метод updateUsernameViaApi
            // updateUsernameViaApi(newUsername);
            
            // Для прототипа просто обновляем имя в SessionManager
            updateUsernameLocally(newUsername);
        });
    }
    
    private void updateUsernameLocally(String newUsername) {
        // Обновляем имя пользователя в SessionManager
        sessionManager.updateUsername(newUsername);
        
        // Переходим на экран успешного обновления
        String title = "Имя пользователя успешно изменено";
        Intent intent = new Intent(ChangeUsername.this, ChangeSuccess.class);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }
    
    /**
     * Метод для обновления имени пользователя через API (закомментирован, так как API может не поддерживать эту функцию)
     */
    /*
    private void updateUsernameViaApi(String newUsername) {
        // Показываем индикатор загрузки
        // (код для показа прогресс-бара)
        
        String token = sessionManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();
            return;
        }
        
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", newUsername);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при формировании запроса", Toast.LENGTH_SHORT).show();
            return;
        }
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                API_URL + "/user/profile", // Предполагаемый эндпоинт
                jsonBody,
                response -> {
                    // Обработка успешного ответа
                    try {
                        if (response.has("name")) {
                            String updatedName = response.getString("name");
                            sessionManager.updateUsername(updatedName);
                            
                            // Переходим на экран успешного обновления
                            String title = "Имя пользователя успешно изменено";
                            Intent intent = new Intent(ChangeUsername.this, ChangeSuccess.class);
                            intent.putExtra("TITLE", title);
                            startActivity(intent);
                        } else {
                            // Обновляем имя пользователя локально
                            sessionManager.updateUsername(newUsername);
                            
                            // Переходим на экран успешного обновления
                            String title = "Имя пользователя успешно изменено";
                            Intent intent = new Intent(ChangeUsername.this, ChangeSuccess.class);
                            intent.putExtra("TITLE", title);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка при обработке ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Обработка ошибки
                    Toast.makeText(this, "Ошибка при обновлении имени пользователя", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        
        Volley.newRequestQueue(this).add(request);
    }
    */
}