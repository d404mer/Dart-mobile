package com.example.dartmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import com.android.volley.DefaultRetryPolicy;

public class LogInActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText, passwordEditText;
    private MaterialButton loginButton;
    private static final String API_URL = "https://compassionate-bravery-production.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // Initialize views
        initializeViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Forgot password click listener
        findViewById(R.id.forgotPasswordText).setOnClickListener(v -> {
            Intent intent = new Intent(this, RestorePassword.class);
            intent.putExtra("COMING_FROM", "LogInView");
            startActivity(intent);
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> validateAndLogin());
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.signupButton);
    }

    private void validateAndLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Пожалуйста, заполните все поля");
            return;
        }

        // Proceed with login
        performLogin(username, password);
    }

    private void performLogin(String username, String password) {
        // Для отладки
        Log.d("LoginDebug", "Username: " + username);
        Log.d("LoginDebug", "Password: " + password);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", username);
            jsonBody.put("password", password);
            
            // Для отладки
            Log.d("LoginDebug", "Request body: " + jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Ошибка при формировании запроса");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                API_URL + "/user/login",
                jsonBody,
                response -> {
                    // Для отладки
                    Log.d("LoginDebug", "Success response: " + response.toString());
                    try {
                        String token = response.getString("token");
                        saveToken(token);
                        navigateToFeed();
                    } catch (JSONException e) {
                        showToast("Ошибка при обработке ответа сервера");
                    }
                },
                error -> {
                    // Для отладки
                    Log.e("LoginDebug", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("LoginDebug", "Error code: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e("LoginDebug", "Error response: " + errorResponse);
                        }
                    }

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data);
                            JSONObject errorJson = new JSONObject(errorResponse);
                            String errorMessage = errorJson.getString("message");
                            showToast(errorMessage);
                        } catch (JSONException e) {
                            showToast("Неверное имя пользователя или пароль");
                        }
                    } else {
                        showToast("Ошибка сети. Проверьте подключение к интернету");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Добавляем таймаут для запроса
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("userToken", token).apply();
    }

    private void navigateToFeed() {
        Intent intent = new Intent(LogInActivity.this, Feed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocusView = getCurrentFocus();
            if (currentFocusView instanceof EditText) {
                Rect outRect = new Rect();
                currentFocusView.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    currentFocusView.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}