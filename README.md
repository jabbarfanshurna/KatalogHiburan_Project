# 🎬 Movie Catalog App (Katalog Hiburan)

Aplikasi katalog film yang dirancang untuk memberikan pengalaman navigasi informasi film yang intuitif, fitur favorit yang terintegrasi, serta pengingat jadwal nonton personal. Tugas Final Lab Mobile 2026[cite: 1].

---

## 📋 Deskripsi Aplikasi
Aplikasi ini memungkinkan pengguna untuk mencari film, memfilter berdasarkan genre, melihat detail film, menandai film sebagai favorit, serta mengatur jadwal nonton dengan notifikasi pengingat[cite: 1]. Aplikasi ini dibangun dengan mengutamakan performa dan pengalaman pengguna yang modern[cite: 1].

## 🚀 Fitur Utama
*   **Dynamic Theme**: Mendukung *Dark Mode* dan *Light Mode* secara otomatis berdasarkan preferensi pengguna[cite: 1].
*   **Live Search & Genre Filter**: Pencarian film *real-time* dan filter genre yang responsif[cite: 1].
*   **Favorite Management**: Simpan film favorit ke penyimpanan lokal menggunakan **Room Database**[cite: 1].
*   **Personalized Review**: Berikan ulasan pribadi pada film yang disimpan di favorit[cite: 1].
*   **Watch Reminder**: Atur jadwal nonton dengan sistem notifikasi *AlarmManager* yang terintegrasi[cite: 1].
*   **Offline Mode**: Data tetap dapat diakses saat tidak ada koneksi internet[cite: 1].
*   **Network Resilience**: Fitur *Refresh* otomatis saat gagal memuat data dari API[cite: 1].

## 🛠 Implementasi Teknis
Proyek ini mengimplementasikan spesifikasi teknis berikut[cite: 1]:
*   **Language**: Java
*   **Architecture**: MVVM Pattern
*   **Networking**: Retrofit (API: The Movie Database)[cite: 1]
*   **Local Data**: Room Persistence Library[cite: 1]
*   **Navigation**: Jetpack Navigation Component[cite: 1]
*   **Background Tasks**: ExecutorService & Handler[cite: 1]
*   **UI/UX**: Material Design 3, Glide (Image Loading), RecyclerView[cite: 1]

## 📱 Struktur Navigasi
1.  **HomeFragment**: Menampilkan daftar film populer dan hasil pencarian.
2.  **FavoriteFragment**: Daftar film yang telah ditandai.
3.  **ProfileFragment**: Pengaturan profil, *Logout*, dan daftar jadwal nonton.
4.  **DetailActivity**: Informasi mendalam film, rating, dan pengaturan jadwal.

---

## ⚙️ Cara Menjalankan
1. *Clone* repository ini ke komputer lokal Anda.
2. Buka proyek menggunakan **Android Studio**.
3. Pastikan *API Key* sudah dikonfigurasi di `ApiService` atau kelas `ApiClient`.
4. *Build* dan jalankan pada *emulator* atau perangkat fisik (min. Android 8.0).

---
*Dibuat oleh Abd Jabbar Fanshurna Musra untuk Tugas Final Lab Mobile 2026[cite: 1].*
