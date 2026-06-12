package com.example.kataloghiburan.network;

import com.example.kataloghiburan.model.MovieDetailResponse;
import com.example.kataloghiburan.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint untuk mengambil film populer dari TMDB
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey
    );

    // Endpoint tambahan untuk mencari film
    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    // Endpoint baru untuk mengambil detail spesifik (Durasi, Genre) berdasarkan ID Film
    @GET("movie/{movie_id}")
    Call<MovieDetailResponse> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    // ... (endpoint yang sudah ada sebelumnya biarkan saja) ...

    // TAMBAHAN: Endpoint untuk filter film berdasarkan Genre
    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("with_genres") int genreId
    );
}