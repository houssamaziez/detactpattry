package com.example.detactpattry

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.os.Build
import android.util.Log

class WebSocketService : Service() {
    private lateinit var webSocket: WebSocket

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())  // Start service in foreground
        connectWebSocket()
        return START_STICKY // Keeps the service running even after app is closed
    }

    private fun connectWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("ws://10.0.2.2:8080").build()  // Replace with your WebSocket URL
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocketService", "Received: $text")
                // Send a notification
                showNotification("New WebSocket Message", text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocketService", "WebSocket Error: ${t.message}")
                reconnectWebSocket()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocketService", "WebSocket Closed: $reason")
            }
        })
    }

    private fun reconnectWebSocket() {
        // Retry to connect WebSocket if failed
        connectWebSocket()
    }

    private fun createNotification(): Notification {
        val channelId = "foreground_service_channel"
        val channelName = "WebSocket Foreground Service"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return Notification.Builder(this, channelId)
            .setContentTitle("WebSocket Service")
            .setContentText("Service is running...")
            .setSmallIcon(R.mipmap.ic_launcher)  // Set your app icon here
            .build()
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification.Builder(this, "foreground_service_channel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)  // Set your app icon here
            .build()
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Service Destroyed")  // Close WebSocket on service destruction
    }
}
