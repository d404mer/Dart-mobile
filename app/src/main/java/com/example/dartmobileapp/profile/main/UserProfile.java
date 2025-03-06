package com.example.dartmobileapp.profile.main;

import android.content.Intent;
import android.os.Bundle;

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

public class UserProfile extends AppCompatActivity {

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
}