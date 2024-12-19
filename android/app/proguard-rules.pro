# OkHttp ProGuard rules
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep BouncyCastle and Conscrypt classes required by OkHttp
-keep class org.bouncycastle.** { *; }
-keep class org.conscrypt.** { *; }
-keep class org.openjsse.** { *; }

# Keep SSL classes for the network security configuration
-keep class javax.net.ssl.** { *; }
-keep class java.security.cert.** { *; }

# Keep all OkHttp classes
-dontwarn okhttp3.**
-dontwarn okio.**

# To keep network connection-related classes if you have issues with them being stripped out
-keep class okhttp3.internal.** { *; }
-keep class okhttp3.internal.platform.** { *; }
