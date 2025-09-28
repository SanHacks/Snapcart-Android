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
 * Shopping List Widget - Quick access to add items to shopping list
 */
public class ShoppingListWidget extends AppWidgetProvider {
    
    private static final String PREFS_NAME = "SnapCartWidgetPrefs";
    private static final String PREF_ITEM_COUNT = "shopping_list_count";
    private static final String ACTION_ADD_ITEM = "com.webview.myapplication.ADD_ITEM";
    private static final String ACTION_OPEN_APP = "com.webview.myapplication.OPEN_APP";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update all widget instances
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (ACTION_ADD_ITEM.equals(intent.getAction())) {
            // Simulate adding an item (in real app, this would sync with web app)
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int currentCount = prefs.getInt(PREF_ITEM_COUNT, 0);
            prefs.edit().putInt(PREF_ITEM_COUNT, currentCount + 1).apply();
            
            // Update all widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new android.content.ComponentName(context, ShoppingListWidget.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
            
        } else if (ACTION_OPEN_APP.equals(intent.getAction())) {
            // Open the main app
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get current shopping list count
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int itemCount = prefs.getInt(PREF_ITEM_COUNT, 0);
        
        // Get current time for last updated
        String lastUpdated = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            .format(new Date());

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shopping_list_widget);
        
        // Update widget content
        views.setTextViewText(R.id.widget_item_count, String.valueOf(itemCount));
        views.setTextViewText(R.id.widget_last_updated, "Updated: " + lastUpdated);
        
        // Set up click intents
        Intent addItemIntent = new Intent(context, ShoppingListWidget.class);
        addItemIntent.setAction(ACTION_ADD_ITEM);
        PendingIntent addItemPendingIntent = PendingIntent.getBroadcast(context, 0, 
            addItemIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_add_button, addItemPendingIntent);
        
        Intent openAppIntent = new Intent(context, ShoppingListWidget.class);
        openAppIntent.setAction(ACTION_OPEN_APP);
        PendingIntent openAppPendingIntent = PendingIntent.getBroadcast(context, 1, 
            openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }
}
