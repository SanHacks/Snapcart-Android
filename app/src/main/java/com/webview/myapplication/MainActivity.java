package com.webview.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    // Permission request codes
    private static final int STORAGE_PERMISSION_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;
    private static final int LOCATION_PERMISSION_CODE = 3;
    private static final int AUDIO_PERMISSION_CODE = 4;
    private static final int MULTIPLE_PERMISSIONS_CODE = 100;
    
    private WebView mWebView;
    private ValueCallback<Uri[]> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    
    // Development URLs - easily change these for different testing environments
    private static final String LOCALHOST_URL = "http://localhost:3000";
    private static final String NETWORK_URL = "http://10.1.118.128:3000";
    private static final String NGROK_URL = "https://324d8d0f97a9.ngrok-free.app";
    private static final String DEVELOPMENT_URL = NETWORK_URL; // Change this to switch between environments

    // Request all necessary permissions for social media shopping app
    private void requestAllPermissions() {
        String[] permissions = {
            // Core permissions
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            
            // Social media permissions
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            
            // Calendar permissions
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            
            // SMS and phone permissions
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            
            // System permissions
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        };
        
        // Add Android version-specific permissions
        java.util.List<String> permissionList = new java.util.ArrayList<>(java.util.Arrays.asList(permissions));
        
        // Android 13+ permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.POST_NOTIFICATIONS);
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissionList.add(Manifest.permission.READ_MEDIA_AUDIO);
        }
        
        // Android 12+ permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissionList.add(Manifest.permission.BLUETOOTH_SCAN);
            permissionList.add(Manifest.permission.SCHEDULE_EXACT_ALARM);
        }
        
        // Android 11+ permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        
        // Android 10+ permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        
        String[] finalPermissions = permissionList.toArray(new String[0]);
        ActivityCompat.requestPermissions(this, finalPermissions, MULTIPLE_PERMISSIONS_CODE);
    }
    
    // Check if all essential permissions are granted
    private boolean hasAllPermissions() {
        String[] essentialPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
        };
        
        for (String permission : essentialPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    // Check for special permissions that require different handling
    private void checkSpecialPermissions() {
        // Check for overlay permission (SYSTEM_ALERT_WINDOW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.canDrawOverlays(this)) {
                android.content.Intent intent = new android.content.Intent(
                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:" + getPackageName())
                );
                startActivityForResult(intent, 123);
            }
        }
        
        // Check for battery optimization exemption
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.os.PowerManager powerManager = (android.os.PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                android.content.Intent intent = new android.content.Intent(
                    android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                );
                intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        
        // Check for exact alarm permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                android.content.Intent intent = new android.content.Intent(
                    android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                );
                startActivity(intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Request all permissions needed for social media shopping app
        if (!hasAllPermissions()) {
            requestAllPermissions();
        }
        
        // Check special permissions that require different handling
        checkSpecialPermissions();
        
        setupWebView();
        
        // Load the configured development URL
        mWebView.loadUrl(DEVELOPMENT_URL);
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        
        // Enable JavaScript and DOM storage
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        
        // Enable file access and content
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        
        // Viewport and zoom settings
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        
        // Text and media settings
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        
        // Geolocation
        webSettings.setGeolocationEnabled(true);
        
        // Allow mixed content for development
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        
        // User agent for better compatibility
        webSettings.setUserAgentString(webSettings.getUserAgentString() + " SnapCart/1.0");
        
        // Cache settings for better performance
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Cookie management for persistent login
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        
        // Set custom WebView client and chrome client
        mWebView.setWebViewClient(new SnapCartWebViewClient());
        mWebView.setWebChromeClient(new SnapCartWebChromeClient());
        
        // Download listener
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            Uri source = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(source);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading from Snap Cart...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Download started...", Toast.LENGTH_SHORT).show();
        });
    }

    // Custom WebViewClient for Snap Cart
    private class SnapCartWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // Ensure cookies are saved
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            }
        }
    }

    // Custom WebChromeClient for camera, file upload, geolocation, etc.
    private class SnapCartWebChromeClient extends WebChromeClient {
        
        // Handle file uploads
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            
            uploadMessage = filePathCallback;
            
            // Create intent for file selection
            android.content.Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
            } catch (android.content.ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(MainActivity.this, "Cannot open file chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
        
        // Handle geolocation requests
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
        
        // Handle camera and microphone permissions
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }
        
        // Handle JavaScript alerts
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Snap Cart")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> result.confirm())
                .setCancelable(false)
                .create()
                .show();
            return true;
        }
        
        // Handle JavaScript confirms
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Snap Cart")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> result.confirm())
                .setNegativeButton("Cancel", (dialog, which) -> result.cancel())
                .setCancelable(false)
                .create()
                .show();
            return true;
        }
    }

    // Handle file chooser results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessage == null) return;
            
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            uploadMessage.onReceiveValue(results);
            uploadMessage = null;
        }
    }
    
    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_CODE:
                int grantedCount = 0;
                int deniedCount = 0;
                java.util.List<String> deniedPermissions = new java.util.ArrayList<>();
                
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedCount++;
                    } else {
                        deniedCount++;
                        deniedPermissions.add(permissions[i]);
                    }
                }
                
                if (deniedCount == 0) {
                    Toast.makeText(this, "🎉 All permissions granted! Snap Cart is ready to go!", Toast.LENGTH_SHORT).show();
                } else {
                    String message = String.format(
                        "📊 Permissions: %d granted, %d denied\n" +
                        "⚠️ Some features may be limited without all permissions.",
                        grantedCount, deniedCount
                    );
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    
                    // Show which permissions were denied for debugging
                    if (deniedPermissions.size() > 0) {
                        android.util.Log.w("SnapCart", "Denied permissions: " + deniedPermissions.toString());
                    }
                }
                break;
        }
    }
    
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }
    
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
            mWebView = null;
        }
        super.onDestroy();
    }
}