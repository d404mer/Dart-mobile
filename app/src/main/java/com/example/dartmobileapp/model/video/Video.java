package com.example.dartmobileapp.model.video;

public class Video {
    private int id;
    private String title;
    private String description;
    private String iframe;

    // Конструктор
    public Video(int id, String title, String description, String iframe) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iframe = iframe;
    }

    // Геттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIframe() { return iframe; }
}