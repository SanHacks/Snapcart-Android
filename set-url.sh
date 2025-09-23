#!/bin/bash

# URL Configuration Script for Snap Cart Android App
# Usage: ./set-url.sh <url> [build]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# File to modify
MAIN_ACTIVITY="app/src/main/java/com/webview/myapplication/MainActivity.java"

# Function to update URL in MainActivity
update_url() {
    local new_url="$1"
    
    print_status "Updating URL to: $new_url"
    
    # Escape special characters for sed
    local escaped_url=$(echo "$new_url" | sed 's/[[\.*^$()+?{|]/\\&/g')
    
    # Update the DEVELOPMENT_URL line
    sed -i "s|private static final String DEVELOPMENT_URL = .*|private static final String DEVELOPMENT_URL = \"$new_url\"; // Updated $(date)|" "$MAIN_ACTIVITY"
    
    if grep -q "$new_url" "$MAIN_ACTIVITY"; then
        print_success "URL updated successfully in $MAIN_ACTIVITY"
    else
        print_error "Failed to update URL"
        exit 1
    fi
}

# Function to show current URL
show_current_url() {
    local current_url=$(grep "private static final String DEVELOPMENT_URL" "$MAIN_ACTIVITY" | sed 's/.*= "\([^"]*\)".*/\1/')
    echo "Current URL: $current_url"
}

# Function to show predefined URLs
show_presets() {
    echo ""
    echo "Quick presets:"
    echo "  localhost  -> http://localhost:3000"
    echo "  network    -> http://10.1.118.128:3000"
    echo "  ngrok      -> https://324d8d0f97a9.ngrok-free.app"
    echo ""
}

# Main script
main() {
    echo -e "${BLUE}======================================${NC}"
    echo -e "${BLUE}    Snap Cart URL Configuration${NC}"
    echo -e "${BLUE}======================================${NC}"
    echo ""
    
    if [ $# -eq 0 ]; then
        show_current_url
        show_presets
        echo "Usage: $0 <url|preset> [build]"
        echo "       $0 localhost build"
        echo "       $0 http://192.168.1.100:3000"
        exit 0
    fi
    
    local input_url="$1"
    local should_build="$2"
    
    # Handle presets
    case "$input_url" in
        "localhost"|"local")
            input_url="http://localhost:3000"
            ;;
        "network"|"lan")
            input_url="http://10.1.118.128:3000"
            ;;
        "ngrok")
            input_url="https://324d8d0f97a9.ngrok-free.app"
            ;;
    esac
    
    # Validate URL format
    if [[ ! "$input_url" =~ ^https?:// ]]; then
        print_error "Invalid URL format. Must start with http:// or https://"
        exit 1
    fi
    
    show_current_url
    update_url "$input_url"
    
    echo ""
    show_current_url
    
    # Build if requested
    if [ "$should_build" = "build" ]; then
        echo ""
        print_status "Building APK with new URL..."
        ./build-apk.sh
    else
        echo ""
        print_status "URL updated! Run './build-apk.sh' to build with new URL"
        print_status "Or run './set-url.sh $input_url build' to update and build in one step"
    fi
}

# Run main function
main "$@"
