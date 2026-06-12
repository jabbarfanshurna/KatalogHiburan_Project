package com.example.kataloghiburan.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.local.FavoriteMovie;
import com.example.kataloghiburan.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvEmptySchedule;
    private MaterialButton btnEditProfile, btnLogout;
    private RecyclerView rvSchedules;

    private SessionManager sessionManager;
    private AppDatabase database;
    private ExecutorService executorService;
    private ScheduleAdapter scheduleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvSchedules = view.findViewById(R.id.rvSchedules);
        tvEmptySchedule = view.findViewById(R.id.tvEmptySchedule);

        sessionManager = new SessionManager(requireContext());
        database = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        // Setup RecyclerView Jadwal
        scheduleAdapter = new ScheduleAdapter();
        rvSchedules.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSchedules.setAdapter(scheduleAdapter);

        loadProfileData();

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Memuat jadwal setiap kali halaman profil dibuka
        loadScheduleData();
    }

    private void loadProfileData() {
        String savedUsername = sessionManager.getUsername();
        String savedEmail = sessionManager.getEmail();

        tvProfileName.setText(savedUsername != null ? savedUsername : "Kennan");
        tvProfileEmail.setText(savedEmail != null ? savedEmail : "kennan@kataloghiburan.com");
    }

    private void loadScheduleData() {
        executorService.execute(() -> {
            // Mengambil film yang sudah diset jadwalnya dari database
            List<FavoriteMovie> scheduledMovies = database.favoriteMovieDao().getScheduledMovies();

            requireActivity().runOnUiThread(() -> {
                if (scheduledMovies != null && !scheduledMovies.isEmpty()) {
                    scheduleAdapter.setData(scheduledMovies);
                    rvSchedules.setVisibility(View.VISIBLE);
                    tvEmptySchedule.setVisibility(View.GONE);
                } else {
                    rvSchedules.setVisibility(View.GONE);
                    tvEmptySchedule.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText etEditName = dialogView.findViewById(R.id.etEditName);
        EditText etEditEmail = dialogView.findViewById(R.id.etEditEmail);

        etEditName.setText(tvProfileName.getText().toString());
        etEditEmail.setText(tvProfileEmail.getText().toString());

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newName = etEditName.getText().toString().trim();
            String newEmail = etEditEmail.getText().toString().trim();

            if (!newName.isEmpty() && !newEmail.isEmpty()) {
                sessionManager.createLoginSession(newName, newEmail);
                loadProfileData();
                Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}