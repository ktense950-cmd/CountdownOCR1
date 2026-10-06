package com.ktense.countdownocr

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var statusText: TextView

    companion object {
        private const val REQUEST_SCREEN_CAPTURE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 60, 40, 40)
        }

        statusText = TextView(this).apply {
            text = "Countdown OCR\n\n准备开始屏幕捕获测试"
            textSize = 20f
        }

        val startButton = Button(this).apply {
            text = "开始屏幕捕获"
            setOnClickListener {
                requestScreenCapture()
            }
        }

        layout.addView(statusText)
        layout.addView(startButton)

        setContentView(layout)
    }

    private fun requestScreenCapture() {
        val manager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val intent = manager.createScreenCaptureIntent()
        startActivityForResult(intent, REQUEST_SCREEN_CAPTURE)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                statusText.text =
                    "屏幕捕获授权成功\n\n准备启动实时识别……"

                val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                    putExtra("resultCode", resultCode)
                    putExtra("data", data)
                }

                startForegroundService(serviceIntent)
            } else {
                statusText.text =
                    "屏幕捕获授权失败或已取消"
            }
        }
    }
}
