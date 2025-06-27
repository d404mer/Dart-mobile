package com.example.dartmobileapp.feed.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dartmobileapp.R;
import com.example.dartmobileapp.common.network.ApiClient;
import com.example.dartmobileapp.feed.adapter.VideoAdapter;
import com.example.dartmobileapp.model.Video;
import com.example.dartmobileapp.profile.main.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Feed extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<Video> videoList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed);

        // ... existing view setup code ...

        findViewById(R.id.profile_icon).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });

        // ... rest of the code ...
    }
}