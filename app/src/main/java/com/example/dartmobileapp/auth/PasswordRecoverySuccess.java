package com.example.dartmobileapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.profile.UserProfile;
import com.example.dartmobileapp.ui.main.MainFrame;

public class PasswordRecoverySuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_recovery_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String from = getIntent().getStringExtra("COMING_FROM");

// Обработка кнопки "Далее"
        Button signUpButton = findViewById(R.id.nextButton_btn);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("LogInView".equals(from)) {
                    // Если пользователь пришёл с экрана LogInView, переходим на MainFrame
                    Intent intent = new Intent(PasswordRecoverySuccess.this, MainFrame.class);
                    startActivity(intent);
                } else if ("ChangePasswordView".equals(from)) {
                    // Если пользователь пришёл с экрана ChangePasswordView, переходим на UserProfile
                    Intent intent = new Intent(PasswordRecoverySuccess.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });

    }
}