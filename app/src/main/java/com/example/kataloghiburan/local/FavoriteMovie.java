package com.example.kataloghiburan.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Ini akan menjadi nama tabel di dalam SQLite
@Entity(tableName = "favorite_movies")
public class FavoriteMovie {

    @PrimaryKey
    private int id;

    private String title;
    private String overview;
    private String posterPath;

    // Tambahan Fase 3: Kolom untuk Rating dan Ulasan Personal
    @ColumnInfo(name = "user_rating")
    private float userRating;

    @ColumnInfo(name = "user_review")
    private String userReview;

    // Tambahan Fase 4: Kolom untuk Jadwal Nonton
    @ColumnInfo(name = "watch_date")
    private String watchDate;

    @ColumnInfo(name = "watch_time")
    private String watchTime;

    // --- Getter dan Setter ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public float getUserRating() { return userRating; }
    public void setUserRating(float userRating) { this.userRating = userRating; }

    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }

    public String getWatchDate() { return watchDate; }
    public void setWatchDate(String watchDate) { this.watchDate = watchDate; }

    public String getWatchTime() { return watchTime; }
    public void setWatchTime(String watchTime) { this.watchTime = watchTime; }
}