package com.example.kataloghiburan.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Pastikan import di bawah ini disesuaikan dengan lokasi file User dan UserDao milikmu
import com.example.kataloghiburan.model.User;
import com.example.kataloghiburan.data.UserDao; // Sesuaikan jika UserDao kamu taruh di package 'local'

@Database(entities = {FavoriteMovie.class, User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // DAO untuk film favorit yang sudah ada sebelumnya
    public abstract FavoriteMovieDao favoriteMovieDao();

    // DAO baru untuk sistem User/Login
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "katalog_hiburan_db")
                            // Perintah ini sangat penting agar aplikasi tidak crash saat versi database naik dari 1 ke 2
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}