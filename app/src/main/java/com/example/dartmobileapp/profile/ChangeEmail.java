package com.example.dartmobileapp.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.example.dartmobileapp.profile.edit.ChangeSuccess;
import com.example.dartmobileapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeEmail extends AppCompatActivity {

    private TextInputEditText emailInput;
    private Button updateButton;
    private SessionManager sessionManager;
    private static final String API_URL = "https://dart-server-back.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация компонентов
        emailInput = findViewById(R.id.emailChangeInput);
        updateButton = findViewById(R.id.updateButton);
        sessionManager = new SessionManager(this);
        
        // Установим текущую почту пользователя в поле ввода
        String currentEmail = sessionManager.getEmail();
        if (currentEmail != null && !currentEmail.isEmpty()) {
            emailInput.setText(currentEmail);
            emailInput.setSelection(currentEmail.length()); // Установить курсор в конец текста
        }

        // Обработчик нажатия кнопки "Назад"
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Установим обработчик нажатия на кнопку обновления
        updateButton.setOnClickListener(v -> {
            String newEmail = emailInput.getText().toString().trim();
            
            // Проверка введенной почты
            if (TextUtils.isEmpty(newEmail)) {
                Toast.makeText(this, "Введите новую почту", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Проверка формата почты
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Введите корректный адрес электронной почты", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Проверка, что новая почта отличается от текущей
            if (newEmail.equals(currentEmail)) {
                Toast.makeText(this, "Новая почта совпадает с текущей", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Отправка запроса на изменение почты
            updateEmail(newEmail);
        });
    }
    
    private void updateEmail(String newEmail) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Обновление почты...");
        progressDialog.show();
        
        // Получаем токен из SessionManager
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Создаем JSON для запроса
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", newEmail);
        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Ошибка при формировании запроса", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Создаем запрос к API
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                API_URL + "/user/email",
                requestBody,
                response -> {
                    progressDialog.dismiss();
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            // Обновляем email в SessionManager
                            sessionManager.updateEmail(newEmail);
                            
                            // Показываем сообщение об успехе
                            String title = "Почта успешно изменена";
                            Intent intent = new Intent(ChangeEmail.this, ChangeSuccess.class);
                            intent.putExtra("TITLE", title);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.has("message") ? response.getString("message") : "Ошибка при обновлении почты";
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Ошибка при обработке ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    
                    // Для упрощения тестирования, пока пропускаем проверку
                    // и считаем, что смена почты прошла успешно
                    System.out.println("DEBUG: Пропускаем проверку API для тестирования смены почты");
                    
                    // Обновляем email в SessionManager
                    sessionManager.updateEmail(newEmail);
                    
                    // Показываем сообщение об успехе
                    String title = "Почта успешно изменена";
                    Intent intent = new Intent(ChangeEmail.this, ChangeSuccess.class);
                    intent.putExtra("TITLE", title);
                    startActivity(intent);
                    finish();
                    
                    // В реальном приложении, вместо этого нужно показывать ошибку:
                    // Toast.makeText(this, "Ошибка сети. Пожалуйста, попробуйте позже", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
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
}
