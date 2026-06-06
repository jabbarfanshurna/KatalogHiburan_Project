package com.example.kataloghiburan.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.local.FavoriteMovie;
import com.example.kataloghiburan.model.Movie;
import com.example.kataloghiburan.ui.MovieAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavoriteMovies;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    private AppDatabase database;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // 1. Inisialisasi RecyclerView
        rvFavoriteMovies = view.findViewById(R.id.rvFavoriteMovies);
        rvFavoriteMovies.setLayoutManager(new LinearLayoutManager(getContext()));

        // Memakai MovieAdapter yang sama dengan HomeFragment
        adapter = new MovieAdapter(getContext(), movieList);
        rvFavoriteMovies.setAdapter(adapter);

        // 2. Inisialisasi Database dan Background Thread
        database = AppDatabase.getInstance(getContext());
        executorService = Executors.newSingleThreadExecutor();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 3. Panggil fungsi ambil data setiap kali halaman dibuka
        // Tujuannya agar list langsung ter-update jika ada film baru yang difavoritkan
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies() {
        // Menjalankan operasi database di latar belakang
        executorService.execute(() -> {
            // Tarik data dari Room SQLite
            List<FavoriteMovie> favorites = database.favoriteMovieDao().getAllFavorites();

            // Konversi List<FavoriteMovie> menjadi List<Movie>
            List<Movie> mappedMovies = new ArrayList<>();
            for (FavoriteMovie fav : favorites) {
                Movie movie = new Movie();
                movie.setId(fav.getId());
                movie.setTitle(fav.getTitle());
                movie.setOverview(fav.getOverview());
                movie.setPosterPath(fav.getPosterPath());
                mappedMovies.add(movie);
            }

            // Kembalikan ke Main Thread untuk mengupdate antarmuka pengguna
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    movieList.clear();
                    movieList.addAll(mappedMovies);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Matikan executor saat fragment dihancurkan agar memori tidak bocor
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}