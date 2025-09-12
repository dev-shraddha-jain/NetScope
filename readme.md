API Inspector System
A complete API debugging solution consisting of an Android library, companion app, and web interface for real-time HTTP request/response monitoring.

Components
Android Library - Lightweight interceptor that captures network traffic

Companion Android App - Native app to view requests in real-time

Web App - Browser-based interface for cross-platform access

Features

✅ Minimal integration (1 interceptor + 1 init call)

✅ Real-time updates via Server-Sent Events (SSE)

✅ Debug builds only (automatically disabled in release)

✅ In-memory storage with automatic cleanup

✅ Search and filter capabilities

✅ Detailed request/response inspection

✅ Cross-platform web interface

✅ No native dependencies

Quick Start

1. Add the Library to Your Android Project

Step 1: Add dependencies to your app's build.gradle:


`dependencies {
debugImplementation project(':apiinspector')
// OR if published to a repository:
// debugImplementation 'com.apiinspector:library:1.0.0'

    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'org.nanohttpd:nanohttpd:2.3.1'
    implementation 'com.google.code.gson:gson:2.10.1'
}`

Step 2: Initialize in your Application class:

`class MyApplication : Application() {
override fun onCreate() {
super.onCreate()

        // Initialize API Inspector (debug builds only)
        val serverUrl = ApiInspector.init(this, port = 8081)
        if (serverUrl != null) {
            Log.d("APIInspector", "Server running at: $serverUrl")
        }
    }
}`

Step 3: Add interceptor to your OkHttp client:

kotlin
val client = OkHttpClient.Builder()
.addInterceptor(ApiInspector.getInterceptor())
// ... other interceptors
.build()
That's it! The library will now capture all HTTP requests made through this OkHttp client.

2. View Captured Requests

Option A: Use the Companion Android App

Install the companion app APK on the same device or another device on the same network

Enter the server URL (displayed in logs, e.g., http://192.168.1.100:8081)

Tap "Connect" to start viewing requests in real-time

Option B: Use the Web Interface

Save the index.html file and open it in any browser

Enter the server URL in the input field

Click "Connect" to start monitoring

Option C: Direct API Access

Access the raw API endpoints:


GET /requests - Get all captured requests as JSON

GET /request/{id} - Get specific request details

GET /events - Server-Sent Events stream for real-time updates

POST /clear - Clear all captured requests

Library Architecture
Core Components
NetworkInterceptor - OkHttp interceptor that captures requests/responses
RequestStore - In-memory storage with automatic cleanup (max 100 requests)
DebugServer - Lightweight HTTP server using NanoHTTPD
ApiInspector - Main API for initialization and control
Security Features
Debug builds only: Server automatically disabled in release builds
Optional access token: Add accessToken parameter to init() for basic authentication
Local network only: Server binds to local IP address
Example with Access Token:
kotlin
ApiInspector.init(this, port = 8081, accessToken = "my-secret-token")
Then access with: http://device-ip:8081/requests?token=my-secret-token

Project Structure
api-inspector/
├── library/
│   ├── src/main/java/com/apiinspector/library/
│   │   ├── NetworkInterceptor.kt
│   │   ├── RequestStore.kt
│   │   ├── DebugServer.kt
│   │   ├── SSEClient.kt
│   │   ├── CapturedRequest.kt
│   │   └── ApiInspector.kt
│   └── build.gradle
├── companion-app/
│   ├── src/main/java/com/apiinspector/companion/
│   │   ├── MainActivity.kt
│   │   ├── RequestAdapter.kt
│   │   ├── RequestDetailsDialog.kt
│   │   ├── EventSourceFactory.kt
│   │   └── CapturedRequest.kt
│   ├── src/main/res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── item_request.xml
│   │   │   └── dialog_request_details.xml
│   │   └── drawable/
│   │       └── method_background.xml
│   ├── AndroidManifest.xml
│   └── build.gradle
└── web-app/
└── index.html
API Endpoints
GET /requests
Returns all captured requests as JSON array.

Response:

json
[
{
"id": "uuid-string",
"method": "GET",
"url": "https://api.example.com/users",
"requestHeaders": {"Accept": ["application/json"]},
"requestBody": "",
"responseHeaders": {"Content-Type": ["application/json"]},
"statusCode": 200,
"statusMessage": "OK",
"responseBody": "[{\"id\":1,\"name\":\"John\"}]",
"duration": 245,
"timestamp": 1699123456789
}
]
GET /request/{id}
Returns detailed information for a specific request.

GET /events
Server-Sent Events stream with real-time updates.

Events:

connected - Connection established
request - New request captured
clear - All requests cleared
POST /clear
Clears all captured requests from memory.

Customization
Modify Request Body Preview Length
kotlin
// In NetworkInterceptor.kt, change the truncation limit:
if (bodyString.length > 2000) { // Default is 1000
bodyString.substring(0, 2000) + "... (truncated)"
}
Change Maximum Stored Requests
kotlin
// In RequestStore.kt:
private const val MAX_REQUESTS = 200 // Default is 100
Custom Server Port
kotlin
ApiInspector.init(this, port = 9090) // Default is 8081
Troubleshooting
Server Not Starting
Check if port is already in use
Ensure debug build variant is selected
Verify network permissions in AndroidManifest.xml
Cannot Connect from Companion App/Web
Ensure both devices are on the same network
Check firewall settings
Verify the IP address is correct (check device's WiFi settings)
Try using adb port-forward: adb forward tcp:8081 tcp:8081
No Requests Appearing
Verify the interceptor is added to OkHttp client
Check that requests are actually being made
Ensure the app is using the instrumented OkHttp client
Performance Considerations
The library automatically limits stored requests to prevent memory issues
Request/response bodies are truncated to 1KB by default
Server stops automatically when app is killed
Building from Source
Library Module
bash
cd library/
./gradlew assembleDebug
Companion App
bash
cd companion-app/
./gradlew assembleDebug
./gradlew installDebug  # Install on connected device
Web App
The web app is a single HTML file - just open index.html in any modern browser.

License
This project is provided as-is for debugging and development purposes. Use responsibly and ensure it's disabled in production builds.

Contributing
Feel free to submit issues and enhancement requests. This is designed as a development tool, so security and production-readiness are not primary concerns.

