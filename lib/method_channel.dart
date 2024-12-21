import 'package:flutter/services.dart';

class ForegroundServiceHelper {
  static const platform = MethodChannel('com.example.websocket_service');

  static Future<void> startService() async {
    try {
      await platform.invokeMethod('startWebSocketService');
    } catch (e) {
      print("Failed to start service: $e");
    }
  }

  static Future<void> stopService() async {
    try {
      await platform.invokeMethod('stopWebSocketService');
    } catch (e) {
      print("Failed to stop service: $e");
    }
  }
}
