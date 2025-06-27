package com.example.dartmobileapp.feed.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dartmobileapp.R;

import java.util.List;

import com.example.dartmobileapp.activityVideoDetail;
import com.example.dartmobileapp.model.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<Video> videos;

    public VideoAdapter(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.titleView.setText(video.getTitle());
        holder.descriptionView.setText(video.getDescription());

        // Make the entire item clickable
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), activityVideoDetail.class);
            intent.putExtra("VIDEO_ID", video.getId());
            holder.itemView.getContext().startActivity(intent);
        });

        // Настраиваем WebView для отображения iframe
        holder.webView.getSettings().setJavaScriptEnabled(true);
        holder.webView.getSettings().setLoadWithOverviewMode(true);
        holder.webView.getSettings().setUseWideViewPort(true);
        holder.webView.getSettings().setDomStorageEnabled(true); // Важно для Rutube

        String videoHtml =
                "<html>" +
                        "<head>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "body { margin: 0; padding: 0; }" +
                        ".video-container { position: relative; width: 100%; height: 200px; }" +
                        "iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='video-container'>" +
                        video.getIframe() + // Используем полный iframe как есть
                        "</div>" +
                        "</body>" +
                        "</html>";

        holder.webView.loadDataWithBaseURL(null, videoHtml, "text/html", "utf-8", null);
    }

    private String extractVideoId(String iframe) {
        // Извлекаем ID видео из iframe строки
        if (iframe != null && iframe.contains("youtube.com/embed/")) {
            int startIndex = iframe.indexOf("embed/") + 6;
            int endIndex = iframe.indexOf("\"", startIndex);
            if (endIndex == -1) {
                endIndex = iframe.indexOf("'", startIndex);
            }
            if (startIndex != -1 && endIndex != -1) {
                return iframe.substring(startIndex, endIndex);
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void updateVideos(List<Video> newVideos) {
        this.videos = newVideos;
        notifyDataSetChanged();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView descriptionView;
        WebView webView;

        VideoViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.video_title);
            descriptionView = itemView.findViewById(R.id.video_description);
            webView = itemView.findViewById(R.id.video_webview);
        }
    }

}