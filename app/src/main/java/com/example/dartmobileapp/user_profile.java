package com.example.dartmobileapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class user_profile extends AppCompatActivity {

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

        // Кнопка выхода
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainFrame.class);
            startActivity(intent);
        });

        // Кнопка Назад
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Переход к смене почты
        findViewById(R.id.emailtext).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangeEmail.class);
            startActivity(intent);
        });
    }
}