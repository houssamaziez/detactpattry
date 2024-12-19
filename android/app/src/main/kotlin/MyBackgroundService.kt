// package com.example.detactpattry

// import android.app.Service
// import android.content.Intent
// import android.os.IBinder
// import android.os.Handler
// import android.os.Looper
// import android.util.Log
// import androidx.core.app.NotificationCompat
// import android.app.NotificationManager
// import android.app.NotificationChannel
// import android.content.Context
// import java.text.SimpleDateFormat
// import java.util.*

// class MyBackgroundService : Service() {

//     private val handler = Handler(Looper.getMainLooper()) // Handler for delayed tasks
//     private lateinit var notificationManager: NotificationManager // Notification manager
//     private var startTime: Long = 0 // Time when service started

//     companion object {
//         const val CHANNEL_ID = "my_channel_id" // Channel ID for notifications
//         const val CHANNEL_NAME = "My Channel"  // Channel name
//     }

//     override fun onCreate() {
//         super.onCreate()
//         createNotificationChannel()
        
//         // Initialize notificationManager
//         notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//         // Save the start time
//         startTime = System.currentTimeMillis()
//     }

//     override fun onBind(intent: Intent?): IBinder? {
//         return null
//     }

//     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//         Log.d("MyBackgroundService", "Service is running...")

//         // Start updating the notification every second
//         handler.postDelayed(object : Runnable {
//             override fun run() {
//                 // Update notification every second
//                 updateNotification()
//                 handler.postDelayed(this, 1000) // Update every second
//             }
//         }, 1000)

//         // Ensure the service stays alive even if it gets killed
//         return START_STICKY
//     }

//     private fun updateNotification() {
//         val currentTime = System.currentTimeMillis()
//         val elapsedTime = currentTime - startTime // Calculate the elapsed time in milliseconds
        
//         // Format the elapsed time into a readable format (HH:mm:ss)
//         val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//         val formattedTime = timeFormat.format(Date(elapsedTime))

//         // Send the updated notification with the elapsed time
//         val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//             .setSmallIcon(android.R.drawable.ic_notification_overlay)
//             .setContentTitle("Background Service")
//             .setContentText("Service running for: $formattedTime")
//             .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//             .build()

//         // Make the service a foreground service by calling startForeground()
//         startForeground(1, notification) // This will keep the service running
//     }

//     private fun createNotificationChannel() {
//         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//             val name = CHANNEL_NAME
//             val descriptionText = "My Channel for background tasks"
//             val importance = NotificationManager.IMPORTANCE_DEFAULT
//             val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                 description = descriptionText
//             }
//             // Register the channel with the system
//             val notificationManager: NotificationManager =
//                 getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//             notificationManager.createNotificationChannel(channel)
//         }
//     }

//     override fun onDestroy() {
//         super.onDestroy()
//         Log.d("MyBackgroundService", "Service stopped.")
//         handler.removeCallbacksAndMessages(null) // Stop updating the notification when service is destroyed
//     }
// }
package com.example.detactpattry

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import okhttp3.*
import java.text.SimpleDateFormat
import java.util.*
import java.io.IOException

class MyBackgroundService : Service() {

    private val handler = Handler(Looper.getMainLooper()) // Handler for delayed tasks
    private lateinit var notificationManager: NotificationManager // Notification manager
    private var startTime: Long = 0 // Time when service started
    private lateinit var websocket: WebSocket // WebSocket object
    private lateinit var okHttpClient: OkHttpClient // OkHttp client for WebSocket

    companion object {
        const val CHANNEL_ID = "my_channel_id" // Channel ID for notifications
        const val CHANNEL_NAME = "My Channel"  // Channel name
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Initialize notificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Save the start time
        startTime = System.currentTimeMillis()

        // Initialize OkHttpClient
        okHttpClient = OkHttpClient()

        // Initialize WebSocket connection
        initWebSocket()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyBackgroundService", "Service is running...")

        // Start updating the notification every second
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Update notification every second
                updateNotification()
                handler.postDelayed(this, 1000) // Update every second
            }
        }, 1000)

        // Ensure the service stays alive even if it gets killed
        return START_STICKY
    }

    private fun updateNotification() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime // Calculate the elapsed time in milliseconds
        
        // Format the elapsed time into a readable format (HH:mm:ss)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedTime = timeFormat.format(Date(elapsedTime))

        // Send the updated notification with the elapsed time
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("Background Service")
            .setContentText("Service running for: $formattedTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Make the service a foreground service by calling startForeground()
        startForeground(1, notification) // This will keep the service running
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "My Channel for background tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initWebSocket() {
        val request = Request.Builder()
            .url("ws://your-websocket-url") // Replace with your WebSocket URL
            .build()

        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // Print the data received from the WebSocket
                Log.d("WebSocket", "Received message: $text")
                // You can also perform additional operations with the received data
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e("WebSocket", "WebSocket error: ${t.message}")
            }
        }

        websocket = okHttpClient.newWebSocket(request, listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyBackgroundService", "Service stopped.")
        handler.removeCallbacksAndMessages(null) // Stop updating the notification when service is destroyed
        websocket.close(1000, "Service stopped") // Close the WebSocket connection
    }
}
