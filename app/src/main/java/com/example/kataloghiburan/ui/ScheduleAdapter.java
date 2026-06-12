package com.example.kataloghiburan.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.FavoriteMovie;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<FavoriteMovie> listSchedules;

    public void setData(List<FavoriteMovie> listSchedules) {
        this.listSchedules = listSchedules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        FavoriteMovie movie = listSchedules.get(position);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvDateTime.setText(movie.getWatchDate() + " Pukul " + movie.getWatchTime());
    }

    @Override
    public int getItemCount() {
        return listSchedules == null ? 0 : listSchedules.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvScheduleTitle);
            tvDateTime = itemView.findViewById(R.id.tvScheduleDateTime);
        }
    }
}