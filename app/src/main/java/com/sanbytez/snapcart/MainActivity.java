package com.sanbytez.snapcart;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
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
    
    // Loading screen components
    private LinearLayout loadingScreen;
    private TextView loadingText;
    private TextView connectionStatusText;
    private ImageView connectionStatusIcon;
    private ImageView loadingIcon;
    
    // Development URLs - easily change these for different testing environments
    private static final String LOCALHOST_URL = "http://localhost:3000";
    private static final String NETWORK_URL = "http://10.1.118.128:3000";
    private static final String NGROK_URL = "https://324d8d0f97a9.ngrok-free.app";
    private static final String PRODUCTION_URL = "https://snapcartza.co.za";
    private static final String DEVELOPMENT_URL = PRODUCTION_URL; // Production URL for SnapCart ZA
    
    // 100+ Randomized Loading Messages - Grocery Comparison Theme
    private static final String[] LOADING_MESSAGES = {
        // SnapCart Brand Messages
        "Welcome to SnapCart - where grocery comparison begins",
        "SnapCart is scanning the best grocery deals",
        "Connecting to SnapCart's price comparison engine",
        "SnapCart is preparing your personalized shopping experience",
        "Loading SnapCart's smart grocery comparison tools",
        
        // Grocery Comparison Core Messages
        "Comparing grocery prices across multiple stores",
        "Finding the best deals on your favorite products",
        "Scanning thousands of grocery items for price matches",
        "Analyzing grocery prices to save you money",
        "Searching for the lowest grocery prices in your area",
        "Comparing fresh produce prices from local stores",
        "Finding bulk buying opportunities to maximize savings",
        "Checking seasonal discounts on grocery items",
        "Locating store-specific promotions and deals",
        "Matching coupons with current grocery prices",
        
        // Shopping Experience Messages
        "Preparing your smart shopping assistant",
        "Loading your personalized grocery recommendations",
        "Setting up your price comparison dashboard",
        "Initializing your savings calculator",
        "Preparing your shopping list optimizer",
        "Loading store location and inventory data",
        "Setting up real-time price alerts",
        "Preparing your grocery budget tracker",
        "Loading nutritional information database",
        "Initializing barcode scanning capabilities",
        
        // Store & Product Messages
        "Connecting to local grocery store networks",
        "Loading fresh produce availability",
        "Checking dairy product prices and expiry dates",
        "Scanning meat and poultry price comparisons",
        "Loading bakery item freshness indicators",
        "Comparing frozen food deals across stores",
        "Checking pantry staples for bulk discounts",
        "Loading organic product price comparisons",
        "Scanning beverage deals and promotions",
        "Checking snack and confectionery prices",
        
        // Savings & Deals Messages
        "Calculating potential savings on your shopping list",
        "Finding weekly specials and limited-time offers",
        "Locating clearance items and markdown deals",
        "Checking loyalty program benefits and rewards",
        "Finding buy-one-get-one-free opportunities",
        "Scanning for price-match guarantees",
        "Loading cashback and rebate opportunities",
        "Checking student and senior citizen discounts",
        "Finding family pack savings opportunities",
        "Locating end-of-season grocery clearances",
        
        // Technology & Features Messages
        "Initializing smart price tracking algorithms",
        "Loading machine learning price prediction models",
        "Setting up real-time inventory synchronization",
        "Preparing advanced search and filter options",
        "Loading GPS-based store locator",
        "Initializing voice search capabilities",
        "Setting up shopping list sharing features",
        "Loading recipe integration and meal planning",
        "Preparing nutritional analysis tools",
        "Initializing expense tracking and budgeting",
        
        // User Experience Messages
        "Customizing your shopping preferences",
        "Loading your favorite products and brands",
        "Setting up dietary restriction filters",
        "Preparing allergen information database",
        "Loading your shopping history and patterns",
        "Customizing store preference rankings",
        "Setting up price drop notifications",
        "Loading your saved shopping lists",
        "Preparing quick reorder functionality",
        "Initializing family account sharing",
        
        // Market & Competition Messages
        "Analyzing current market prices and trends",
        "Comparing competitor pricing strategies",
        "Loading regional price variations",
        "Checking supply chain impact on prices",
        "Analyzing seasonal price fluctuations",
        "Comparing brand vs generic product prices",
        "Loading import vs local product comparisons",
        "Checking wholesale vs retail price differences",
        "Analyzing bulk purchase savings potential",
        "Loading price history and trend analysis",
        
        // Convenience & Time-Saving Messages
        "Optimizing your shopping route for efficiency",
        "Planning the fastest grocery shopping trip",
        "Loading store hours and peak time data",
        "Preparing contactless shopping options",
        "Setting up curbside pickup availability",
        "Loading delivery service comparisons",
        "Checking express checkout options",
        "Preparing mobile payment integrations",
        "Loading parking availability at stores",
        "Setting up shopping reminder notifications",
        
        // Quality & Freshness Messages
        "Checking product quality ratings and reviews",
        "Loading freshness indicators for perishables",
        "Comparing product ratings across stores",
        "Checking expiration date tracking",
        "Loading customer satisfaction scores",
        "Comparing product origin and sourcing",
        "Checking organic certification status",
        "Loading fair trade product identification",
        "Comparing local vs imported product options",
        "Checking product recall and safety information",
        
        // Additional Variety Messages
        "Preparing your grocery comparison adventure",
        "Loading the ultimate shopping companion",
        "Setting up your personal grocery concierge",
        "Initializing smart shopping recommendations",
        "Preparing your pocket-friendly shopping guide",
        "Loading your grocery savings superhero",
        "Setting up your intelligent shopping assistant",
        "Preparing your budget-conscious shopping buddy",
        "Loading your grocery deal detective",
        "Initializing your smart spending advisor"
    };
    
    private static final String[] CONNECTION_MESSAGES = {
        "Establishing secure connection to SnapCart servers",
        "Connecting to grocery price databases",
        "Syncing with local store inventories",
        "Loading real-time price updates",
        "Connecting to deal aggregation services",
        "Establishing link to savings calculator",
        "Syncing with coupon databases",
        "Connecting to store location services",
        "Loading product comparison engines",
        "Establishing secure payment gateways",
        "Syncing with loyalty program networks",
        "Connecting to nutritional databases",
        "Loading barcode scanning services",
        "Establishing GPS location services",
        "Syncing with shopping list cloud storage",
        "Connecting to recipe recommendation engines",
        "Loading user preference synchronization",
        "Establishing real-time inventory feeds",
        "Syncing with price alert systems",
        "Connecting to customer review platforms"
    };
    
    private static final int[] LOADING_ICONS = {
        R.drawable.ic_loading_cart,
        R.drawable.ic_loading_compare,
        R.drawable.ic_loading_search,
        R.drawable.ic_loading_deals,
        R.drawable.ic_loading_grocery
    };

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
        
        // Initialize loading screen components
        initializeLoadingScreen();
        
        // Request all permissions needed for social media shopping app
        if (!hasAllPermissions()) {
            requestAllPermissions();
        }
        
        // Check special permissions that require different handling
        checkSpecialPermissions();
        
        setupWebView();
        
        // Show loading screen and start loading
        showLoadingScreen();
        
        // Load the configured development URL
        mWebView.loadUrl(DEVELOPMENT_URL);
    }
    
    // Initialize loading screen components
    private void initializeLoadingScreen() {
        loadingScreen = findViewById(R.id.loading_screen);
        loadingText = findViewById(R.id.loading_text);
        connectionStatusText = findViewById(R.id.connection_status_text);
        connectionStatusIcon = findViewById(R.id.connection_status_icon);
        loadingIcon = findViewById(R.id.loading_icon);
    }
    
    // Show loading screen
    private void showLoadingScreen() {
        loadingScreen.setVisibility(android.view.View.VISIBLE);
        mWebView.setVisibility(android.view.View.GONE);
        
        // Update loading text with random message
        updateLoadingTextRandom();
        
        // Start a timer to update loading messages
        startLoadingAnimation();
    }
    
    // Hide loading screen and show WebView
    private void hideLoadingScreen() {
        loadingScreen.setVisibility(android.view.View.GONE);
        mWebView.setVisibility(android.view.View.VISIBLE);
    }
    
    // Update loading text
    private void updateLoadingText(String text) {
        if (loadingText != null) {
            loadingText.setText(text);
        }
    }
    
    // Update loading text with random message
    private void updateLoadingTextRandom() {
        if (loadingText != null) {
            java.util.Random random = new java.util.Random();
            String randomMessage = LOADING_MESSAGES[random.nextInt(LOADING_MESSAGES.length)];
            loadingText.setText(randomMessage);
            
            // Also update icon randomly
            if (loadingIcon != null) {
                int randomIcon = LOADING_ICONS[random.nextInt(LOADING_ICONS.length)];
                loadingIcon.setImageResource(randomIcon);
            }
        }
    }
    
    // Update connection status with random message
    private void updateConnectionStatusRandom() {
        if (connectionStatusText != null) {
            java.util.Random random = new java.util.Random();
            String randomMessage = CONNECTION_MESSAGES[random.nextInt(CONNECTION_MESSAGES.length)];
            connectionStatusText.setText(randomMessage);
        }
    }
    
    // Update connection status
    private void updateConnectionStatus(String status, boolean isConnected) {
        if (connectionStatusText != null) {
            connectionStatusText.setText(status);
        }
        if (connectionStatusIcon != null && isConnected) {
            connectionStatusIcon.setImageResource(R.drawable.ic_wifi_connecting);
        }
    }
    
    // Start loading animation with randomized messages
    private void startLoadingAnimation() {
        android.os.Handler handler = new android.os.Handler();
        
        // Update messages every 1.5-2 seconds with random content
        handler.postDelayed(() -> {
            updateLoadingTextRandom();
            updateConnectionStatusRandom();
        }, 1500);
        
        handler.postDelayed(() -> {
            updateLoadingTextRandom();
            updateConnectionStatusRandom();
        }, 3000);
        
        handler.postDelayed(() -> {
            updateLoadingTextRandom();
            updateConnectionStatusRandom();
        }, 4500);
        
        handler.postDelayed(() -> {
            updateLoadingTextRandom();
            updateConnectionStatusRandom();
        }, 6000);
        
        handler.postDelayed(() -> {
            updateLoadingTextRandom();
            updateConnectionStatusRandom();
        }, 7500);
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
            // Download will show in notification bar automatically
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
            // Update loading status when page starts loading
            updateLoadingTextRandom();
            updateConnectionStatusRandom();
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            
            // Ensure cookies are saved
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            }
            
            // Add a small delay to ensure content is fully rendered
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(() -> {
                updateLoadingText("Welcome to SnapCart - Start comparing and saving!");
                updateConnectionStatus("Connected successfully - Ready to find the best deals!", true);
                
                // Hide loading screen after a brief moment
                handler.postDelayed(() -> hideLoadingScreen(), 800);
            }, 1000);
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            
            // Update loading screen to show error
            updateLoadingText("Connection issue detected - Please check your internet");
            updateConnectionStatus("Retrying connection to SnapCart servers...", false);
            
            // Log error for debugging (production apps shouldn't show technical error details to users)
            android.util.Log.e("SnapCart", "Failed to load page: " + description + " (URL: " + failingUrl + ")");
        }
        
        @Override
        public void onReceivedHttpError(WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            
            if (request.isForMainFrame()) {
                updateLoadingText("Server temporarily unavailable - Please try again");
                updateConnectionStatus("Attempting to reconnect to SnapCart...", false);
            }
        }
    }

    // Custom WebChromeClient for camera, file upload, geolocation, etc.
    private class SnapCartWebChromeClient extends WebChromeClient {
        
        // Handle page loading progress
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            
            // Update loading text based on progress with randomized messages
            if (newProgress < 25) {
                updateLoadingTextRandom();
                updateConnectionStatus("Progress: " + newProgress + "% - Initializing comparison engine", true);
            } else if (newProgress < 50) {
                updateLoadingTextRandom();
                updateConnectionStatus("Progress: " + newProgress + "% - Loading grocery databases", true);
            } else if (newProgress < 75) {
                updateLoadingTextRandom();
                updateConnectionStatus("Progress: " + newProgress + "% - Preparing price comparisons", true);
            } else if (newProgress < 100) {
                updateLoadingTextRandom();
                updateConnectionStatus("Progress: " + newProgress + "% - Almost ready to save money", true);
            }
        }
        
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
                android.util.Log.e("SnapCart", "Cannot open file chooser", e);
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
                
                // Only show a simple message if some permissions are denied
                if (deniedCount > 0) {
                    // Log denied permissions for debugging (only in logs, not user-facing)
                    if (deniedPermissions.size() > 0) {
                        android.util.Log.w("SnapCart", "Some permissions were denied: " + deniedPermissions.toString());
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