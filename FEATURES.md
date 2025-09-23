# Snap Cart - Mobile Shopping App Features

## 🛍️ App Overview
**Snap Cart** is a modern WebView-based Android application designed for seamless mobile shopping experiences. It wraps your web application with native Android capabilities.

## 📱 Core Features

### ✅ **Full Web Browser Capabilities**
- JavaScript enabled with DOM storage
- HTML5 video/audio support without user gesture requirement
- Geolocation services
- File upload support (camera, gallery, documents)
- Progressive Web App (PWA) support
- Persistent cookie storage for user sessions

### 📷 **Camera & Media**
- Camera access for product photos
- Microphone access for voice features
- File chooser for uploading images/documents
- Media playback controls

### 🔒 **Comprehensive Permissions**
**Core Shopping Features:**
- 📷 Camera & microphone (product photos, video reviews, voice search)
- 📍 Location services (store finder, delivery tracking, location deals)
- 💾 File system access (media uploads, downloads, document sharing)
- 🌐 Network monitoring (connectivity status, data usage optimization)

**Social Media Integration:**
- 👥 Contacts access (friend invitations, referral sharing)
- 📅 Calendar integration (sale reminders, delivery scheduling)
- 📱 SMS & phone access (order confirmations, customer support)
- 👤 Account management (social login, profile sync)

**Enhanced User Experience:**
- 🔔 Notifications (real-time order updates, flash sales, social alerts)
- 🔊 System overlays (chat bubbles, floating cart, quick actions)
- 🔋 Battery optimization (background sync, always-on features)
- ⏰ Exact alarms (limited-time offers, delivery notifications)
- 📡 Bluetooth & NFC (wireless payments, smart device integration)
- 🔐 Biometric security (fingerprint login, secure checkout)

### 🌐 **Network & Connectivity**
- Support for localhost development
- ngrok tunnel compatibility
- Local network IP support (10.1.118.128:3000)
- Mixed content support (HTTP/HTTPS)
- Network security configuration for development

### 📁 **Downloads & Storage**
- Native download manager integration
- External storage access
- Media file access (images, video, audio)
- Persistent data storage

### 🎨 **User Experience**
- Custom "Snap Cart" branding with colorful shopping cart logo
- Professional app icons in all required sizes (MDPI to XXXHDPI)
- Modern adaptive icon support with clean background
- Native Android navigation (back button)
- Full-screen WebView experience
- Proper lifecycle management (pause/resume)
- JavaScript alerts/confirms with native dialogs

## 🛠️ **Development Features**

### **Easy URL Configuration**
```bash
./set-url.sh localhost     # http://localhost:3000
./set-url.sh network       # http://10.1.118.128:3000
./set-url.sh ngrok         # https://324d8d0f97a9.ngrok-free.app
./set-url.sh <custom-url>  # Any custom URL
```

### **Quick Build & Deploy**
```bash
./build-apk.sh            # Build APK
./build-apk.sh clean      # Clean & build
./build-apk.sh install    # Build & install to device
```

### **Automatic Features**
- Cookie persistence across sessions
- Automatic permission handling
- File upload dialog integration
- Download notifications
- Memory management

## 📊 **Technical Specifications**

- **Target SDK**: Android 34 (Android 14)
- **Minimum SDK**: Android 21 (Android 5.0)
- **Package**: com.webview.myapplication
- **APK Size**: ~5.5MB
- **User Agent**: Includes "SnapCart/1.0" identifier

## 🔧 **Configuration Files**

- **MainActivity.java** - Core app logic and WebView setup
- **AndroidManifest.xml** - Permissions and app configuration  
- **network_security_config.xml** - Network security for development
- **strings.xml** - App name and branding
- **build-apk.sh** - Build automation script
- **set-url.sh** - URL configuration script

## 🚀 **Ready for Production**

The app is configured for development but can be easily adapted for production by:
1. Updating the target URL to your production domain
2. Removing development network security exceptions
3. Updating the package name and signing keys
4. Customizing app icons and branding

## 📝 **Current Configuration**
- **App Name**: Snap Cart
- **App Icon**: Custom colorful shopping cart logo
- **Target URL**: http://10.1.118.128:3000
- **Build Output**: app/build/outputs/apk/debug/app-debug.apk
- **APK Size**: 6.4MB
- **Status**: ✅ Ready for testing!

## 🎨 **Branding Assets**
- **Logo Source**: logo.png (500x500)
- **Generated Icons**: All Android densities (48px to 192px)
- **Adaptive Icon**: Clean white background with logo foreground
- **Color Scheme**: Vibrant shopping cart with red, orange, and teal accents
