import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Background Service Demo',
      home: BackgroundServiceScreen(),
    );
  }
}

class BackgroundServiceScreen extends StatefulWidget {
  @override
  _BackgroundServiceScreenState createState() =>
      _BackgroundServiceScreenState();
}

class _BackgroundServiceScreenState extends State<BackgroundServiceScreen> {
  static const _channel = MethodChannel('com.example.detactpattry/service');

  String _serviceStatus = "Service not started";

  Future<void> _startService() async {
    try {
      await _channel.invokeMethod('startService');
      setState(() {
        _serviceStatus = "Service started";
      });
    } catch (e) {
      setState(() {
        _serviceStatus = "Failed to start service: $e";
      });
    }
  }

  Future<void> _stopService() async {
    try {
      await _channel.invokeMethod('stopService');
      setState(() {
        _serviceStatus = "Service stopped";
      });
    } catch (e) {
      setState(() {
        _serviceStatus = "Failed to stop service: $e";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Background Service'),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              _serviceStatus,
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 18),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _startService,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child:
                  const Text('Start Service', style: TextStyle(fontSize: 16)),
            ),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: _stopService,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text('Stop Service', style: TextStyle(fontSize: 16)),
            ),
          ],
        ),
      ),
    );
  }
}
