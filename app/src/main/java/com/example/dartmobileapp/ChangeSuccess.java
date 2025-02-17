package com.example.dartmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangeSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем текст из Intent
        String title = getIntent().getStringExtra("TITLE");

        // Устанавливаем заголовок
        TextView titleText = findViewById(R.id.title);
        if (title != null) {
            titleText.setText(title);
        }


        Button signUpButton = findViewById(R.id.nextButton_btn);
        signUpButton.setOnClickListener(v -> {
            // Переход в SignUpActivity
            Intent intent = new Intent(ChangeSuccess.this,  Feed.class);
            startActivity(intent);
        });
    }
}