package com.nilhcem.the10mnwatchface.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.wearable.watchface.CanvasWatchFaceService;

import java.util.Calendar;
import java.util.TimeZone;

public class TimezoneHelper {

    private final Calendar calendar;
    private final CanvasWatchFaceService watchfaceService;
    private final CanvasWatchFaceService.Engine watchfaceEngine;

    // Receiver to update the time zone
    private final BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            calendar.setTimeZone(TimeZone.getDefault());
            watchfaceEngine.invalidate();
        }
    };

    private boolean registeredTimeZoneReceiver;

    public TimezoneHelper(CanvasWatchFaceService service, CanvasWatchFaceService.Engine engine) {
        calendar = Calendar.getInstance();
        watchfaceService = service;
        watchfaceEngine = engine;
    }

    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            registerReceiver();
            // Update time zone in case it changed while we weren't visible.
            calendar.setTimeZone(TimeZone.getDefault());
        } else {
            unregisterReceiver();
        }
    }

    public void setTimeToNow() {
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    public Calendar getCalendar() {
        return calendar;
    }

    private void registerReceiver() {
        if (registeredTimeZoneReceiver) {
            return;
        }
        registeredTimeZoneReceiver = true;
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        watchfaceService.registerReceiver(timeZoneReceiver, filter);
    }

    private void unregisterReceiver() {
        if (!registeredTimeZoneReceiver) {
            return;
        }
        registeredTimeZoneReceiver = false;
        watchfaceService.unregisterReceiver(timeZoneReceiver);
    }
}
