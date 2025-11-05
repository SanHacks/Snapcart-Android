package com.sanbytez.snapcart;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Barcode Scanner Widget - Quick access to barcode scanning
 */
public class BarcodeScannerWidget extends AppWidgetProvider {
    
    private static final String PREFS_NAME = "SharpSavingsScannerPrefs";
    private static final String PREF_LAST_SCAN = "last_scan_product";
    private static final String PREF_SCAN_COUNT = "scan_count";
    private static final String ACTION_SCAN_BARCODE = "com.webview.myapplication.SCAN_BARCODE";
    private static final String ACTION_SCAN_QR = "com.webview.myapplication.SCAN_QR";
    private static final String ACTION_OPEN_SCANNER = "com.webview.myapplication.OPEN_SCANNER";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (ACTION_SCAN_BARCODE.equals(intent.getAction()) || 
            ACTION_SCAN_QR.equals(intent.getAction()) ||
            ACTION_OPEN_SCANNER.equals(intent.getAction())) {
            
            // Open the main app with camera/scanner
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.putExtra("action", "scan");
            launchIntent.putExtra("type", intent.getAction().contains("QR") ? "qr" : "barcode");
            context.startActivity(launchIntent);
            
            // Update scan count
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int scanCount = prefs.getInt(PREF_SCAN_COUNT, 0);
            prefs.edit()
                .putInt(PREF_SCAN_COUNT, scanCount + 1)
                .putString(PREF_LAST_SCAN, "Product #" + (scanCount + 1))
                .apply();
            
            // Update all widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new android.content.ComponentName(context, BarcodeScannerWidget.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get scan data from preferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int scanCount = prefs.getInt(PREF_SCAN_COUNT, 0);
        String lastScan = prefs.getString(PREF_LAST_SCAN, "No recent scans");
        
        // Get current time for last updated
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(new Date());

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barcode_scanner_widget);
        
        // Update widget content
        views.setTextViewText(R.id.widget_scan_count, String.valueOf(scanCount));
        views.setTextViewText(R.id.widget_last_scan, lastScan);
        views.setTextViewText(R.id.widget_last_updated, "Updated: " + currentTime);
        
        // Set up click intents
        Intent scanBarcodeIntent = new Intent(context, BarcodeScannerWidget.class);
        scanBarcodeIntent.setAction(ACTION_SCAN_BARCODE);
        PendingIntent scanBarcodePendingIntent = PendingIntent.getBroadcast(context, 0, 
            scanBarcodeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_scan_barcode_button, scanBarcodePendingIntent);
        
        Intent scanQRIntent = new Intent(context, BarcodeScannerWidget.class);
        scanQRIntent.setAction(ACTION_SCAN_QR);
        PendingIntent scanQRPendingIntent = PendingIntent.getBroadcast(context, 1, 
            scanQRIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_scan_qr_button, scanQRPendingIntent);
        
        Intent openScannerIntent = new Intent(context, BarcodeScannerWidget.class);
        openScannerIntent.setAction(ACTION_OPEN_SCANNER);
        PendingIntent openScannerPendingIntent = PendingIntent.getBroadcast(context, 2, 
            openScannerIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_scanner_container, openScannerPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
