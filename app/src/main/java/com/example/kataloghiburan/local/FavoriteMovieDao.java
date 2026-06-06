package com.example.kataloghiburan.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteMovieDao {

    // Menyimpan data. Jika ID sudah ada, timpa (REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteMovie movie);

    // Menghapus data
    @Delete
    void deleteFavorite(FavoriteMovie movie);

    // Mengambil semua film favorit
    @Query("SELECT * FROM favorite_movies")
    List<FavoriteMovie> getAllFavorites();

    // Mengecek apakah sebuah film sudah difavoritkan
    @Query("SELECT * FROM favorite_movies WHERE id = :id LIMIT 1")
    FavoriteMovie getFavoriteById(int id);
}