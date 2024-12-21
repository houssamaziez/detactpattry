import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_background_service/flutter_background_service.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:web_socket_channel/io.dart';
import 'dart:convert';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize the background service with the correct onStart function signature
  FlutterBackgroundService().configure(
    androidConfiguration: AndroidConfiguration(
      onStart:
          onServiceStarted, // onStart expects a function that takes ServiceInstance as argument
      autoStart: true, isForegroundMode: true,
    ),
    iosConfiguration: IosConfiguration(
      onForeground:
          onServiceStarted, // onForeground also expects the same function signature
    ),
  );

  runApp(const MyApp());
}

void onServiceStarted(ServiceInstance service) async {
  // This function receives ServiceInstance parameter, as required by flutter_background_service
  if (kDebugMode) {
    print("Background service started");
  }

  // Initialize notifications
  FlutterLocalNotificationsPlugin notificationsPlugin =
      FlutterLocalNotificationsPlugin();
  const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
  const initializationSettings =
      InitializationSettings(android: androidSettings);
  await notificationsPlugin.initialize(initializationSettings);

  // Start WebSocket in the background
  connectWebSocket(notificationsPlugin);
}

void onBackground() {
  // Handle background operation here
  print("Service is running in the background");
}

void connectWebSocket(FlutterLocalNotificationsPlugin notificationsPlugin) {
  final channel = IOWebSocketChannel.connect(
      'ws://192.168.2.231:8080/?token=123456'); // Replace with your WebSocket URL

  channel.stream.listen((message) async {
    final decodedMessage =
        message is List<int> ? utf8.decode(message) : message.toString();
    print("Received WebSocket message: $decodedMessage");

    // Show local notification
    const androidDetails = AndroidNotificationDetails(
      'background_websocket_channel',
      'WebSocket Notifications',
      channelDescription: 'Notifications for WebSocket messages',
      importance: Importance.high,
      priority: Priority.high,
    );

    const notificationDetails = NotificationDetails(android: androidDetails);
    await notificationsPlugin.show(
      DateTime.now().millisecondsSinceEpoch ~/ 1000,
      'New WebSocket Message',
      decodedMessage,
      notificationDetails,
    );
  });
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: WebSocketExample(),
    );
  }
}

class WebSocketExample extends StatelessWidget {
  const WebSocketExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('WebSocket Example')),
      body: const Center(
        child: Text('WebSocket Service Running'),
      ),
    );
  }
}
