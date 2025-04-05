package com.example.dartmobileapp.auth;

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
import com.example.dartmobileapp.R;
import com.example.dartmobileapp.feed.Feed;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.example.dartmobileapp.ui.main.MainFrame;
import com.example.dartmobileapp.utils.SessionManager;

public class LogInActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText, passwordEditText;
    private MaterialButton loginButton;
    private static final String API_URL = "https://dart-server-back.up.railway.app/api";

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
                    try {
                        // Log the entire response for debugging
                        Log.d("LoginDebug", "Response: " + response.toString());
                        System.out.println("LOGIN DEBUG - Login Response: " + response.toString());
                        
                        // Extract token from response
                        String token = response.getString("token");
                        
                        // Now that we have the token, let's fetch user data
                        fetchUserProfile(token, username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("LoginDebug", "JSON Error: " + e.getMessage());
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
    
    private void fetchUserProfile(String token, String fallbackEmail) {
        // Запрос на получение данных пользователя
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET,
                API_URL + "/user/profile",
                null,
                profileResponse -> {
                    try {
                        // Log the entire profile response for debugging
                        Log.d("LoginDebug", "Profile Response: " + profileResponse.toString());
                        System.out.println("LOGIN DEBUG - Profile Response: " + profileResponse.toString());
                        
                        String userId = "user_" + System.currentTimeMillis(); // Placeholder ID
                        
                        // Extract user info
                        String name;
                        String email = fallbackEmail;
                        
                        // Try to get the name directly from the response
                        if (profileResponse.has("name")) {
                            name = profileResponse.getString("name");
                            Log.d("LoginDebug", "Name found in profile: " + name);
                            System.out.println("LOGIN DEBUG - Name found in profile: " + name);
                        } else if (profileResponse.has("user") && profileResponse.getJSONObject("user").has("name")) {
                            name = profileResponse.getJSONObject("user").getString("name");
                            Log.d("LoginDebug", "Name found in user object: " + name);
                            System.out.println("LOGIN DEBUG - Name found in user object: " + name);
                        } else {
                            // Default fallback - use the part before @ in email
                            name = fallbackEmail.split("@")[0];
                            Log.d("LoginDebug", "No name found in profile, using fallback: " + name);
                            System.out.println("LOGIN DEBUG - No name found in profile, using fallback: " + name);
                        }
                        
                        // Try to get the email
                        if (profileResponse.has("email")) {
                            email = profileResponse.getString("email");
                        } else if (profileResponse.has("user") && profileResponse.getJSONObject("user").has("email")) {
                            email = profileResponse.getJSONObject("user").getString("email");
                        }
                        
                        Log.d("LoginDebug", "Final values - Name: " + name + ", Email: " + email);
                        System.out.println("LOGIN DEBUG - Final values - Name: " + name + ", Email: " + email);
                        
                        // Create session
                        SessionManager sessionManager = new SessionManager(LogInActivity.this);
                        sessionManager.createSession(token, userId, name, email);
                        
                        navigateToFeed();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("LoginDebug", "Error parsing profile: " + e.getMessage());
                        createSessionWithFallback(token, fallbackEmail);
                    }
                },
                error -> {
                    Log.e("LoginDebug", "Error fetching profile: " + error.toString());
                    // If we can't get the profile, create session with fallback values
                    createSessionWithFallback(token, fallbackEmail);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        
        profileRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        
        Volley.newRequestQueue(this).add(profileRequest);
    }
    
    private void createSessionWithFallback(String token, String email) {
        String userId = "user_" + System.currentTimeMillis();
        String name = email.split("@")[0]; // Fallback to part of email
        
        SessionManager sessionManager = new SessionManager(LogInActivity.this);
        sessionManager.createSession(token, userId, name, email);
        
        navigateToFeed();
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