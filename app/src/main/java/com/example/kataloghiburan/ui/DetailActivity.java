package com.example.kataloghiburan.ui;

import android.os.Bundle;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private ExecutorService executorService;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Inisialisasi Database dan Executor (Background Thread)
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        ImageView imgDetailPoster = findViewById(R.id.imgDetailPoster);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailOverview = findViewById(R.id.tvDetailOverview);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnFavorite = findViewById(R.id.btnFavorite);

        // Menangkap data dari Intent
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String overview = getIntent().getStringExtra("EXTRA_OVERVIEW");
        String posterPath = getIntent().getStringExtra("EXTRA_POSTER");
        int movieId = getIntent().getIntExtra("EXTRA_ID", 0);

        // Menampilkan data ke UI
        tvDetailTitle.setText(title);
        tvDetailOverview.setText(overview);

        String posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
        Glide.with(this)
                .load(posterUrl)
                .into(imgDetailPoster);

        // Aksi Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Mengecek apakah film ini sudah ada di database favorit (berjalan di background)
        executorService.execute(() -> {
            FavoriteMovie existingMovie = database.favoriteMovieDao().getFavoriteById(movieId);
            if (existingMovie != null) {
                isFavorite = true;
                // Update UI harus di Main Thread
                runOnUiThread(() -> btnFavorite.setText("Hapus dari Favorit"));
            }
        });

        // Aksi Tombol Favorit (Simpan / Hapus dengan Background Thread)
        btnFavorite.setOnClickListener(v -> {
            FavoriteMovie favMovie = new FavoriteMovie();
            favMovie.setId(movieId);
            favMovie.setTitle(title);
            favMovie.setOverview(overview);
            favMovie.setPosterPath(posterPath);

            executorService.execute(() -> {
                if (!isFavorite) {
                    // Jika belum favorit, maka Simpan
                    database.favoriteMovieDao().insertFavorite(favMovie);
                    isFavorite = true;
                    runOnUiThread(() -> {
                        btnFavorite.setText("Hapus dari Favorit");
                        Toast.makeText(DetailActivity.this, "Berhasil disimpan ke Favorit!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Jika sudah favorit, maka Hapus
                    database.favoriteMovieDao().deleteFavorite(favMovie);
                    isFavorite = false;
                    runOnUiThread(() -> {
                        btnFavorite.setText("Tambah ke Favorit");
                        Toast.makeText(DetailActivity.this, "Dihapus dari Favorit!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Matikan executor untuk mencegah memory leak
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}