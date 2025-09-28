package com.webview.myapplication;

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
import java.util.Random;

/**
 * Deals Widget - Shows current deals and offers
 */
public class DealsWidget extends AppWidgetProvider {
    
    private static final String PREFS_NAME = "SnapCartDealsPrefs";
    private static final String ACTION_REFRESH_DEALS = "com.webview.myapplication.REFRESH_DEALS";
    private static final String ACTION_OPEN_DEALS = "com.webview.myapplication.OPEN_DEALS";
    
    // Sample deals data
    private static final String[] DEAL_TITLES = {
        "Fresh Fruits 30% OFF",
        "Buy 2 Get 1 FREE Snacks",
        "Organic Vegetables 25% OFF",
        "Dairy Products 20% OFF",
        "Weekend Special 40% OFF"
    };
    
    private static final String[] DEAL_DESCRIPTIONS = {
        "All fresh fruits including apples, bananas, oranges",
        "Mix and match your favorite snacks",
        "Farm fresh organic vegetables",
        "Milk, cheese, yogurt and more",
        "Selected items for weekend shoppers"
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (ACTION_REFRESH_DEALS.equals(intent.getAction())) {
            // Refresh deals data
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new android.content.ComponentName(context, DealsWidget.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
            
        } else if (ACTION_OPEN_DEALS.equals(intent.getAction())) {
            // Open the main app to deals section
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.putExtra("section", "deals");
            context.startActivity(launchIntent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get a random deal to display
        Random random = new Random();
        int dealIndex = random.nextInt(DEAL_TITLES.length);
        
        String dealTitle = DEAL_TITLES[dealIndex];
        String dealDescription = DEAL_DESCRIPTIONS[dealIndex];
        
        // Calculate time until deal expires (simulate)
        String timeLeft = getRandomTimeLeft();
        
        // Get current time for last updated
        String lastUpdated = new SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(new Date());

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.deals_widget);
        
        // Update widget content
        views.setTextViewText(R.id.widget_deal_title, dealTitle);
        views.setTextViewText(R.id.widget_deal_description, dealDescription);
        views.setTextViewText(R.id.widget_time_left, "Ends in " + timeLeft);
        views.setTextViewText(R.id.widget_last_updated, "Updated: " + lastUpdated);
        
        // Set up click intents
        Intent refreshIntent = new Intent(context, DealsWidget.class);
        refreshIntent.setAction(ACTION_REFRESH_DEALS);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, 
            refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);
        
        Intent openDealsIntent = new Intent(context, DealsWidget.class);
        openDealsIntent.setAction(ACTION_OPEN_DEALS);
        PendingIntent openDealsPendingIntent = PendingIntent.getBroadcast(context, 1, 
            openDealsIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_deals_container, openDealsPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    private static String getRandomTimeLeft() {
        Random random = new Random();
        int hours = random.nextInt(24) + 1;
        int minutes = random.nextInt(60);
        return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
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
