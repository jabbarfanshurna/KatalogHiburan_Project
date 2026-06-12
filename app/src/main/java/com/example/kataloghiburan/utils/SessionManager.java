package com.example.kataloghiburan.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Nama file memori
    private static final String PREF_NAME = "LoginSession";

    // Kunci-kunci data
    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_EMAIL = "EMAIL";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Fungsi untuk menyimpan sesi saat berhasil login
    public void createLoginSession(String username, String email) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Fungsi untuk mengecek apakah user sedang login (dipakai di Splash Screen)
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    // Fungsi untuk mengambil nama user
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "Kennan");
    }

    // Fungsi untuk mengambil email user
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "kennan@kataloghiburan.com");
    }

    // Fungsi untuk menghapus sesi saat Logout
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}