package com.example.dartmobileapp.profile.edit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.auth.RestorePassword;
import com.example.dartmobileapp.profile.UserProfile;
import com.example.dartmobileapp.utils.SessionManager;

public class ChangePassword extends AppCompatActivity {
    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация SessionManager
        sessionManager = new SessionManager(this);

        // Инициализация полей ввода
        currentPasswordInput = findViewById(R.id.currentpassword_input);
        newPasswordInput = findViewById(R.id.newpassword_input);
        confirmPasswordInput = findViewById(R.id.newpassworconfirm_input);

        // Забыли пароль
        findViewById(R.id.forgotPasswordText).setOnClickListener(v -> {
            Intent intent = new Intent(this, RestorePassword.class);
            intent.putExtra("COMING_FROM", "ChangePasswordView");
            startActivity(intent);
        });

        // Кнопка Назад
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });

        Button updateButton = findViewById(R.id.updateButton);
        // Устанавливаем обработчик нажатия
        updateButton.setOnClickListener(v -> {
            // Получаем введенные данные
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            
            // Проверяем пустые поля
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Проверяем, что новый пароль соответствует требованиям безопасности
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Новый пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Проверяем, что пароли совпадают
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Новые пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Проверяем, что новый пароль отличается от текущего
            if (newPassword.equals(currentPassword)) {
                Toast.makeText(this, "Новый пароль должен отличаться от текущего", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Для демо-режима считаем, что текущий пароль всегда верный
            // и обновляем пароль локально
            updatePasswordLocally(newPassword);
        });
    }
    
    private void updatePasswordLocally(String newPassword) {
        // Обновляем пароль пользователя в SessionManager
        sessionManager.updatePassword(newPassword);
        
        // Показываем сообщение об успешном обновлении
        Toast.makeText(this, "Пароль успешно обновлен", Toast.LENGTH_SHORT).show();
        
        // Переходим на экран успешного обновления
        String title = "Ваш пароль был успешно изменён!";
        Intent intent = new Intent(ChangePassword.this, ChangeSuccess.class);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }
}