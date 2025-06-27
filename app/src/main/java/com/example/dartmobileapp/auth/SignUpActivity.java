package com.example.dartmobileapp.auth;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.graphics.Rect;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dartmobileapp.R;
import com.example.dartmobileapp.feed.Feed;
import com.example.dartmobileapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.dartmobileapp.ui.main.MainFrame;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText, emailEditText, passwordEditText, passwordConfirmEditText;
    private Button signUpButton;
    private static final String API_URL = "https://dart-server-back-2.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        initializeViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // "Already have account" text click listener
        findViewById(R.id.accountExistsText).setOnClickListener(v -> {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        });

        // Sign up button click listener
        signUpButton.setOnClickListener(v -> validateAndSignUp());
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordcocnfirmEditText);
        signUpButton = findViewById(R.id.signupButton);
    }

    private void validateAndSignUp() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = passwordConfirmEditText.getText().toString();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Пожалуйста, заполните все поля");
            return;
        }

        if (!isValidEmail(email)) {
            showToast("Пожалуйста, введите корректный email");
            return;
        }

        if (password.length() < 6) {
            showToast("Пароль должен содержать минимум 6 символов");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Пароли не совпадают");
            return;
        }

        // If validation passes, proceed with registration
        performSignUp(username, email, password);
    }


    private void performSignUp(String username, String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Регистрация...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                API_URL + "/user/register",
                jsonBody,
                response -> {
                    progressDialog.dismiss();
                    try {
                        String token = response.getString("token");
                        
                        // For debugging
                        Log.d("SignupDebug", "Response: " + response.toString());
                        System.out.println("SIGNUP DEBUG - Response: " + response.toString());
                        
                        // Now that we have the token, let's fetch user data
                        fetchUserProfile(token, username, email);
                    } catch (JSONException e) {
                        showToast("Ошибка при обработке ответа сервера");
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    handleError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    
    private void fetchUserProfile(String token, String inputName, String inputEmail) {
        // Запрос на получение данных пользователя
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET,
                API_URL + "/user/profile",
                null,
                profileResponse -> {
                    try {
                        // Log the entire profile response for debugging
                        Log.d("SignupDebug", "Profile Response: " + profileResponse.toString());
                        System.out.println("SIGNUP DEBUG - Profile Response: " + profileResponse.toString());
                        
                        String userId = "user_" + System.currentTimeMillis(); // Placeholder ID
                        
                        // Extract user info
                        String name;
                        String email = inputEmail;
                        
                        // Try to get the name directly from the response
                        if (profileResponse.has("name")) {
                            name = profileResponse.getString("name");
                            Log.d("SignupDebug", "Name found in profile: " + name);
                            System.out.println("SIGNUP DEBUG - Name found in profile: " + name);
                        } else if (profileResponse.has("user") && profileResponse.getJSONObject("user").has("name")) {
                            name = profileResponse.getJSONObject("user").getString("name");
                            Log.d("SignupDebug", "Name found in user object: " + name);
                            System.out.println("SIGNUP DEBUG - Name found in user object: " + name);
                        } else {
                            // Default fallback - use the input name provided during registration
                            name = inputName;
                            Log.d("SignupDebug", "No name found in profile, using input name: " + name);
                            System.out.println("SIGNUP DEBUG - No name found in profile, using input name: " + name);
                        }
                        
                        // Try to get the email
                        if (profileResponse.has("email")) {
                            email = profileResponse.getString("email");
                        } else if (profileResponse.has("user") && profileResponse.getJSONObject("user").has("email")) {
                            email = profileResponse.getJSONObject("user").getString("email");
                        }
                        
                        Log.d("SignupDebug", "Final values - Name: " + name + ", Email: " + email);
                        System.out.println("SIGNUP DEBUG - Final values - Name: " + name + ", Email: " + email);
                        
                        // Create session
                        SessionManager sessionManager = new SessionManager(SignUpActivity.this);
                        sessionManager.createSession(token, userId, name, email);
                        
                        navigateToFeed();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("SignupDebug", "Error parsing profile: " + e.getMessage());
                        createSessionWithInput(token, inputName, inputEmail);
                    }
                },
                error -> {
                    Log.e("SignupDebug", "Error fetching profile: " + error.toString());
                    // If we can't get the profile, create session with input values
                    createSessionWithInput(token, inputName, inputEmail);
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
    
    private void createSessionWithInput(String token, String name, String email) {
        String userId = "user_" + System.currentTimeMillis();
        
        SessionManager sessionManager = new SessionManager(SignUpActivity.this);
        sessionManager.createSession(token, userId, name, email);
        
        navigateToFeed();
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("userToken", token).apply();
    }

    private void navigateToFeed() {
        Intent intent = new Intent(SignUpActivity.this, Feed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleError(VolleyError error) {
        if (error.networkResponse != null) {
            try {
                String errorResponse = new String(error.networkResponse.data);
                JSONObject errorJson = new JSONObject(errorResponse);
                String errorMessage = errorJson.getString("message");
                showToast(errorMessage);
            } catch (JSONException e) {
                showToast("Ошибка при регистрации");
            }
        } else {
            showToast("Ошибка сети. Проверьте подключение к интернету");
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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