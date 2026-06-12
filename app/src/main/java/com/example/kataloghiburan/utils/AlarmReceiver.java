package com.example.kataloghiburan.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.kataloghiburan.ui.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "movie_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra("MOVIE_TITLE");
        int movieId = intent.getIntExtra("MOVIE_ID", 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Membuat Notification Channel untuk Android Oreo (8.0) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Nonton Film",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel untuk mengingatkan jadwal menonton film");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Aksi ketika notifikasi diklik (membuka MainActivity)
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                movieId,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Merakit UI Notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Ikon jam bawaan OS
                .setContentTitle("Waktunya Nonton Film! 🎬")
                .setContentText("Yuk buka aplikasi, jadwal nonton film '" + movieTitle + "' sudah tiba.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000}); // Efek getar HP

        if (notificationManager != null) {
            // Memunculkan notifikasi
            notificationManager.notify(movieId, builder.build());
        }
    }
}