package com.example.kataloghiburan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kataloghiburan.R;
import com.example.kataloghiburan.local.AppDatabase;
import com.example.kataloghiburan.model.User;
import com.example.kataloghiburan.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    private AppDatabase database;
    private ExecutorService executorService;
    private SessionManager sessionManager; // Gunakan SessionManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Inisialisasi SessionManager
        sessionManager = new SessionManager(this);

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                User user = database.userDao().getUserByEmailAndPassword(email, password);

                runOnUiThread(() -> {
                    if (user != null) {
                        // REFACTOR: Simpan sesi menggunakan SessionManager
                        // Ubah nama method ini jika di file SessionManager-mu berbeda
                        sessionManager.createLoginSession(user.getName(), user.getEmail());

                        Toast.makeText(LoginActivity.this, "Selamat datang, " + user.getName() + "!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
                    }
                });
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