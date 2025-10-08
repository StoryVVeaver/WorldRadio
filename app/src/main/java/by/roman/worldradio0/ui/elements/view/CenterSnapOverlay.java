package by.roman.worldradio0.ui.elements.view;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.MapPoint;

public class CenterSnapOverlay extends Overlay {
    public interface OnSnapListener {
        void onSnap(@Nullable MapPoint snapped);
    }

    private final MapView map;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Point circleScreenPos = null;
    private MapPoint snappedPoint = null;
    private int snapThresholdPx;
    private List<MapPoint> visiblePoints;
    private final OnSnapListener listener;
    private final Drawable centerDrawable;
    private final Drawable centerSnappedDrawable;
    private ValueAnimator snapAnimator = null;
    private long snapAnimationMs = 600;
    private long snapProjectionDelayMs = 200;
    private long snapDelayMs = 700;
    private final Runnable delayedSnapRunnable = this::checkSnap;
    private boolean enableSnap = false;
    private Point defaultIconOffset = new Point(0, 0);

    public CenterSnapOverlay(MapView map,
                             int snapThresholdPx,
                             Drawable centerDrawable,
                             Drawable centerSnappedDrawable,
                             OnSnapListener listener) {
        this.map = map;
        this.snapThresholdPx = Math.max(0, snapThresholdPx);
        this.centerDrawable = centerDrawable;
        this.centerSnappedDrawable = centerSnappedDrawable;
        this.listener = listener;
    }

    public void setSnapEnabled(boolean enabled) {
        this.enableSnap = enabled;
        if (!enabled) {
            mainHandler.removeCallbacks(delayedSnapRunnable);
            if (snapAnimator != null && snapAnimator.isRunning()) snapAnimator.cancel();
            snappedPoint = null;
            circleScreenPos = null;
            if (listener != null) listener.onSnap(null);
            map.invalidate();
        }
    }

    public boolean isSnapEnabled() {
        return enableSnap;
    }

    public void setSnapDelayMs(long ms) {
        this.snapDelayMs = Math.max(0, ms);
    }

    public void setSnapAnimationMs(long ms) {
        this.snapAnimationMs = Math.max(0, ms);
    }

    public void setSnapProjectionDelayMs(long ms) {
        this.snapProjectionDelayMs = Math.max(0, ms);
    }

    public void setDefaultIconOffset(@Nullable Point offset) {
        if (offset == null) defaultIconOffset = new Point(0, 0);
        else defaultIconOffset = new Point(offset.x, offset.y);
    }
    public void feedVisiblePoints(List<MapPoint> points) {
        this.visiblePoints = points;
    }
    public void scheduleDelayedSnap() {
        if (!enableSnap) {
            mainHandler.removeCallbacks(delayedSnapRunnable);
            return;
        }
        mainHandler.removeCallbacks(delayedSnapRunnable);
        mainHandler.postDelayed(delayedSnapRunnable, snapDelayMs);
    }
    public void checkSnap() {
        if (!enableSnap) return;
        mainHandler.post(() -> {
            if (map == null) return;

            int cx = map.getWidth() / 2;
            int cy = map.getHeight() / 2;

            MapPoint nearest = null;
            double bestDist2 = Double.MAX_VALUE;

            if (visiblePoints != null && !visiblePoints.isEmpty()) {
                Point tmp = new Point();
                for (MapPoint p : visiblePoints) {
                    GeoPoint gp = new GeoPoint(p.getLatitude(), p.getLongitude());
                    map.getProjection().toPixels(gp, tmp);
                    double dx = tmp.x - cx;
                    double dy = tmp.y - cy;
                    double d2 = dx * dx + dy * dy;
                    if (d2 < bestDist2) {
                        bestDist2 = d2;
                        nearest = p;
                    }
                }
            }

            if (nearest != null && bestDist2 <= (snapThresholdPx * (double) snapThresholdPx)) {
                if (snappedPoint != null && snappedPoint.getUuid().equals(nearest.getUuid())) {
                    return;
                }
                snapToPoint(nearest, true, defaultIconOffset, false);
            } else {
                // unsnap
                if (snappedPoint != null) {
                    snappedPoint = null;
                    if (snapAnimator != null && snapAnimator.isRunning()) snapAnimator.cancel();
                    circleScreenPos = null;
                    if (listener != null) listener.onSnap(null);
                    map.invalidate();
                }
            }

        });
    }


    public void snapTo(MapPoint p, boolean animate) {
        snapToPoint(p, animate, defaultIconOffset, false);
    }
    public void snapTo(MapPoint p, boolean animate, boolean ignoreFlag) {
        snapToPoint(p, animate, defaultIconOffset, ignoreFlag);
    }

    public void snapToPoint(final MapPoint p, final boolean animate, @Nullable final Point iconOffset, final boolean ignoreSnapFlag) {
        if (p == null) return;
        final Point offset = (iconOffset != null) ? iconOffset : defaultIconOffset;

        mainHandler.post(() -> {
            if (!enableSnap && !ignoreSnapFlag) {
                GeoPoint target = new GeoPoint(p.getLatitude(), p.getLongitude());
                if (animate) {
                    try { map.getController().animateTo(target); } catch (Exception ignored) { map.getController().setCenter(target); }
                } else {
                    try { map.getController().setCenter(target); } catch (Exception ignored) {}
                }
                return;
            }

            if (!ignoreSnapFlag && p.equals(snappedPoint)) return;

            final GeoPoint target = new GeoPoint(p.getLatitude(), p.getLongitude());

            if (animate) {
                try { map.getController().animateTo(target); } catch (Exception ignored) { map.getController().setCenter(target); }

                mainHandler.postDelayed(() -> {
                    if (map == null) return;

                    final Point initialTargetPx = new Point();
                    map.getProjection().toPixels(target, initialTargetPx);
                    initialTargetPx.offset(offset.x, offset.y);

                    final Point startPx = (circleScreenPos != null)
                            ? new Point(circleScreenPos.x, circleScreenPos.y)
                            : new Point(map.getWidth() / 2, map.getHeight() / 2);

                    final Point dynamicTargetPx = new Point(initialTargetPx.x, initialTargetPx.y);

                    if (snapAnimator != null && snapAnimator.isRunning()) snapAnimator.cancel();

                    snapAnimator = ValueAnimator.ofFloat(0f, 1f);
                    snapAnimator.setDuration(snapAnimationMs);

                    snapAnimator.addUpdateListener(animation -> {
                        float f = (float) animation.getAnimatedValue();
                        try {
                            map.getProjection().toPixels(target, dynamicTargetPx);
                            dynamicTargetPx.offset(offset.x, offset.y);
                        } catch (Exception ignored) {
                        }

                        int ix = Math.round(startPx.x + (dynamicTargetPx.x - startPx.x) * f);
                        int iy = Math.round(startPx.y + (dynamicTargetPx.y - startPx.y) * f);
                        circleScreenPos = new Point(ix, iy);
                        map.invalidate();
                    });

                    snapAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(android.animation.Animator animation) {
                            circleScreenPos = new Point(dynamicTargetPx.x, dynamicTargetPx.y);
                            snappedPoint = p;
                            if (listener != null) listener.onSnap(p);
                            map.invalidate();
                        }
                    });

                    snapAnimator.start();

                }, snapProjectionDelayMs);

            } else {
                try { map.getController().setCenter(target); } catch (Exception ignored) {}
                Point targetPx = new Point();
                map.getProjection().toPixels(target, targetPx);
                targetPx.offset(offset.x, offset.y);
                circleScreenPos = new Point(targetPx.x, targetPx.y);
                snappedPoint = p;
                if (listener != null) listener.onSnap(p);
                map.invalidate();
            }
        });
    }
    public void clearSnapped(){
        snappedPoint = null;
    }
    public @Nullable MapPoint getSnappedPoint() { return snappedPoint; }
    @Override
    public void draw(Canvas canvas, MapView osmv, boolean shadow) {
        if (shadow) return;

        Drawable d = (snappedPoint != null && centerSnappedDrawable != null) ? centerSnappedDrawable : centerDrawable;
        if (d == null) return;

        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();
        if (dw <= 0) dw = osmv.getWidth() / 10;
        if (dh <= 0) dh = osmv.getWidth() / 10;

        int cx = osmv.getWidth() / 2;
        int cy = osmv.getHeight() / 2;
        int drawX = (circleScreenPos != null) ? circleScreenPos.x : cx;
        int drawY = (circleScreenPos != null) ? circleScreenPos.y : cy;

        int left = drawX - dw / 2;
        int top = drawY - dh / 2;
        int right = left + dw;
        int bottom = top + dh;

        d.setBounds(left, top, right, bottom);
        d.draw(canvas);

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        return false;
    }
}
