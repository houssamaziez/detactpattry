package com.example.detactpattry

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import com.example.detactpattry.WebSocketService

// تأكد من إضافة جميع الاستيرادات اللازمة
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.websocket_service"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        
        // إعداد قناة MethodChannel للتواصل مع Flutter
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "startWebSocketService" -> {
                        // بدء خدمة WebSocket في المقدمة
                        val intent = Intent(this, WebSocketService::class.java)
                        startForegroundService(intent)
                        result.success(null)
                    }
                    "stopWebSocketService" -> {
                        // إيقاف خدمة WebSocket
                        val intent = Intent(this, WebSocketService::class.java)
                        stopService(intent)
                        result.success(null)
                    }
                    else -> result.notImplemented()
                }
            }
    }
}
