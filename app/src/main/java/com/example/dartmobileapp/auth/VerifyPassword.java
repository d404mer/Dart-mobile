package com.example.dartmobileapp.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.profile.ChangeEmail;
import com.example.dartmobileapp.profile.edit.ChangeUsername;
import com.example.dartmobileapp.profile.UserProfile;

public class VerifyPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String from = getIntent().getStringExtra("COMING_FROM");

        findViewById(R.id.nextButton).setOnClickListener(v -> {
            if ("ChangeEmail".equals(from))
            {
                startActivity(new Intent(VerifyPassword.this, ChangeEmail.class));
            } else if ("ChangeUsername".equals(from))
            {
                startActivity(new Intent(VerifyPassword.this, ChangeUsername.class));
            }
        });



        // Кнопка Назад
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });

    }
}