package com.example.dartmobileapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dartmobileapp.feed.Feed;
import com.example.dartmobileapp.profile.UserProfile;
import com.example.dartmobileapp.feed.VideoAdapter;
import com.example.dartmobileapp.model.Video;
import com.example.dartmobileapp.utils.SessionManager;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class activityVideoDetail extends AppCompatActivity {

    private WebView videoPlayer;
    private TextView videoTitle, videoViews, videoDescription, showMore;
    private TextView likeCount;
    private ImageView likeIcon;
    private LinearLayout likeButton;
    private RecyclerView relatedVideosRecyclerView;
    private VideoAdapter relatedVideosAdapter;
    private List<Video> relatedVideosList = new ArrayList<>();
    private static final String API_URL = "https://dart-server-back-2.up.railway.app/api";


    private Video currentVideo;
    private boolean isDescriptionExpanded = false;
    private boolean isLiked = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Initialize views
        initializeViews();

        // Get video data from intent
        if (getIntent().hasExtra("VIDEO_ID")) {
            int videoId = getIntent().getIntExtra("VIDEO_ID", -1);
            loadVideoDetails(videoId);
            // Check if user is logged in to get like status
            if (sessionManager.isLoggedIn()) {
                checkLikeStatus(videoId);
            }
        } else if (getIntent().hasExtra("VIDEO_OBJECT")) {
            // If we passed a serialized Video object directly
            currentVideo = (Video) getIntent().getSerializableExtra("VIDEO_OBJECT");
            displayVideoDetails(currentVideo);
            // Check like status if logged in
            if (sessionManager.isLoggedIn() && currentVideo != null) {
                checkLikeStatus(currentVideo.getId());
            }
        } else {
            Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up click listeners
        setupClickListeners();

        // Load related videos
        loadRelatedVideos();
    }

    private void initializeViews() {
        videoPlayer = findViewById(R.id.video_player);
        videoTitle = findViewById(R.id.video_title);
        videoViews = findViewById(R.id.video_views);
        videoDescription = findViewById(R.id.video_description);
        showMore = findViewById(R.id.show_more);
        likeCount = findViewById(R.id.like_count);
        likeButton = findViewById(R.id.like_button);
        likeIcon = likeButton.findViewById(R.id.like_icon);

        // Set up RecyclerView for related videos
        relatedVideosRecyclerView = findViewById(R.id.related_videos_recycler_view);
        relatedVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        relatedVideosAdapter = new VideoAdapter(relatedVideosList);
        relatedVideosRecyclerView.setAdapter(relatedVideosAdapter);

        // Configure WebView for video playback
        videoPlayer.getSettings().setJavaScriptEnabled(true);
        videoPlayer.getSettings().setDomStorageEnabled(true);
        videoPlayer.getSettings().setLoadWithOverviewMode(true);
        videoPlayer.getSettings().setUseWideViewPort(true);
    }

    private void setupClickListeners() {
        // Profile icon click
        findViewById(R.id.profile_icon).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });

        // Logo click - return to feed
        findViewById(R.id.logo).setOnClickListener(v -> {
            Intent intent = new Intent(this, Feed.class);
            startActivity(intent);
        });

        // Show more/less for description
        showMore.setOnClickListener(v -> {
            isDescriptionExpanded = !isDescriptionExpanded;
            if (isDescriptionExpanded) {
                videoDescription.setMaxLines(Integer.MAX_VALUE);
                showMore.setText("Show less");
            } else {
                videoDescription.setMaxLines(3);
                showMore.setText("Show more");
            }
        });

        // Like button click
        likeButton.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn() && currentVideo != null) {
                toggleLike(currentVideo.getId());
            } else {
                toggleLike(currentVideo.getId());
            }
        });
    }

    private void loadVideoDetails(int videoId) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL + "/video", null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject videoJson = response.getJSONObject(i);
                            if (videoJson.getInt("id") == videoId) {
                                Video video = new Video(
                                        videoJson.getInt("id"),
                                        videoJson.getString("title"),
                                        videoJson.getString("description"),
                                        videoJson.getString("iframe")
                                );
                                currentVideo = video;
                                displayVideoDetails(video);
                                
                                // Now fetch the like count
                                getLikeCount(videoId);
                                
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка при обработке данных", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Ошибка при загрузке видео", Toast.LENGTH_SHORT).show();
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

    private void getLikeCount(int videoId) {
        // Using placeholder like count instead of API call
        // This will be updated with real API call later
        likeCount.setText("205K");
        
        /* Commented out API call
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                API_URL + "/like/count/" + videoId, null,
                response -> {
                    try {
                        if (response.has("count")) {
                            String count = response.getString("count");
                            likeCount.setText(count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // If there's an error, just leave the default count
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
        */
    }

    private void displayVideoDetails(Video video) {
        videoTitle.setText(video.getTitle());
        videoViews.setText("4.7M views • 2 months ago"); // This would normally come from the API
        videoDescription.setText(video.getDescription());

        // Limit description and add show more functionality
        videoDescription.setMaxLines(3);

        // Get the like count for this video
        getLikeCount(video.getId());

        // Set up WebView to display video
        String videoHtml =
                "<html>" +
                        "<head>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "body { margin: 0; padding: 0; background-color: #000000; }" +
                        ".video-container { position: relative; width: 100%; height: 100%; }" +
                        "iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='video-container'>" +
                        video.getIframe() +
                        "</div>" +
                        "</body>" +
                        "</html>";

        videoPlayer.loadDataWithBaseURL(null, videoHtml, "text/html", "utf-8", null);
    }

    private void loadRelatedVideos() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL + "/video", null,
                response -> {
                    try {
                        List<Video> videos = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject videoJson = response.getJSONObject(i);
                            // Skip the current video
                            if (currentVideo != null && videoJson.getInt("id") == currentVideo.getId()) {
                                continue;
                            }
                            Video video = new Video(
                                    videoJson.getInt("id"),
                                    videoJson.getString("title"),
                                    videoJson.getString("description"),
                                    videoJson.getString("iframe")
                            );
                            videos.add(video);
                        }
                        relatedVideosAdapter.updateVideos(videos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка при обработке данных", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Ошибка при загрузке рекомендаций", Toast.LENGTH_SHORT).show();
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

    private void checkLikeStatus(int videoId) {
        // Using placeholder value instead of API call
        // This will be updated with real API call later
        updateLikeUI(false);
        
        /* Commented out API call
        String token = sessionManager.getToken();
        if (token == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                API_URL + "/like/" + videoId, null,
                response -> {
                    try {
                        boolean liked = response.getBoolean("liked");
                        updateLikeUI(liked);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // If error is 404, it means the user hasn't liked the video
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        updateLikeUI(false);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
        */
    }

    private void toggleLike(int videoId) {
        // Toggle the local state
        isLiked = !isLiked;
        updateLikeUI(isLiked);
        
        // Update like count based on state (placeholder)
        if (isLiked) {
            likeCount.setText("206K");
        } else {
            likeCount.setText("205K");
        }
        
        /* Commented out API call
        String token = sessionManager.getToken();
        // Remove token check
        // if (token == null) return;

        // Toggle the local state first for immediate feedback
        isLiked = !isLiked;
        updateLikeUI(isLiked);

        // Prepare API request - POST to like, DELETE to unlike
        int method = isLiked ? Request.Method.POST : Request.Method.DELETE;
        String url = API_URL + "/like/" + videoId;

        JsonObjectRequest request = new JsonObjectRequest(method, url, null,
                response -> {
                    try {
                        // Update like count from response
                        if (response.has("likeCount")) {
                            String count = response.getString("likeCount");
                            likeCount.setText(count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Revert UI if request fails but don't show error message
                    isLiked = !isLiked;
                    updateLikeUI(isLiked);
                    // Removing error message
                    // Toast.makeText(this, "Ошибка при обновлении лайка", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
        */
    }

    private void updateLikeUI(boolean liked) {
        isLiked = liked;
        
        // Update the like icon
        if (liked) {
            likeIcon.setImageResource(R.drawable.thumbs_up_filled);
        } else {
            likeIcon.setImageResource(R.drawable.thumbs_up);
        }
    }
}
