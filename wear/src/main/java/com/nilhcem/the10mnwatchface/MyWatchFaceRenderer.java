package com.nilhcem.the10mnwatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.nilhcem.the10mnwatchface.core.WatchMode;

public class MyWatchFaceRenderer {

    private final Paint bgPaint = new Paint();
    private final Paint mnPaint = new Paint();
    private final Paint handHourPaint = new Paint();
    private final Paint handMinutePaint = new Paint();
    private final Paint handSecondPaint = new Paint();
    private final Paint handWasherPaint = new Paint();
    private final Paint handScrewPaint = new Paint();
    private final Paint shadowPaint = new Paint();
    private final Paint ambientPaint = new Paint();
    private final Paint ambientBlackPaint = new Paint();

    private final Path mnPath = new Path();
    private final Path handHourPath = new Path();
    private final Path handMinutePath = new Path();
    private final Path handSecondPath = new Path();
    private final Path handWasherPath = new Path();
    private final Path handScrewPath = new Path();

    private final DisplayMetrics displayMetrics;

    private int size;
    private float radius;
    private WatchMode mode = WatchMode.INTERACTIVE;

    public MyWatchFaceRenderer(Context context) {
        displayMetrics = context.getResources().getDisplayMetrics();
        initPaintObjects();
    }

    public void setSize(int size) {
        if (this.size != size) {
            this.size = size;
            radius = 0.5f * size;

            bgPaint.setShader(new RadialGradient(radius, radius, dpToPx(8),
                    new int[]{0xff252525, 0xff252525, Color.BLACK, Color.BLACK},
                    new float[]{0, 0.25f, 0.25f, 1f}, Shader.TileMode.REPEAT));

            mnPath.set(createMinutesIndicators(radius, radius, radius - dpToPx(10)));
            handHourPath.set(createWatchHandPath(radius, radius, radius - dpToPx(42), dpToPx(10), dpToPx(6), handHourPaint));
            handMinutePath.set(createWatchHandPath(radius, radius, radius - dpToPx(16), dpToPx(8), dpToPx(5), handMinutePaint));
            handSecondPath.set(createWatchHandSecondsPath(radius, radius, radius - dpToPx(16), dpToPx(5)));

            handWasherPath.reset();
            handWasherPath.addCircle(radius, radius, dpToPx(5), Path.Direction.CW);

            handScrewPath.reset();
            handScrewPath.addCircle(radius, radius, dpToPx(2), Path.Direction.CW);
        }
    }

    public void setMode(WatchMode mode) {
        this.mode = mode;
        ambientPaint.setAntiAlias(mode != WatchMode.LOW_BIT);
        ambientBlackPaint.setAntiAlias(mode != WatchMode.LOW_BIT);
    }

    public void drawTime(Canvas canvas, float angleHours, float angleMinutes, float angleSeconds) {
        boolean interactive = mode == WatchMode.INTERACTIVE;

        // Background
        if (interactive) {
            canvas.drawRect(0, 0, size, size, bgPaint);
            canvas.drawPath(mnPath, mnPaint);
        } else {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        // Hour hand
        canvas.save();
        canvas.rotate(angleHours, radius, radius);
        if (interactive) {
            canvas.drawPath(handHourPath, shadowPaint);
        }
        canvas.drawPath(handHourPath, interactive ? handHourPaint : ambientPaint);
        canvas.restore();

        // Minute hand
        canvas.save();
        canvas.rotate(angleMinutes, radius, radius);
        canvas.drawPath(handMinutePath, interactive ? shadowPaint : ambientBlackPaint);
        canvas.drawPath(handMinutePath, interactive ? handMinutePaint : ambientPaint);
        canvas.restore();

        // Second hand (+ washer / screw)
        if (interactive) {
            canvas.save();
            canvas.rotate(angleSeconds, radius, radius);
            canvas.drawPath(handSecondPath, handSecondPaint);
            canvas.restore();

            canvas.drawPath(handWasherPath, handWasherPaint);
            canvas.drawPath(handScrewPath, handScrewPaint);
        }
    }

    private void initPaintObjects() {
        CornerPathEffect cornerEffect = new CornerPathEffect(dpToPx(4));

        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);

        mnPaint.setStyle(Paint.Style.FILL);
        mnPaint.setColor(Color.WHITE);
        mnPaint.setAntiAlias(true);
        mnPaint.setShadowLayer(4f, 2f, 2f, Color.GRAY);

        handHourPaint.setStyle(Paint.Style.FILL);
        handHourPaint.setAntiAlias(true);
        handHourPaint.setPathEffect(cornerEffect);

        handMinutePaint.set(handHourPaint);

        handSecondPaint.setAntiAlias(true);
        handSecondPaint.setStyle(Paint.Style.STROKE);
        handSecondPaint.setColor(0xffd5cfdf);
        handSecondPaint.setShadowLayer(1f, 4, 4f, 0xff1a1a1a);
        handSecondPaint.setStrokeWidth(dpToPx(1.25f));
        handSecondPaint.setPathEffect(cornerEffect);
        handSecondPaint.setStrokeJoin(Paint.Join.ROUND);
        handSecondPaint.setStrokeCap(Paint.Cap.ROUND);

        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(0xff1a1a1a);
        shadowPaint.setShadowLayer(4f, 4f, 2f, 0xff1a1a1a);

        ambientPaint.setAntiAlias(true);
        ambientPaint.setStrokeWidth(dpToPx(1));
        ambientPaint.setColor(Color.WHITE);
        ambientPaint.setStyle(Paint.Style.STROKE);

        ambientBlackPaint.set(ambientPaint);
        ambientBlackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        ambientBlackPaint.setColor(Color.BLACK);

        handWasherPaint.setAntiAlias(true);
        handWasherPaint.setStyle(Paint.Style.FILL);
        handWasherPaint.setColor(0xffd5cfdf);
        handWasherPaint.setShadowLayer(4f, 0f, 2f, 0xff1a1a1a);

        handScrewPaint.setAntiAlias(true);
        handScrewPaint.setStyle(Paint.Style.FILL);
        handScrewPaint.setColor(0xff343436);
    }

    private Path createMinutesIndicators(float centerX, float centerY, float radius) {
        Path path = new Path();

        double angleRadians;
        for (int i = 0; i < 60; i++) {
            angleRadians = Math.PI * 2 / 60 * i;
            path.addCircle((float) (centerX + radius * Math.cos(angleRadians)),
                    (float) (centerY + radius * Math.sin(angleRadians)),
                    dpToPx(i % 5 == 0 ? 3f : 1.5f), Path.Direction.CW);
        }
        return path;
    }

    private Path createWatchHandPath(float centerX, float centerY, float handHeight, float circleRadius, float sizeWidth, Paint paint) {
        paint.setShader(new LinearGradient(radius - circleRadius, 0, radius + circleRadius, 0,
                new int[]{0xff878191, 0xffaba6b3, 0xffb9b1c5, 0xffa9a2b3},
                new float[]{0, 0.49f, 0.51f, 1f}, Shader.TileMode.CLAMP));

        Path path = new Path();
        path.moveTo(centerX - circleRadius - sizeWidth, centerY);
        path.lineTo(centerX - circleRadius, centerY);
        path.arcTo(new RectF(centerX - circleRadius, centerY - circleRadius, centerX + circleRadius, centerY + circleRadius), 180f, -180f);
        path.lineTo(centerX + circleRadius + sizeWidth, centerY);
        path.quadTo(centerX, centerY - circleRadius * 2f, centerX + dpToPx(1), centerY - handHeight);
        path.lineTo(centerX - dpToPx(1), centerY - handHeight);
        path.quadTo(centerX, centerY - circleRadius * 2f, centerX - circleRadius - sizeWidth, centerY);

        return path;
    }

    private Path createWatchHandSecondsPath(float centerX, float centerY, float handHeight, float circleRadius) {
        Path path = new Path();
        path.moveTo(centerX, centerY - handHeight);
        path.lineTo(centerX, centerY - circleRadius);
        path.addCircle(centerX, centerY, circleRadius, Path.Direction.CW);
        path.moveTo(centerX, centerY + circleRadius - dpToPx(1));
        path.lineTo(centerX, centerY + circleRadius + dpToPx(8));
        path.lineTo(centerX - dpToPx(2f), centerY + dpToPx(16));
        path.lineTo(centerX - dpToPx(4), centerY + dpToPx(30));
        path.lineTo(centerX + dpToPx(4), centerY + dpToPx(30));
        path.lineTo(centerX + dpToPx(2f), centerY + dpToPx(16));
        path.lineTo(centerX, centerY + circleRadius + dpToPx(8));
        path.close();

        return path;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
