package com.example.lab_week_08

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class SecondNotificationService : Service() {

    companion object {
        const val CHANNEL_ID_TWO = "second_channel"
    }

    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val channelId = intent?.getStringExtra("channel_id") ?: "Unknown"

        createNotificationChannel()

        startForeground(
            2,
            NotificationCompat.Builder(this, CHANNEL_ID_TWO)
                .setContentTitle("Service 2 Running")
                .setContentText("Countdown Starting...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )

        job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 5 downTo 0) {
                val notification = NotificationCompat.Builder(this@SecondNotificationService, CHANNEL_ID_TWO)
                    .setContentTitle("Second Channel ID: $channelId")
                    .setContentText("Countdown: $i")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()

                val manager = getSystemService(NotificationManager::class.java)
                manager.notify(2, notification)
                delay(900)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@SecondNotificationService,
                    "Second service with Channel id $channelId is done!",
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
                CHANNEL_ID_TWO,
                "Second Notification Service Channel",
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
