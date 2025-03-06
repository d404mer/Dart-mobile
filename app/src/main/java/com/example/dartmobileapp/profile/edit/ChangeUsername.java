package com.example.dartmobileapp.profile.edit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dartmobileapp.R;

public class ChangeUsername extends AppCompatActivity {

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



        // Обрабатываем нажатие на кнопку "Назад"
        findViewById(R.id.backButton).setOnClickListener(v -> finish());






        // Получаем кнопку обновления
        Button updateButton = findViewById(R.id.updateButton);

        // Устанавливаем обработчик нажатия
        updateButton.setOnClickListener(v -> {
            // Пример текста, который передаем
            String title = "Имя пользователя успешно изменено";

            // Создаем Intent для перехода на ChangeSuccess
            Intent intent = new Intent(ChangeUsername.this, ChangeSuccess.class);
            // Добавляем строку в Intent
            intent.putExtra("TITLE", title);
            // Запускаем новое Activity
            startActivity(intent);
        });
    }
}