package com.sanbytez.snapcart;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Cart Status Widget - Shows current cart status and quick checkout
 */
public class CartStatusWidget extends AppWidgetProvider {
    
    private static final String PREFS_NAME = "SnapCartStatusPrefs";
    private static final String PREF_CART_ITEMS = "cart_items";
    private static final String PREF_CART_TOTAL = "cart_total";
    private static final String ACTION_OPEN_CART = "com.webview.myapplication.OPEN_CART";
    private static final String ACTION_QUICK_CHECKOUT = "com.webview.myapplication.QUICK_CHECKOUT";
    private static final String ACTION_REFRESH_CART = "com.webview.myapplication.REFRESH_CART";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (ACTION_OPEN_CART.equals(intent.getAction())) {
            // Open the main app to cart section
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.putExtra("section", "cart");
            context.startActivity(launchIntent);
            
        } else if (ACTION_QUICK_CHECKOUT.equals(intent.getAction())) {
            // Open the main app to checkout
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.putExtra("section", "checkout");
            context.startActivity(launchIntent);
            
        } else if (ACTION_REFRESH_CART.equals(intent.getAction())) {
            // Simulate cart refresh with random data
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Random random = new Random();
            int items = random.nextInt(10) + 1;
            double total = (random.nextDouble() * 100) + 10; // $10-$110
            
            prefs.edit()
                .putInt(PREF_CART_ITEMS, items)
                .putFloat(PREF_CART_TOTAL, (float) total)
                .apply();
            
            // Update all widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new android.content.ComponentName(context, CartStatusWidget.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get cart data from preferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int cartItems = prefs.getInt(PREF_CART_ITEMS, 0);
        float cartTotal = prefs.getFloat(PREF_CART_TOTAL, 0.0f);
        
        // Format currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedTotal = currencyFormat.format(cartTotal);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cart_status_widget);
        
        // Update widget content
        if (cartItems > 0) {
            views.setTextViewText(R.id.widget_cart_items, String.valueOf(cartItems));
            views.setTextViewText(R.id.widget_cart_total, formattedTotal);
            views.setTextViewText(R.id.widget_cart_status, "Ready to checkout");
            views.setInt(R.id.widget_checkout_button, "setBackgroundResource", R.drawable.checkout_button_active);
        } else {
            views.setTextViewText(R.id.widget_cart_items, "0");
            views.setTextViewText(R.id.widget_cart_total, "$0.00");
            views.setTextViewText(R.id.widget_cart_status, "Cart is empty");
            views.setInt(R.id.widget_checkout_button, "setBackgroundResource", R.drawable.checkout_button_inactive);
        }
        
        // Set up click intents
        Intent openCartIntent = new Intent(context, CartStatusWidget.class);
        openCartIntent.setAction(ACTION_OPEN_CART);
        PendingIntent openCartPendingIntent = PendingIntent.getBroadcast(context, 0, 
            openCartIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_cart_container, openCartPendingIntent);
        
        Intent checkoutIntent = new Intent(context, CartStatusWidget.class);
        checkoutIntent.setAction(ACTION_QUICK_CHECKOUT);
        PendingIntent checkoutPendingIntent = PendingIntent.getBroadcast(context, 1, 
            checkoutIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_checkout_button, checkoutPendingIntent);
        
        Intent refreshIntent = new Intent(context, CartStatusWidget.class);
        refreshIntent.setAction(ACTION_REFRESH_CART);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 2, 
            refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Initialize with some sample data
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(PREF_CART_ITEMS)) {
            Random random = new Random();
            prefs.edit()
                .putInt(PREF_CART_ITEMS, random.nextInt(5) + 1)
                .putFloat(PREF_CART_TOTAL, (float) ((random.nextDouble() * 50) + 15))
                .apply();
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
