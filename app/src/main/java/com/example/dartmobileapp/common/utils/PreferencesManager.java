package com.example.dartmobileapp.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_USER_TOKEN = "userToken";
    
    private SharedPreferences preferences;
    private static PreferencesManager instance;

    private PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public void saveToken(String token) {
        preferences.edit().putString(KEY_USER_TOKEN, token).apply();
    }

    public String getToken() {
        return preferences.getString(KEY_USER_TOKEN, null);
    }

    public void clearToken() {
        preferences.edit().remove(KEY_USER_TOKEN).apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
} 