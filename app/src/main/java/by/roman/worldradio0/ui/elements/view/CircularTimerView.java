package by.roman.worldradio0.ui.elements.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircularTimerView extends View {
    private long maxTimeMillis = 10 * 60_000L;
    private long currentTimeMillis = maxTimeMillis;
    private float strokeWidth = 20f;
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint indicatorPaint;
    private Paint textPaint;
    private RectF oval = new RectF();

    public CircularTimerView(Context context) {
        super(context);
        init();
    }

    public CircularTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Style.STROKE);
        backgroundPaint.setColor(Color.GRAY);
        backgroundPaint.setStrokeWidth(strokeWidth);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Style.STROKE);
        progressPaint.setStrokeCap(Cap.ROUND);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setShadowLayer(10f, 0f, 0f, Color.BLUE);

        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setStyle(Style.FILL);
        indicatorPaint.setColor(Color.WHITE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Align.CENTER);
    }

    public void setMaxTimeMillis(long maxTimeMillis) {
        this.maxTimeMillis = maxTimeMillis;
        invalidate();
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
        invalidate();
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    private void setupGradient(float width, float height) {
        float centerX = width / 2f;
        float centerY = height / 2f;

        SweepGradient shader = new SweepGradient(
                centerX, centerY,
                new int[] {
                        Color.parseColor("#4E9EFF"),
                        Color.parseColor("#8AC5FF")
                },
                null
        );
        progressPaint.setShader(shader);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = strokeWidth / 2f;
        oval.set(padding, padding, w - padding, h - padding);
        setupGradient(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - (strokeWidth / 2f);

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        float fraction = (float) currentTimeMillis / (float) maxTimeMillis;
        float sweepAngle = 360f * fraction;

        canvas.drawArc(oval, -90f, sweepAngle, false, progressPaint);

        double endAngle = Math.toRadians(-90 + sweepAngle);
        float indicatorX = (float) (centerX + radius * Math.cos(endAngle));
        float indicatorY = (float) (centerY + radius * Math.sin(endAngle));
        canvas.drawCircle(indicatorX, indicatorY, strokeWidth / 2f, indicatorPaint);

        long totalSeconds = currentTimeMillis / 1000;
        long hh = totalSeconds / 3600;
        long mm = (totalSeconds % 3600) / 60;
        long ss = totalSeconds % 60;

        String timeString = String.format("%02d:%02d:%02d", hh, mm, ss);

        float textY = centerY - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(timeString, centerX, textY, textPaint);

    }
}
