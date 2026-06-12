package com.example.kataloghiburan.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etNameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        tvGoToLogin.setOnClickListener(v -> finish()); // Kembali ke halaman Login

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proses register di Background Thread agar aplikasi tidak lag
            executorService.execute(() -> {
                // Cek apakah email sudah terdaftar
                User existingUser = database.userDao().checkEmailExists(email);

                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show());
                } else {
                    // Masukkan data user baru
                    User newUser = new User(name, email, password);
                    database.userDao().registerUser(newUser);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registrasi Berhasil! Silakan Login", Toast.LENGTH_SHORT).show();
                        finish(); // Tutup halaman register
                    });
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}