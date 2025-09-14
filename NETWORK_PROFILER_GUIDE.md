# OkHttp Profiler Integration Guide

This guide explains how to use the OkHttp Profiler to monitor your Reddit API calls and other network requests in your SubStream app.

## 🚀 **What's Implemented**

✅ **OkHttp Profiler Integration**: Added `com.localebro:okhttpprofiler:1.0.8`  
✅ **Ktor + OkHttp Engine**: Configured Ktor to use OkHttp engine for profiling  
✅ **Debug-Only Profiler**: Profiler only works in debug builds (not in release)  
✅ **UI Integration**: Added "Open Network Profiler" button in MainActivity  
✅ **Automatic Profiling**: All Reddit API calls are automatically profiled  

## 📱 **How to Use**

### 1. **Run Debug Build**
The profiler only works in debug builds, so make sure you're running:
```bash
./gradlew assembleDebug
```

### 2. **Open the Profiler**
- Launch your app on a device/emulator
- Tap the **"Open Network Profiler"** button in the app
- The profiler will open in a new activity

### 3. **Make API Calls**
- Use the "Refresh Token" or "Get Valid Token" buttons
- Or make any Reddit API calls through your app
- All network requests will appear in the profiler

### 4. **View Network Details**
The profiler shows:
- **Request URL**: Full endpoint being called
- **Request Headers**: Including Authorization Bearer tokens
- **Request Body**: POST data (like refresh token requests)
- **Response Status**: HTTP status codes
- **Response Headers**: Server response headers
- **Response Body**: Full JSON responses
- **Timing**: Request duration and timing details

## 🔍 **What You'll See**

### **Reddit Token Refresh Calls**
When you refresh tokens, you'll see:
```
POST https://www.reddit.com/api/v1/access_token
Headers:
  User-Agent: SubStream/1.0 by YourUsername
  Content-Type: application/x-www-form-urlencoded
Body:
  grant_type=refresh_token&refresh_token=YOUR_REFRESH_TOKEN
Response:
  200 OK
  {
    "access_token": "new_token_here",
    "expires_in": 3600,
    "token_type": "bearer",
    "scope": "read"
  }
```

### **Reddit API Calls**
When you make Reddit API calls, you'll see:
```
GET https://oauth.reddit.com/r/android/hot?limit=25
Headers:
  User-Agent: SubStream/1.0 by YourUsername
  Authorization: Bearer YOUR_ACCESS_TOKEN
Response:
  200 OK
  {
    "kind": "Listing",
    "data": { ... }
  }
```

## 🛠️ **Technical Details**

### **Dependencies Added**
```kotlin
// In build.gradle.kts
debugImplementation("com.localebro:okhttpprofiler:1.0.8")
implementation("io.ktor:ktor-client-okhttp:2.3.12")
```

### **Configuration**
The profiler is automatically configured in `NetworkModule.kt`:
- Uses reflection to add profiler only in debug builds
- Gracefully handles missing profiler in release builds
- Integrates with Ktor's OkHttp engine

### **Permissions Added**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🐛 **Troubleshooting**

### **Profiler Not Opening**
- Make sure you're running a **debug build**
- Check that the device has internet connectivity
- Verify the profiler dependency is included

### **No Network Requests Showing**
- Ensure you're making actual API calls
- Check that the Reddit API is being called
- Verify the OkHttp engine is being used

### **Release Build Issues**
- The profiler is intentionally disabled in release builds
- This is normal behavior for security reasons

## 📊 **Benefits**

1. **Debug API Issues**: See exactly what's being sent and received
2. **Monitor Token Refresh**: Verify token refresh is working correctly
3. **Performance Analysis**: Check request/response times
4. **Security Verification**: Ensure tokens are being sent properly
5. **Reddit API Debugging**: Debug Reddit API integration issues

## 🔒 **Security Notes**

- Profiler only works in debug builds
- Never ship debug builds to production
- The profiler shows sensitive data (tokens, API keys)
- Use only for development and testing

## 🎯 **Next Steps**

1. **Run the app** in debug mode
2. **Open the profiler** using the button
3. **Make API calls** to see them in the profiler
4. **Debug any issues** using the detailed request/response data

The profiler will help you monitor and debug your Reddit API integration, ensuring everything works correctly!
