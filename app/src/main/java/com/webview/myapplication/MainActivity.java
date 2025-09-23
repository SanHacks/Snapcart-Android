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

    // Request all necessary permissions for Snap Cart
    private void requestAllPermissions() {
        String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        
        // Add notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] newPermissions = new String[permissions.length + 1];
            System.arraycopy(permissions, 0, newPermissions, 0, permissions.length);
            newPermissions[permissions.length] = Manifest.permission.POST_NOTIFICATIONS;
            permissions = newPermissions;
        }
        
        ActivityCompat.requestPermissions(this, permissions, MULTIPLE_PERMISSIONS_CODE);
    }
    
    // Check if all essential permissions are granted
    private boolean hasAllPermissions() {
        String[] essentialPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
        };
        
        for (String permission : essentialPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    // Adjust WebView settings based on screen size and density
    private void adjustWebViewForScreenSize() {
        android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        
        int screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
        int screenHeightDp = (int) (displayMetrics.heightPixels / displayMetrics.density);
        
        // Log screen info for debugging
        android.util.Log.d("SnapCart", "Screen: " + screenWidthDp + "x" + screenHeightDp + "dp, density: " + displayMetrics.density);
        
        // Adjust initial scale based on screen size
        float initialScale = 1.0f;
        if (screenWidthDp < 360) {
            // Very small screens (< 360dp width)
            initialScale = 1.0f;
        } else if (screenWidthDp < 480) {
            // Small screens (360-480dp width)
            initialScale = 1.0f;
        } else {
            // Normal and larger screens
            initialScale = 1.0f;
        }
        
        // Apply initial scale
        mWebView.setInitialScale((int) (initialScale * 100));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Request all permissions needed for Snap Cart
        if (!hasAllPermissions()) {
            requestAllPermissions();
        }
        
        setupWebView();
        
        // Load the configured development URL
        mWebView.loadUrl(DEVELOPMENT_URL);
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        
        // Adjust WebView based on screen density
        adjustWebViewForScreenSize();
        
        // Enable JavaScript and DOM storage
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        
        // Enable file access and content
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        
        // Viewport and zoom settings - optimized for mobile responsiveness
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        
        // Disable zoom to prevent scaling issues
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        
        // Set initial and default zoom scales
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        
        // Force viewport meta tag support
        webSettings.setLoadWithOverviewMode(false); // This helps with responsive design
        
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
        
        // Additional viewport and density settings
        webSettings.setMinimumFontSize(8); // Prevent text from being too small
        webSettings.setMinimumLogicalFontSize(8);
        webSettings.setDefaultFontSize(16);
        webSettings.setDefaultFixedFontSize(13);
        
        // Force density settings for better scaling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Force hardware acceleration
            mWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        }
        
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
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            
            // Inject viewport meta tag if not present to ensure proper mobile scaling
            String viewportScript = 
                "javascript:(function() {" +
                "    var viewport = document.querySelector('meta[name=viewport]');" +
                "    if (!viewport) {" +
                "        viewport = document.createElement('meta');" +
                "        viewport.name = 'viewport';" +
                "        viewport.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, shrink-to-fit=no';" +
                "        document.head.appendChild(viewport);" +
                "    } else {" +
                "        viewport.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, shrink-to-fit=no';" +
                "    }" +
                "    var snapCartStyle = document.getElementById('snapcart-mobile-fix');" +
                "    if (!snapCartStyle) {" +
                "        var style = document.createElement('style');" +
                "        style.id = 'snapcart-mobile-fix';" +
                "        style.innerHTML = '" +
                "            html { -webkit-text-size-adjust: 100%; }" +
                "            body { margin: 0; padding: 0; min-width: 320px; }" +
                "            * { box-sizing: border-box; }" +
                "        ';" +
                "        document.head.appendChild(style);" +
                "    }" +
                "})()";
            
            view.loadUrl(viewportScript);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            
            // Ensure cookies are saved
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            }
            
            // Additional responsive design fixes
            String responsiveScript = 
                "javascript:(function() {" +
                "    var style = document.createElement('style');" +
                "    style.innerHTML = '" +
                "        @media screen and (max-width: 768px) {" +
                "            body { transform-origin: top left; }" +
                "            .container, .main-content { max-width: 100% !important; }" +
                "            img { max-width: 100% !important; height: auto !important; }" +
                "            table { width: 100% !important; }" +
                "        }" +
                "    ';" +
                "    document.head.appendChild(style);" +
                "})()";
            
            view.loadUrl(responsiveScript);
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
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                
                if (allPermissionsGranted) {
                    Toast.makeText(this, "All permissions granted for Snap Cart!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Some permissions denied. App may not work fully.", Toast.LENGTH_LONG).show();
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