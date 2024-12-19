import 'package:flutter/services.dart';

class BackgroundService {
  static const _channel = MethodChannel('com.example.detactpattry/service');

  static Future<void> startService() async {
    try {
      await _channel.invokeMethod('startService');
    } catch (e) {
      print("Failed to start service: $e");
    }
  }

  static Future<void> stopService() async {
    try {
      await _channel.invokeMethod('stopService');
    } catch (e) {
      print("Failed to stop service: $e");
    }
  }
}
