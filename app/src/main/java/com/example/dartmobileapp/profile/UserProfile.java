package com.example.dartmobileapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.auth.VerifyPassword;
import com.example.dartmobileapp.feed.Feed;
import com.example.dartmobileapp.profile.edit.ChangePassword;
import com.example.dartmobileapp.ui.main.MainFrame;
import com.example.dartmobileapp.utils.SessionManager;

public class UserProfile extends AppCompatActivity {

    private TextView usernameText, emailText, passwordText;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Debug - dump all SharedPreferences values
        dumpSharedPreferences();

        // Initialize TextViews
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailtext);
        passwordText = findViewById(R.id.paswordtext);

        // Display user data
        displayUserData();

        // Кнопка выхода
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, MainFrame.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Кнопка Назад
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, Feed.class);
            startActivity(intent);
        });

        // Переход к смене почты
        findViewById(R.id.emailtext).setOnClickListener(v -> {
            Intent intent = new Intent(this, VerifyPassword.class);
            intent.putExtra("COMING_FROM", "ChangeEmail");
            startActivity(intent);
        });

        // Переход к имени пользователя
        findViewById(R.id.usernameText).setOnClickListener(v -> {
            Intent intent = new Intent(this, VerifyPassword.class);
            intent.putExtra("COMING_FROM", "ChangeUsername");
            startActivity(intent);
        });

        findViewById(R.id.paswordtext).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePassword.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to this activity
        displayUserData();
    }

    private void displayUserData() {
        // Check if user is logged in
        if (sessionManager.isLoggedIn()) {
            // Get user data from SessionManager
            String name = sessionManager.getUsername(); // This is actually the name field
            String email = sessionManager.getEmail();
            
            // Add debug logging
            android.util.Log.d("UserProfileDebug", "Name from SessionManager: " + name);
            android.util.Log.d("UserProfileDebug", "Email from SessionManager: " + email);
            
            // Display username and email
            usernameText.setText("@" + name); // Add the @ symbol back
            emailText.setText(email);
            passwordText.setText("••••••••"); // Display placeholder for password
        } else {
            // If not logged in, redirect to login
            Toast.makeText(this, "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainFrame.class);
            startActivity(intent);
            finish();
        }
    }

    private void dumpSharedPreferences() {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("AppSession", MODE_PRIVATE);
            android.util.Log.d("UserProfileDebug", "===== DUMPING SHARED PREFERENCES =====");
            android.util.Log.d("UserProfileDebug", "username: " + prefs.getString("username", "NOT FOUND"));
            android.util.Log.d("UserProfileDebug", "email: " + prefs.getString("email", "NOT FOUND"));
            android.util.Log.d("UserProfileDebug", "userId: " + prefs.getString("userId", "NOT FOUND"));
            android.util.Log.d("UserProfileDebug", "isLoggedIn: " + prefs.getBoolean("isLoggedIn", false));
            android.util.Log.d("UserProfileDebug", "token: " + (prefs.getString("userToken", "NOT FOUND") != null ? "EXISTS" : "NULL"));
            android.util.Log.d("UserProfileDebug", "===== END DUMP =====");
        } catch (Exception e) {
            android.util.Log.e("UserProfileDebug", "Error dumping SharedPreferences: " + e.getMessage());
        }
    }
}