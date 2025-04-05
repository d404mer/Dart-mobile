package com.example.dartmobileapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppSession";
    private static final String KEY_TOKEN = "userToken";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createSession(String token, String userId, String username, String email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void updateUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void updateEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void updatePassword(String password) {
        // Для демо-режима пароль просто сохраняется локально
        // В реальном приложении здесь должно быть безопасное хранение
        editor.putString("password", password);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    // Метод для доступа к SharedPreferences напрямую
    public SharedPreferences getSharedPreferences() {
        return prefs;
    }
} 