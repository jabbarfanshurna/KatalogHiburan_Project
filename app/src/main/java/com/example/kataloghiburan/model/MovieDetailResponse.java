package com.example.kataloghiburan.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieDetailResponse {

    @SerializedName("runtime")
    private int runtime; // Durasi dalam bentuk menit

    @SerializedName("genres")
    private List<Genre> genres; // Daftar genre

    @SerializedName("release_date")
    private String releaseDate;

    public int getRuntime() {
        return runtime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}