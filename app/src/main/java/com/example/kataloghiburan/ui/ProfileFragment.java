package com.example.kataloghiburan.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.kataloghiburan.R;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail;
    private MaterialButton btnEditProfile, btnLogout;
    private SharedPreferences sharedPreferences;

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

        // Inisialisasi SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LoginSession", Context.MODE_PRIVATE);

        // Memuat data saat fragment dibuka (Gunakan data default jika belum ada)
        loadProfileData();

        // Aksi Tombol Edit Profil
        btnEditProfile.setOnClickListener(v -> {
            showEditProfileDialog();
        });

        // Aksi Tombol Logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // Fungsi untuk memuat data dari memori dan menampilkannya di layar
    private void loadProfileData() {
        String savedUsername = sharedPreferences.getString("USERNAME", "Pengguna Baru");
        String savedEmail = sharedPreferences.getString("EMAIL", "email@kataloghiburan.com");

        tvProfileName.setText(savedUsername);
        tvProfileEmail.setText(savedEmail);
    }

    // Fungsi untuk menampilkan Custom Dialog Form Edit Profil
    private void showEditProfileDialog() {
        // Membuat pembuat dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Memasukkan layout XML yang baru kita buat ke dalam dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText etEditName = dialogView.findViewById(R.id.etEditName);
        EditText etEditEmail = dialogView.findViewById(R.id.etEditEmail);

        // Menampilkan nama dan email saat ini di dalam form sebelum diedit
        etEditName.setText(tvProfileName.getText().toString());
        etEditEmail.setText(tvProfileEmail.getText().toString());

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newName = etEditName.getText().toString().trim();
            String newEmail = etEditEmail.getText().toString().trim();

            if (!newName.isEmpty() && !newEmail.isEmpty()) {
                // Menyimpan data baru ke dalam SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USERNAME", newName);
                editor.putString("EMAIL", newEmail);
                editor.apply();

                // Memperbarui teks di halaman profil secara langsung
                loadProfileData();

                Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> {
            dialog.dismiss(); // Menutup dialog jika batal
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}