package com.example.kataloghiburan.network;

import com.example.kataloghiburan.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // Endpoint untuk mengambil film populer dari TMDB
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey
    );
}