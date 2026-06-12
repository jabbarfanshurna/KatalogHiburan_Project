package com.example.kataloghiburan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.local.FavoriteMovie;
import com.example.kataloghiburan.model.Genre;
import com.example.kataloghiburan.model.MovieDetailResponse;
import com.example.kataloghiburan.network.ApiClient;
import com.example.kataloghiburan.network.ApiService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private ExecutorService executorService;
    private boolean isFavorite = false;

    private TextView tvDetailRuntime;
    private TextView tvDetailGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Inisialisasi Database dan Executor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        ImageView imgDetailPoster = findViewById(R.id.imgDetailPoster);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailOverview = findViewById(R.id.tvDetailOverview);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnFavorite = findViewById(R.id.btnFavorite);
        MaterialButton btnShare = findViewById(R.id.btnShare); // Inisialisasi Tombol Share

        tvDetailRuntime = findViewById(R.id.tvDetailRuntime);
        tvDetailGenres = findViewById(R.id.tvDetailGenres);

        // Menangkap data
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String overview = getIntent().getStringExtra("EXTRA_OVERVIEW");
        String posterPath = getIntent().getStringExtra("EXTRA_POSTER");
        int movieId = getIntent().getIntExtra("EXTRA_ID", 0);

        tvDetailTitle.setText(title);
        tvDetailOverview.setText(overview);

        String posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
        Glide.with(this)
                .load(posterUrl)
                .into(imgDetailPoster);

        // Aksi Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Memanggil API Detail Film
        fetchMovieDetails(movieId);

        // Mengecek status favorit
        executorService.execute(() -> {
            FavoriteMovie existingMovie = database.favoriteMovieDao().getFavoriteById(movieId);
            if (existingMovie != null) {
                isFavorite = true;
                runOnUiThread(() -> btnFavorite.setText("Hapus dari Favorit"));
            }
        });

        // Aksi Tombol Favorit
        btnFavorite.setOnClickListener(v -> {
            FavoriteMovie favMovie = new FavoriteMovie();
            favMovie.setId(movieId);
            favMovie.setTitle(title);
            favMovie.setOverview(overview);
            favMovie.setPosterPath(posterPath);

            executorService.execute(() -> {
                if (!isFavorite) {
                    database.favoriteMovieDao().insertFavorite(favMovie);
                    isFavorite = true;
                    runOnUiThread(() -> {
                        btnFavorite.setText("Hapus dari Favorit");
                        Toast.makeText(DetailActivity.this, "Berhasil disimpan ke Favorit!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    database.favoriteMovieDao().deleteFavorite(favMovie);
                    isFavorite = false;
                    runOnUiThread(() -> {
                        btnFavorite.setText("Tambah ke Favorit");
                        Toast.makeText(DetailActivity.this, "Dihapus dari Favorit!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // --- TAMBAHAN FASE 3: Aksi Tombol Share ---
        btnShare.setOnClickListener(v -> {
            String shareText = "Ayo nonton film seru ini!\n\n" +
                    "🎥 Judul: " + title + "\n" +
                    "📖 Sinopsis: " + overview;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Rekomendasi Film: " + title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            startActivity(Intent.createChooser(shareIntent, "Bagikan film via..."));
        });
        // ------------------------------------------
    }

    private void fetchMovieDetails(int movieId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        String apiKey = "ee577d1401cb1a62355ac90f7458be06";

        apiService.getMovieDetails(movieId, apiKey).enqueue(new Callback<MovieDetailResponse>() {
            @Override
            public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailResponse detailResponse = response.body();

                    int runtime = detailResponse.getRuntime();
                    if (runtime > 0) {
                        tvDetailRuntime.setText("Durasi: " + runtime + " Menit");
                    } else {
                        tvDetailRuntime.setText("Durasi: Tidak tersedia");
                    }

                    List<Genre> genres = detailResponse.getGenres();
                    if (genres != null && !genres.isEmpty()) {
                        List<String> genreNames = new ArrayList<>();
                        for (Genre genre : genres) {
                            genreNames.add(genre.getName());
                        }
                        String joinedGenres = android.text.TextUtils.join(", ", genreNames);
                        tvDetailGenres.setText("Genre: " + joinedGenres);
                    } else {
                        tvDetailGenres.setText("Genre: Tidak tersedia");
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
                Log.e("DetailActivity", "Error fetching detail: " + t.getMessage());
                tvDetailRuntime.setText("Durasi: Gagal memuat");
                tvDetailGenres.setText("Genre: Gagal memuat");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}