package com.example.dartmobileapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.profile.edit.ChangeSuccess;

public class ChangeEmail extends AppCompatActivity {

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

        // Обрабатываем нажатие на кнопку "Назад"
        findViewById(R.id.backButton).setOnClickListener(v -> finish());






        // Получаем кнопку обновления
        Button updateButton = findViewById(R.id.updateButton);

        // Устанавливаем обработчик нажатия
        updateButton.setOnClickListener(v -> {
            // Пример текста, который передаем
            String title = "Почта успешно изменена";
            Intent intent = new Intent(ChangeEmail.this, ChangeSuccess.class);
            intent.putExtra("TITLE", title);
            startActivity(intent);
        });
    }
}
