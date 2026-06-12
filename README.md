🎬 Movie Catalog App (Katalog Hiburan)
Aplikasi katalog film yang dirancang untuk memberikan pengalaman navigasi informasi film yang intuitif, fitur favorit yang terintegrasi, serta pengingat jadwal nonton personal. Tugas Final Lab Mobile 2026.  

📋 Deskripsi Aplikasi
Aplikasi ini memungkinkan pengguna untuk mencari film, memfilter berdasarkan genre, melihat detail film, menandai film sebagai favorit, serta mengatur jadwal nonton dengan notifikasi pengingat. Aplikasi ini dibangun dengan mengutamakan performa dan pengalaman pengguna yang modern.

🚀 Fitur Utama
Dynamic Theme: Mendukung Dark Mode dan Light Mode secara otomatis berdasarkan preferensi pengguna.  

Live Search & Genre Filter: Pencarian film real-time dan filter genre yang responsif.

Favorite Management: Simpan film favorit ke penyimpanan lokal menggunakan Room Database.  

Personalized Review: Berikan ulasan pribadi pada film yang disimpan di favorit.

Watch Reminder: Atur jadwal nonton dengan sistem notifikasi AlarmManager yang terintegrasi.  

Offline Mode: Data tetap dapat diakses saat tidak ada koneksi internet.  

Network Resilience: Fitur Refresh otomatis saat gagal memuat data dari API.  

🛠 Implementasi Teknis
Proyek ini mengimplementasikan spesifikasi teknis berikut:  

Language: Java

Architecture: MVVM Pattern

Networking: Retrofit (API: The Movie Database)  

Local Data: Room Persistence Library  

Navigation: Jetpack Navigation Component  

Background Tasks: ExecutorService & Handler  

UI/UX: Material Design 3, Glide (Image Loading), RecyclerView  

📱 Struktur Navigasi
HomeFragment: Menampilkan daftar film populer dan hasil pencarian.

FavoriteFragment: Daftar film yang telah ditandai.

ProfileFragment: Pengaturan profil, Logout, dan daftar jadwal nonton.

DetailActivity: Informasi mendalam film, rating, dan pengaturan jadwal.

⚙️ Cara Menjalankan
Clone repository ini ke komputer lokal Anda.

Buka proyek menggunakan Android Studio.

Pastikan API Key sudah dikonfigurasi di ApiService atau kelas ApiClient.

Build dan jalankan pada emulator atau perangkat fisik (min. Android 8.0).

Sync Gradle dan pastikan semua dependensi terunduh.

📝 Changelog (Semantic Versioning)
feat: Tambahkan fitur jadwal nonton & notifikasi alarm.

feat: Implementasi Dark/Light mode sistem.

fix: Perbaikan bug pada pemanggilan API saat koneksi terputus.

perf: Optimasi background thread menggunakan ExecutorService.

docs: Update README.md untuk tugas final.

Dibuat oleh Abd Jabbar Fanshurna Musra untuk Tugas Final Lab Mobile 2026.
