package com.example.kataloghiburan.data; // Sesuaikan dengan package-mu

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.kataloghiburan.model.User;

@Dao
public interface UserDao {

    // Mendaftarkan pengguna baru
    @Insert
    void insertUser(User user);

    // Untuk Login: Mencocokkan email dan password
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User getUserByEmailAndPassword(String email, String password);

    // Untuk Register: Mengecek apakah email sudah pernah didaftarkan
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
}