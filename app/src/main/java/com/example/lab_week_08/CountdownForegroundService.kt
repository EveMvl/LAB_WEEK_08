package com.example.lab_week_08

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class CountdownForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "example_channel"
    }

    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val channelId = intent?.getStringExtra("channel_id") ?: "Unknown"

        createNotificationChannel()

        startForeground(
            1,
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Countdown Started")
                .setContentText("Counting from 10...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )

        job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 10 downTo 0) {
                val notification = NotificationCompat.Builder(this@CountdownForegroundService, CHANNEL_ID)
                    .setContentTitle("Channel ID: $channelId")
                    .setContentText("Countdown: $i")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()

                val manager = getSystemService(NotificationManager::class.java)
                manager.notify(1, notification)
                delay(1000)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@CountdownForegroundService,
                    "Channel id $channelId process is done!",
                    Toast.LENGTH_LONG
                ).show()
            }

            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}
