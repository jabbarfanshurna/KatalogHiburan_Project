package com.example.kataloghiburan.ui;

import android.content.Context;
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

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle());

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);

        // Tambahkan fungsi onClick di sini
        holder.itemView.setOnClickListener(v -> {
            // Membuat Intent untuk pindah Activity
            android.content.Intent intent = new android.content.Intent(context, DetailActivity.class);
            // Menyisipkan data ke dalam Intent
            intent.putExtra("EXTRA_ID", movie.getId());
            intent.putExtra("EXTRA_TITLE", movie.getTitle());
            intent.putExtra("EXTRA_OVERVIEW", movie.getOverview());
            intent.putExtra("EXTRA_POSTER", movie.getPosterPath());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    // ViewHolder class
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