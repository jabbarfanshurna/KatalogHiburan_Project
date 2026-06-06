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

    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    // Ganti teks di bawah dengan API Key dari TMDB kamu
    private static final String API_KEY = "ee577d1401cb1a62355ac90f7458be06";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Inisialisasi View dari XML
        rvMovies = view.findViewById(R.id.rvMovies);
        progressBar = view.findViewById(R.id.progressBar);
        layoutError = view.findViewById(R.id.layoutError);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        // 2. Konfigurasi RecyclerView
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MovieAdapter(getContext(), movieList);
        rvMovies.setAdapter(adapter);

        // 3. Panggil fungsi untuk mengambil data dari internet
        fetchMovies();

        // 4. Setup Tombol Refresh jika terjadi error/tidak ada jaringan
        btnRefresh.setOnClickListener(v -> fetchMovies());

        return view;
    }

    private void fetchMovies() {
        // Tampilkan loading, sembunyikan pesan error dan daftar film sementara
        progressBar.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvMovies.setVisibility(View.GONE);

        // Inisialisasi Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY);

        // Eksekusi pemanggilan API secara Asynchronous
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                progressBar.setVisibility(View.GONE);

                // Jika data berhasil ditarik dari server
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged(); // Perbarui adapter
                    rvMovies.setVisibility(View.VISIBLE); // Tampilkan daftar film
                } else {
                    // Jika server menolak (misal: API Key salah)
                    layoutError.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Gagal memuat data dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Jika koneksi internet terputus atau time out
                progressBar.setVisibility(View.GONE);
                layoutError.setVisibility(View.VISIBLE); // Munculkan tombol Refresh
            }
        });
    }
}