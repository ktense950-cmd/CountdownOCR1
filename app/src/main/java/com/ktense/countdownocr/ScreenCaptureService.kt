package com.ktense.countdownocr

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder

class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null

    companion object {
        private const val CHANNEL_ID = "countdown_ocr"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown OCR")
            .setContentText("正在进行屏幕识别测试")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val resultCode = intent?.getIntExtra("resultCode", 0) ?: 0

        val data = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(
                "data",
                Intent::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra("data")
        }

        if (data != null) {
            val manager =
                getSystemService(MEDIA_PROJECTION_SERVICE)
                        as MediaProjectionManager

            mediaProjection =
                manager.getMediaProjection(resultCode, data)

            // 下一阶段在这里加入：
            // ImageReader
            // 屏幕帧
            // OCR
            // P50 / P95 / P99
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Countdown OCR",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        mediaProjection?.stop()
        mediaProjection = null
        super.onDestroy()
    }
}
