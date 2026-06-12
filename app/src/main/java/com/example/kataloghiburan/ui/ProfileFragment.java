package com.example.kataloghiburan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private AppDatabase database;
    private ExecutorService executorService;
    private TextView tvName; // Dijadikan variabel global agar mudah diupdate

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(getContext());
        database = AppDatabase.getInstance(getContext());
        executorService = Executors.newSingleThreadExecutor();

        tvName = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        MaterialButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        // Menampilkan data saat ini
        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());

        // Aksi Tombol Edit Profil
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Aksi Tombol Logout
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void showEditProfileDialog() {
        if (getContext() == null) return;

        // Membuat layout untuk EditText secara dinamis (lewat kode, tanpa XML)
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Nama Profil");

        final EditText input = new EditText(getContext());
        input.setText(sessionManager.getUserName()); // Isi dengan nama saat ini

        // Memberikan sedikit margin pada EditText agar rapi
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(48, 16, 48, 0);
        input.setLayoutParams(lp);

        LinearLayout container = new LinearLayout(getContext());
        container.addView(input);
        builder.setView(container);

        // Tombol Simpan
        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateProfile(newName);
            } else {
                Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol Batal
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateProfile(String newName) {
        String currentEmail = sessionManager.getUserEmail();

        // Jalankan operasi database di Background Thread
        executorService.execute(() -> {
            // Update SQLite
            database.userDao().updateUserName(newName, currentEmail);

            // Update UI dan Session Manager di Main Thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    sessionManager.createLoginSession(newName, currentEmail); // Timpa sesi lama
                    tvName.setText(newName); // Langsung ubah teks di layar
                    Toast.makeText(getContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                });
            }
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