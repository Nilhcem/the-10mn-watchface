package com.nilhcem.the10mnwatchface.core;

import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;

import java.lang.ref.WeakReference;

public class TimerHelper {

    private static final int MSG_UPDATE_TIME = 0;

    private static class TimerHandler extends Handler {
        private final WeakReference<TimerHelper> timerHelperRef;

        public TimerHandler(TimerHelper timerHelper) {
            timerHelperRef = new WeakReference<>(timerHelper);
        }

        @Override
        public void handleMessage(Message msg) {
            TimerHelper timerHelper = timerHelperRef.get();
            if (timerHelper != null && msg.what == MSG_UPDATE_TIME) {
                timerHelper.handleUpdateTimeMessage();
            }
        }
    }

    private final CanvasWatchFaceService.Engine watchfaceEngine;
    private final int updateRateMs;
    private final Handler timerHandler;

    public TimerHelper(CanvasWatchFaceService.Engine engine, int updateRateMs) {
        watchfaceEngine = engine;
        timerHandler = new TimerHandler(this);
        this.updateRateMs = updateRateMs;
    }

    public void updateTimer() {
        timerHandler.removeMessages(MSG_UPDATE_TIME);
        if (shouldTimerBeRunning()) {
            timerHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        }
    }

    private void handleUpdateTimeMessage() {
        watchfaceEngine.invalidate();
        if (shouldTimerBeRunning()) {
            long timeMs = System.currentTimeMillis();
            long delayMs = updateRateMs - (timeMs % updateRateMs);
            timerHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
        }
    }

    private boolean shouldTimerBeRunning() {
        return watchfaceEngine.isVisible() && !watchfaceEngine.isInAmbientMode();
    }
}
