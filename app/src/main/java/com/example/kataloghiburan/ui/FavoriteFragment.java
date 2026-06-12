package com.example.kataloghiburan.ui; // Sesuaikan package-mu

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.local.FavoriteMovie;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmptyFavorite;
    private FavoriteAdapter adapter;
    private AppDatabase database;
    private ExecutorService executorService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // PERBAIKAN: Gunakan R.layout.fragment_favorite
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvEmptyFavorite = view.findViewById(R.id.tvEmptyFavorite);

        adapter = new FavoriteAdapter();
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(adapter);

        database = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    // Menggunakan onResume agar data selalu refresh ketika kita kembali dari DetailActivity
    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies() {
        executorService.execute(() -> {
            List<FavoriteMovie> favorites = database.favoriteMovieDao().getAllFavorites();

            // Pindah ke Main Thread untuk update UI
            requireActivity().runOnUiThread(() -> {
                if (favorites != null && !favorites.isEmpty()) {
                    adapter.setData(favorites);
                    rvFavorites.setVisibility(View.VISIBLE);
                    tvEmptyFavorite.setVisibility(View.GONE);
                } else {
                    rvFavorites.setVisibility(View.GONE);
                    tvEmptyFavorite.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}