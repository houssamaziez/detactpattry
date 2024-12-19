package com.example.detactpattry

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.detactpattry/service"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startService" -> {
                    startBackgroundService()
                    result.success("Service started")
                }
                "stopService" -> {
                    stopBackgroundService()
                    result.success("Service stopped")
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun startBackgroundService() {
        val serviceIntent = Intent(this, MyBackgroundService::class.java)
        startService(serviceIntent)
    }

    private fun stopBackgroundService() {
        val serviceIntent = Intent(this, MyBackgroundService::class.java)
        stopService(serviceIntent)
    }
}
