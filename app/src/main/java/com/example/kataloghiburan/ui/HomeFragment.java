package com.example.kataloghiburan.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.model.Movie;
import com.example.kataloghiburan.model.MovieResponse;
import com.example.kataloghiburan.network.ApiClient;
import com.example.kataloghiburan.network.ApiService;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private Button btnRefresh;
    private SearchView searchView;
    private ChipGroup chipGroupGenres;

    private ApiService apiService;
    private static final String API_KEY = "ee577d1401cb1a62355ac90f7458be06";

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMovies = view.findViewById(R.id.rvMovies);
        progressBar = view.findViewById(R.id.progressBar);
        layoutError = view.findViewById(R.id.layoutError);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        searchView = view.findViewById(R.id.searchView);
        chipGroupGenres = view.findViewById(R.id.chipGroupGenres);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Inisialisasi RecyclerView dan Adapter baru yang sudah bersih
        rvMovies.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        movieAdapter = new MovieAdapter();
        rvMovies.setAdapter(movieAdapter);

        btnRefresh.setOnClickListener(v -> fetchPopularMovies());

        setupLiveSearch();
        setupGenreFilter();

        // Muat film populer pertama kali
        fetchPopularMovies();
    }

    private void setupLiveSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHandler.removeCallbacks(searchRunnable);
                if (!query.trim().isEmpty()) {
                    searchMovies(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    if (newText.trim().isEmpty()) {
                        checkCurrentGenreAndLoad();
                    } else {
                        chipGroupGenres.check(R.id.chipAll);
                        searchMovies(newText);
                    }
                };
                // Jeda 500ms agar API tidak spam
                searchHandler.postDelayed(searchRunnable, 500);
                return true;
            }
        });
    }

    private void setupGenreFilter() {
        chipGroupGenres.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                group.check(R.id.chipAll);
                return;
            }

            int checkedId = checkedIds.get(0);

            searchView.setQuery("", false);
            searchView.clearFocus();

            if (checkedId == R.id.chipAll) {
                fetchPopularMovies();
            } else if (checkedId == R.id.chipAction) {
                fetchMoviesByGenre(28);
            } else if (checkedId == R.id.chipHorror) {
                fetchMoviesByGenre(27);
            } else if (checkedId == R.id.chipComedy) {
                fetchMoviesByGenre(35);
            } else if (checkedId == R.id.chipAnimation) {
                fetchMoviesByGenre(16);
            }
        });
    }

    private void checkCurrentGenreAndLoad() {
        int checkedId = chipGroupGenres.getCheckedChipId();
        if (checkedId == R.id.chipAction) fetchMoviesByGenre(28);
        else if (checkedId == R.id.chipHorror) fetchMoviesByGenre(27);
        else if (checkedId == R.id.chipComedy) fetchMoviesByGenre(35);
        else if (checkedId == R.id.chipAnimation) fetchMoviesByGenre(16);
        else fetchPopularMovies();
    }

    private void fetchPopularMovies() {
        showLoading(true);
        apiService.getPopularMovies(API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    movieAdapter.setData(response.body().getResults());
                    rvMovies.setVisibility(View.VISIBLE);
                    layoutError.setVisibility(View.GONE);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showLoading(false);
                showError();
                Log.e("HomeFragment", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchMoviesByGenre(int genreId) {
        showLoading(true);
        apiService.getMoviesByGenre(API_KEY, genreId).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    movieAdapter.setData(response.body().getResults());
                    rvMovies.setVisibility(View.VISIBLE);
                    layoutError.setVisibility(View.GONE);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showLoading(false);
                showError();
                Log.e("HomeFragment", "Error: " + t.getMessage());
            }
        });
    }

    private void searchMovies(String query) {
        showLoading(true);
        apiService.searchMovies(API_KEY, query).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> results = response.body().getResults();
                    movieAdapter.setData(results);
                    rvMovies.setVisibility(View.VISIBLE);
                    layoutError.setVisibility(View.GONE);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showLoading(false);
                showError();
                Log.e("HomeFragment", "Error: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) rvMovies.setVisibility(View.GONE);
    }

    private void showError() {
        rvMovies.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
    }
}