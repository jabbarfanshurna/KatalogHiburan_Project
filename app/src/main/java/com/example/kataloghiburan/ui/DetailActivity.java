package com.example.kataloghiburan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

    // UI Rating & Review
    private RatingBar ratingBar;
    private EditText etReview;
    private MaterialButton btnSaveReview;

    // UI Tambahan untuk UX Ulasan Dinamis
    private LinearLayout layoutEditReview;
    private LinearLayout layoutSavedReview;
    private TextView tvSavedReviewText;
    private MaterialButton btnEditReview;

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
        MaterialButton btnShare = findViewById(R.id.btnShare);

        tvDetailRuntime = findViewById(R.id.tvDetailRuntime);
        tvDetailGenres = findViewById(R.id.tvDetailGenres);

        // Inisialisasi Komponen Ulasan
        ratingBar = findViewById(R.id.ratingBar);
        etReview = findViewById(R.id.etReview);
        btnSaveReview = findViewById(R.id.btnSaveReview);
        layoutEditReview = findViewById(R.id.layoutEditReview);
        layoutSavedReview = findViewById(R.id.layoutSavedReview);
        tvSavedReviewText = findViewById(R.id.tvSavedReviewText);
        btnEditReview = findViewById(R.id.btnEditReview);

        // Menangkap data Intent
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String overview = getIntent().getStringExtra("EXTRA_OVERVIEW");
        String posterPath = getIntent().getStringExtra("EXTRA_POSTER");
        int movieId = getIntent().getIntExtra("EXTRA_ID", 0);

        tvDetailTitle.setText(title);
        tvDetailOverview.setText(overview);

        String posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
        Glide.with(this).load(posterUrl).into(imgDetailPoster);

        btnBack.setOnClickListener(v -> finish());
        fetchMovieDetails(movieId);

        // Cek database untuk Status Favorit dan Data Ulasan
        executorService.execute(() -> {
            FavoriteMovie existingMovie = database.favoriteMovieDao().getFavoriteById(movieId);
            if (existingMovie != null) {
                isFavorite = true;
                runOnUiThread(() -> {
                    btnFavorite.setText("Hapus dari Favorit");
                    ratingBar.setRating(existingMovie.getUserRating());

                    String savedReview = existingMovie.getUserReview();
                    if (savedReview != null && !savedReview.trim().isEmpty()) {
                        // Jika sudah ada ulasan tersimpan, tampilkan Mode Baca
                        showReadMode(savedReview);
                    }
                });
            }
        });

        // Tombol Favorit
        btnFavorite.setOnClickListener(v -> {
            FavoriteMovie favMovie = new FavoriteMovie();
            favMovie.setId(movieId);
            favMovie.setTitle(title);
            favMovie.setOverview(overview);
            favMovie.setPosterPath(posterPath);
            favMovie.setUserRating(ratingBar.getRating());
            favMovie.setUserReview(etReview.getText().toString().trim());

            executorService.execute(() -> {
                if (!isFavorite) {
                    database.favoriteMovieDao().insertFavorite(favMovie);
                    isFavorite = true;
                    runOnUiThread(() -> {
                        btnFavorite.setText("Hapus dari Favorit");
                        Toast.makeText(DetailActivity.this, "Disimpan ke Favorit!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    database.favoriteMovieDao().deleteFavorite(favMovie);
                    isFavorite = false;
                    runOnUiThread(() -> {
                        btnFavorite.setText("Tambah ke Favorit");
                        ratingBar.setRating(0);
                        etReview.setText("");
                        showEditMode(); // Kembalikan form jika dihapus
                        Toast.makeText(DetailActivity.this, "Dihapus dari Favorit!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // Tombol Share
        btnShare.setOnClickListener(v -> {
            String shareText = "Ayo nonton film seru ini!\n\n🎥 Judul: " + title + "\n📖 Sinopsis: " + overview;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Rekomendasi Film: " + title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Bagikan film via..."));
        });

        // Tombol Simpan Ulasan
        btnSaveReview.setOnClickListener(v -> {
            if (!isFavorite) {
                Toast.makeText(this, "Silakan 'Tambah ke Favorit' dulu!", Toast.LENGTH_LONG).show();
                return;
            }

            float currentRating = ratingBar.getRating();
            String currentReview = etReview.getText().toString().trim();

            if (currentReview.isEmpty()) {
                Toast.makeText(this, "Ulasan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                database.favoriteMovieDao().updateRatingAndReview(movieId, currentRating, currentReview);
                runOnUiThread(() -> {
                    Toast.makeText(DetailActivity.this, "Ulasan tersimpan!", Toast.LENGTH_SHORT).show();
                    showReadMode(currentReview); // Langsung ubah ke Mode Baca
                });
            });
        });

        // Tombol Edit Ulasan
        btnEditReview.setOnClickListener(v -> {
            showEditMode(); // Buka kembali form untuk diedit
        });
    }

    // --- Fungsi Bantuan untuk UX Dinamis ---
    private void showReadMode(String reviewText) {
        layoutEditReview.setVisibility(View.GONE);
        layoutSavedReview.setVisibility(View.VISIBLE);
        tvSavedReviewText.setText("\"" + reviewText + "\"");
        etReview.setText(reviewText); // Siapkan teksnya di background kalau-kalau mau diedit
    }

    private void showEditMode() {
        layoutSavedReview.setVisibility(View.GONE);
        layoutEditReview.setVisibility(View.VISIBLE);
    }
    // ----------------------------------------

    private void fetchMovieDetails(int movieId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String apiKey = "ee577d1401cb1a62355ac90f7458be06";

        apiService.getMovieDetails(movieId, apiKey).enqueue(new Callback<MovieDetailResponse>() {
            @Override
            public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailResponse detailResponse = response.body();
                    int runtime = detailResponse.getRuntime();
                    tvDetailRuntime.setText(runtime > 0 ? "Durasi: " + runtime + " Menit" : "Durasi: Tidak tersedia");

                    List<Genre> genres = detailResponse.getGenres();
                    if (genres != null && !genres.isEmpty()) {
                        List<String> genreNames = new ArrayList<>();
                        for (Genre genre : genres) { genreNames.add(genre.getName()); }
                        tvDetailGenres.setText("Genre: " + android.text.TextUtils.join(", ", genreNames));
                    } else {
                        tvDetailGenres.setText("Genre: Tidak tersedia");
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
                Log.e("DetailActivity", "Error: " + t.getMessage());
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