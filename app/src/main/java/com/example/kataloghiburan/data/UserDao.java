package com.example.kataloghiburan.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.kataloghiburan.model.User;

@Dao
public interface UserDao {

    // Mendaftarkan user baru
    @Insert
    void registerUser(User user);

    // Mengecek login (mencocokkan email dan password)
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User login(String email, String password);

    // Mengecek apakah email sudah terdaftar sebelumnya
    @Query("SELECT * FROM users WHERE email = :email")
    User checkEmailExists(String email);

    // Mengupdate nama user berdasarkan emailnya
    @Query("UPDATE users SET name = :newName WHERE email = :email")
    void updateUserName(String newName, String email);
}