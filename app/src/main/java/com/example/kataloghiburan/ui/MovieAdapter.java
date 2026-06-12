package com.example.kataloghiburan.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kataloghiburan.R;
import com.example.kataloghiburan.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<>();

    // Konstruktor kosong agar 'new MovieAdapter()' di HomeFragment tidak eror
    public MovieAdapter() {
    }

    // Metode untuk memperbarui data dari HomeFragment
    public void setData(List<Movie> items) {
        this.movieList.clear();
        if (items != null) {
            this.movieList.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle());

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(holder.itemView.getContext())
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);

        // Aksi klik langsung di dalam Adapter
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("EXTRA_ID", movie.getId());
            intent.putExtra("EXTRA_TITLE", movie.getTitle());
            intent.putExtra("EXTRA_OVERVIEW", movie.getOverview());
            intent.putExtra("EXTRA_POSTER", movie.getPosterPath());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}