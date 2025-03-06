package com.example.dartmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.dartmobileapp.VideoAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feed extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<Video> videoList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String API_URL = "https://compassionate-bravery-production.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.profile_icon).setOnClickListener(v -> {
            Intent intent = new Intent(this, user_profile.class);
            startActivity(intent);
        });

        // Инициализация SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadVideos);
        swipeRefreshLayout.setColorSchemeResources(R.color.red);

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.videos_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VideoAdapter(videoList);
        recyclerView.setAdapter(adapter);

        // Загрузка видео
        loadVideos();
    }

    private void loadVideos() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL + "/video", null,
            response -> {
                try {
                    List<Video> videos = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject videoJson = response.getJSONObject(i);
                        Video video = new Video(
                            videoJson.getInt("id"),
                            videoJson.getString("title"),
                            videoJson.getString("description"),
                            videoJson.getString("iframe")
                        );
                        videos.add(video);
                    }
                    adapter.updateVideos(videos);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Ошибка при обработке данных", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            },
            error -> {
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        String errorResponse = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorResponse);
                        String errorMessage = errorJson.getString("message");
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Ошибка при загрузке видео", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Ошибка сети. Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }
}