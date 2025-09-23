# Snap Cart - Mobile Shopping App
 (Build As A Wrapper for https://github.com/SanHacks/smartgrocerycompare (BE - Laravel PHP & FE : REACT JS : Vite)
A modern Android WebView application that provides a native mobile experience for web-based shopping platforms.

![Snap Cart Logo](logo.png)

##  Features

- **Full Web Browser Experience** - JavaScript, DOM storage, and PWA support
- **Camera & Media Access** - Product photos and voice features
- **Location Services** - Location-based shopping and delivery
- **File Upload Support** - Camera, gallery, and document uploads
- **Persistent Sessions** - Cookie storage for user login sessions
- **Download Manager** - Native Android downloads with notifications
- **Modern UI** - Full-screen WebView with native navigation

## 🛠️ Development

### Quick Start

```bash
# Build APK
./build-apk.sh

# Build and install to device
./build-apk.sh install

# Clean build
./build-apk.sh clean
```

### URL Configuration

```bash
# Switch to localhost
./set-url.sh localhost

# Switch to network IP
./set-url.sh network

# Switch to ngrok
./set-url.sh ngrok

# Use custom URL
./set-url.sh https://your-domain.com
```

##  Current Configuration

- **Target URL**: http://10.1.118.128:3000
- **Package**: com.webview.myapplication
- **Min SDK**: Android 5.0 (API 21)
- **Target SDK**: Android 14 (API 34)

##  Requirements

- Android Studio or command line tools
- JDK 17+
- Android SDK
- ImageMagick (for icon generation)

## 🔧 Build System

- **Gradle**: 8.5
- **Android Gradle Plugin**: 8.1.4
- **Build Tools**: 33.0.1

##  Permissions

The app requests the following permissions for full shopping experience:

- **Camera** - Product photos and QR scanning
- **Microphone** - Voice features and audio
- **Location** - Location-based services
- **Storage** - File uploads and downloads
- **Network** - Internet connectivity

##  Branding

- **App Name**: Snap Cart
- **Custom Logo**: Colorful shopping cart design
- **Icon Sizes**: All Android densities (MDPI to XXXHDPI)
- **Adaptive Icons**: Modern Android support

##  Project Structure

```
Snapcart-Android/
├── app/
│   ├── src/main/
│   │   ├── java/com/webview/myapplication/
│   │   │   └── MainActivity.java
│   │   ├── res/
│   │   │   ├── mipmap-*/          # App icons
│   │   │   ├── values/strings.xml  # App name
│   │   │   └── xml/network_security_config.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build-apk.sh                   # Build script
├── set-url.sh                     # URL configuration
├── logo.png                       # Source logo
└── FEATURES.md                    # Detailed features
```

##  Production Setup

1. Update target URL to production domain
2. Remove development network security exceptions
3. Update package name and app signing
4. Customize branding and icons
5. Test on multiple devices

##  License

This project is open source. See individual dependencies for their licenses.

##  Developer

**Gundo Sifhufhi**
- Email: sifhufhisg@gmail.com
- GitHub: [@SanHacks](https://github.com/SanHacks)

---

Built with ❤️ for modern mobile shopping experiences.
