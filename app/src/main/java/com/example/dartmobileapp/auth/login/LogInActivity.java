package com.example.dartmobileapp.auth.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.auth.RestorePassword;
import com.example.dartmobileapp.feed.ui.Feed;

public class LogInActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // ... existing view setup code ...

        findViewById(R.id.forgotPasswordText).setOnClickListener(v -> {
            Intent intent = new Intent(this, RestorePassword.class);
            intent.putExtra("COMING_FROM", "LogInView");
            startActivity(intent);
        });

        // ... rest of the code ...
    }

    private void navigateToFeed() {
        Intent intent = new Intent(LogInActivity.this, Feed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 