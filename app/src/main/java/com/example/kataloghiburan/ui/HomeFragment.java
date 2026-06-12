package com.example.kataloghiburan.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.model.Movie;
import com.example.kataloghiburan.model.MovieResponse;
import com.example.kataloghiburan.network.ApiClient;
import com.example.kataloghiburan.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvMovies;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private Button btnRefresh;
    private SearchView searchView;

    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    // Menggunakan API Key dari TMDB kamu
    private static final String API_KEY = "ee577d1401cb1a62355ac90f7458be06";
    private String currentQuery = ""; // Menyimpan status pencarian terakhir

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Inisialisasi View dari XML
        rvMovies = view.findViewById(R.id.rvMovies);
        progressBar = view.findViewById(R.id.progressBar);
        layoutError = view.findViewById(R.id.layoutError);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        searchView = view.findViewById(R.id.searchView);

        // 2. Konfigurasi RecyclerView
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MovieAdapter(getContext(), movieList);
        rvMovies.setAdapter(adapter);

        // 3. Panggil fungsi untuk mengambil data (Awal buka: film populer)
        fetchMovies();

        // 4. Setup Tombol Refresh jika terjadi error/tidak ada jaringan
        btnRefresh.setOnClickListener(v -> {
            if (currentQuery.isEmpty()) {
                fetchMovies();
            } else {
                searchMoviesFromApi(currentQuery);
            }
        });

        // 5. Setup Aksi Pencarian (Search View)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    currentQuery = query;
                    searchMoviesFromApi(query);
                    searchView.clearFocus(); // Menyembunyikan keyboard setelah mencari
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Jika kolom pencarian dihapus sampai kosong, kembalikan ke list populer
                if (newText.isEmpty()) {
                    currentQuery = "";
                    fetchMovies();
                }
                return false;
            }
        });

        return view;
    }

    // Fungsi untuk menarik film populer
    private void fetchMovies() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY);
        executeApiCall(call);
    }

    // Fungsi untuk mencari film berdasarkan input user
    private void searchMoviesFromApi(String query) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MovieResponse> call = apiService.searchMovies(API_KEY, query);
        executeApiCall(call);
    }

    // Fungsi pembantu untuk memproses respon API agar tidak menulis kode berulang
    private void executeApiCall(Call<MovieResponse> call) {
        progressBar.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvMovies.setVisibility(View.GONE);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    rvMovies.setVisibility(View.VISIBLE);

                    // Notifikasi jika film yang dicari tidak ada
                    if (movieList.isEmpty() && getContext() != null) {
                        Toast.makeText(getContext(), "Film tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Gagal memuat data dari server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                layoutError.setVisibility(View.VISIBLE);
            }
        });
    }
}