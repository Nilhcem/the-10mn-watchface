package com.nilhcem.the10mnwatchface;

import android.graphics.Canvas;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;

import com.nilhcem.the10mnwatchface.core.WatchMode;

public class MyWatchFace extends BaseWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends BaseWatchFaceService.Engine {

        private MyWatchFaceRenderer watch;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            watch = new MyWatchFaceRenderer(context);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setAcceptsTapEvents(false)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_VISIBLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setHotwordIndicatorGravity(Gravity.TOP)
                    .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_OPAQUE)
                    .setShowSystemUiTime(false)
                    .setShowUnreadCountIndicator(true)
                    .setStatusBarGravity(Gravity.TOP)
                    .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR)
                    .build());
        }

        @Override
        protected void onWatchModeChanged(WatchMode mode) {
            watch.setMode(mode);
        }

        @Override
        protected void onWidthChanged(int newWidth) {
            watch.setSize(newWidth);
            invalidate();
        }

        @Override
        protected void onDrawTime(Canvas canvas, float angleHours, float angleMinutes, float angleSeconds) {
            watch.drawTime(canvas, angleHours, angleMinutes, angleSeconds);
        }
    }
}
