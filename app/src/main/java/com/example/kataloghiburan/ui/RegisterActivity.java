package com.example.kataloghiburan.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Penyesuaian ID sesuai dengan XML milik Kennan
        etName = findViewById(R.id.etNameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Tombol kembali ke halaman Login
        tvGoToLogin.setOnClickListener(v -> finish());

        // Tombol Proses Register
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                // Cek apakah email sudah terdaftar
                User existingUser = database.userDao().getUserByEmail(email);

                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show());
                } else {
                    // Masukkan user baru ke database
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword(password);

                    database.userDao().insertUser(newUser);

                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil! Silakan Login.", Toast.LENGTH_LONG).show();
                        finish(); // Tutup halaman register, otomatis kembali ke login
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