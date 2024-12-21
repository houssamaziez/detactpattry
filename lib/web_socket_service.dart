import 'package:web_socket_channel/web_socket_channel.dart';

class WebSocketService {
  WebSocketChannel? channel;

  // Function to connect to the WebSocket server
  void connectToWebSocket(String url, Function(String) onMessageReceived) {
    try {
      channel = WebSocketChannel.connect(Uri.parse(url));

      // Listen for incoming messages
      channel!.stream.listen(
        (message) {
          onMessageReceived(message); // Callback function to handle the message
        },
        onDone: () {
          print('WebSocket closed');
        },
        onError: (error) {
          print('Error: $error');
        },
      );
    } catch (e) {
      print('WebSocket connection error: $e');
    }
  }

  // Function to send a message to the WebSocket server
  void sendMessage(String message) {
    if (channel != null) {
      channel!.sink.add(message);
      print('Sent message: $message');
    } else {
      print('WebSocket connection is not established');
    }
  }

  // Function to close the WebSocket connection
  void closeConnection() {
    channel?.sink.close();
  }
}
