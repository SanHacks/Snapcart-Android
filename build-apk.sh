#!/bin/bash

# APK Build Script for Snap Cart Android App
# Usage: ./build-apk.sh [clean|install]

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APK_NAME="app-debug.apk"
APK_PATH="app/build/outputs/apk/debug/$APK_NAME"
INSTALL_PATH="/tmp/webview-app.apk"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if JAVA_HOME is set
check_java() {
    if [ -z "$JAVA_HOME" ]; then
        print_warning "JAVA_HOME not set, setting it now..."
        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
        export PATH=$JAVA_HOME/bin:$PATH
        print_status "JAVA_HOME set to: $JAVA_HOME"
    else
        print_status "JAVA_HOME: $JAVA_HOME"
    fi
}

# Function to make gradlew executable
setup_gradle() {
    if [ ! -x "./gradlew" ]; then
        print_status "Making gradlew executable..."
        chmod +x ./gradlew
    fi
}

# Function to clean build
clean_build() {
    print_status "Cleaning previous build..."
    ./gradlew clean
    print_success "Clean completed"
}

# Function to build APK
build_apk() {
    print_status "Building debug APK..."
    ./gradlew assembleDebug
    
    if [ -f "$APK_PATH" ]; then
        print_success "APK built successfully!"
        print_status "APK location: $APK_PATH"
        
        # Show APK info
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
        print_status "APK size: $APK_SIZE"
    else
        print_error "APK build failed - file not found: $APK_PATH"
        exit 1
    fi
}

# Function to install APK via ADB
install_apk() {
    if command -v adb &> /dev/null; then
        print_status "Checking for connected Android devices..."
        DEVICES=$(adb devices | grep -v "List of devices" | grep "device$" | wc -l)
        
        if [ "$DEVICES" -gt 0 ]; then
            print_status "Found $DEVICES connected device(s)"
            print_status "Installing APK..."
            
        # Copy APK to temp location with simpler name
        cp "$APK_PATH" "$INSTALL_PATH"
        
        adb install -r "$INSTALL_PATH"
        if [ $? -eq 0 ]; then
            print_success "Snap Cart APK installed successfully!"
            
            # Clean up temp file
            rm -f "$INSTALL_PATH"
            
            # Try to launch the app
            print_status "Launching Snap Cart..."
            adb shell am start -n com.webview.myapplication/.MainActivity
            else
                print_error "APK installation failed"
                rm -f "$INSTALL_PATH"
                exit 1
            fi
        else
            print_warning "No Android devices connected via ADB"
            print_status "Connect your device and enable USB debugging to auto-install"
        fi
    else
        print_warning "ADB not found - skipping installation"
        print_status "Install Android SDK platform-tools to enable auto-installation"
    fi
}

# Function to show current URL configuration
show_config() {
    print_status "Current configuration:"
    echo "  Target URL: http://10.1.118.128:3000"
    echo "  APK output: $APK_PATH"
    echo "  Network security: Configured for local network + ngrok"
    echo ""
}

# Main script
main() {
    echo -e "${BLUE}======================================${NC}"
    echo -e "${BLUE}    Snap Cart APK Builder${NC}"
    echo -e "${BLUE}======================================${NC}"
    echo ""
    
    show_config
    
    # Check Java environment
    check_java
    
    # Setup Gradle
    setup_gradle
    
    # Parse command line arguments
    case "${1:-build}" in
        "clean")
            clean_build
            build_apk
            ;;
        "install")
            build_apk
            install_apk
            ;;
        "build"|"")
            build_apk
            ;;
        *)
            echo "Usage: $0 [clean|install]"
            echo "  clean   - Clean and build APK"
            echo "  install - Build and install APK to connected device"
            echo "  build   - Just build APK (default)"
            exit 1
            ;;
    esac
    
    echo ""
    print_success "Build script completed!"
    
    if [ "$1" != "install" ]; then
        echo ""
        print_status "To install on device:"
        echo "  1. Copy $APK_PATH to your device"
        echo "  2. Enable 'Install from unknown sources'"
        echo "  3. Install the APK"
        echo ""
        print_status "Or run: ./build-apk.sh install (requires ADB)"
    fi
}

# Run main function
main "$@"
