package com.example.detactpattry

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationCompat
import okhttp3.*
import java.io.IOException

class MyBackgroundService : Service() {

    private val handler = Handler(Looper.getMainLooper()) // Handler for delayed tasks
    private lateinit var notificationManager: NotificationManager // Notification manager
    private lateinit var websocket: WebSocket // WebSocket object
    private lateinit var okHttpClient: OkHttpClient // OkHttp client for WebSocket

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val CHANNEL_NAME = "My Channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Initialize notificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Initialize OkHttpClient
        okHttpClient = OkHttpClient()

        // Initialize WebSocket connection
        initWebSocket()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Keep service running even if it's killed
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "My Channel for background tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initWebSocket() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080") // WebSocket URL of your server
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                // Send a message to the WebSocket server once connection is established
                val message = "Hello from client! mobile"
                webSocket.send(message) // Send the "Hello from client! mobile" message to server
                sendNotification("Sent message: $message") // Send notification for the sent message
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // Print the data received from the WebSocket
                sendNotification(text) // Trigger notification with the received message
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // Handle error
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                // Handle WebSocket closing
            }
        }

        websocket = okHttpClient.newWebSocket(request, listener)
    }

    private fun sendNotification(message: String) {
        // Create a notification with the received message
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("WebSocket Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Send notification
        notificationManager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close the WebSocket connection when service is destroyed
        websocket.close(1000, "Service stopped")
    }
}
