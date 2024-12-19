package com.example.detactpattry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class MyBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("MyBootReceiver", "Device booted successfully.")

            // Start the service after the device has rebooted
            val serviceIntent = Intent(context, MyBackgroundService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent) // Start service as a foreground service
            } else {
                context.startService(serviceIntent) // Start service normally
            }
        }
    }
}
