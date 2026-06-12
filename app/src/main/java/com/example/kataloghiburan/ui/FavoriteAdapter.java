package com.example.kataloghiburan.ui; // Sesuaikan jika foldernya berbeda

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.FavoriteMovie;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<FavoriteMovie> listFavorites;

    public void setData(List<FavoriteMovie> listFavorites) {
        this.listFavorites = listFavorites;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // PERBAIKAN: Gunakan R.layout.item_favorite
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteMovie movie = listFavorites.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.rbRating.setRating(movie.getUserRating());

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(holder.itemView.getContext())
                .load(posterUrl)
                .into(holder.imgPoster);

        // Jika kartu favorit diklik, buka kembali halaman Detail
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
        return listFavorites == null ? 0 : listFavorites.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle;
        RatingBar rbRating;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgFavPoster);
            tvTitle = itemView.findViewById(R.id.tvFavTitle);
            rbRating = itemView.findViewById(R.id.ratingBar);
        }
    }
}