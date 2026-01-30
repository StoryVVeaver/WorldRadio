package by.roman.worldradio0.ui.elements.view;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.MapPoint;

public class CenterSnapOverlay extends Overlay {

    private static final int SNAP_DISTANCE_PX = 70;
    private static final int ESCAPE_DISTANCE_PX = 100;
    private static final long ANIMATE_DURATION_MS = 1000;
    private static final long SNAP_INTERVAL_MS = 500;

    private final Handler snapHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSnap;
    public boolean isAnimating = false;

    public interface OnSnappedCallback {
        void onSnapped(MapPoint point);
    }
    private final ImageView point;
    private final MapView map;
    private final OnSnappedCallback callback;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean snapEnabled = true;
    private boolean requireFirstTouch = false;
    private boolean userTouched = false;

    private Point iconOffset = new Point(0, 0);

    private List<MapPoint> visiblePoints = new ArrayList<>();
    private MapPoint snappedPoint;

    private final Runnable delayedSnapRunnable = this::evaluateSnap;

    public CenterSnapOverlay(MapView map, int unused, ImageView point, OnSnappedCallback callback) {
        this.map = map;
        this.callback = callback;
        this.point = point;
    }

    public void setSnapEnabled(boolean enabled) {
        this.snapEnabled = enabled;
        if (!enabled) clearSnapped();
    }

    public boolean isSnapEnabled() {
        return snapEnabled;
    }

    public void setRequireFirstTouch(boolean value) {
        this.requireFirstTouch = value;
    }

    public void setDefaultIconOffset(Point offset) {
        this.iconOffset = offset;
    }

    public void feedVisiblePoints(List<MapPoint> points) {
        visiblePoints = points != null ? points : new ArrayList<>();
    }

    public MapPoint getSnappedPoint() {
        return snappedPoint;
    }

    public void clearSnapped() {
        snappedPoint = null;
        point.setImageTintList(ColorStateList.valueOf(Color.BLACK));
    }

    public void notifyUserInteraction() {
        userTouched = true;
    }

    public void scheduleDelayedSnap() {
        handler.removeCallbacks(delayedSnapRunnable);
        handler.postDelayed(delayedSnapRunnable, 200);
    }

    public void snapTo(MapPoint target, boolean animate, boolean notify) {
        if (target == null) return;

        snappedPoint = target;

        if (pendingSnap != null) {
            snapHandler.removeCallbacks(pendingSnap);
        }

        pendingSnap = () -> executeSnap(target, animate, notify);
        snapHandler.postDelayed(pendingSnap, SNAP_INTERVAL_MS);
    }

    private void evaluateSnap() {
        if (!snapEnabled) return;
        if (requireFirstTouch && !userTouched) return;
        if (visiblePoints.isEmpty()) return;

        Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);

        if (snappedPoint == null) {
            MapPoint nearest = findNearest(center, null);
            if (nearest != null) snapTo(nearest, true, true);
            return;
        }

        double currentDist = distancePx(snappedPoint, center);

        if (currentDist > ESCAPE_DISTANCE_PX) {
            MapPoint nearest = findNearest(center, snappedPoint);
            if (nearest != null) {
                snapTo(nearest, true, true);
            } else {
                clearSnapped();
                if (callback != null) callback.onSnapped(null);
            }
            return;
        }

        MapPoint closer = findCloserThanCurrent(center, currentDist);
        if (closer != null) {
            snapTo(closer, true, true);
        } else {
            snapTo(snappedPoint, true, false);
        }
    }

    private MapPoint findNearest(Point center, MapPoint exclude) {
        double best = Double.MAX_VALUE;
        MapPoint bestPoint = null;

        for (MapPoint p : visiblePoints) {
            if (exclude != null && exclude.getUuid().equals(p.getUuid())) continue;

            double d = distancePx(p, center);
            if (d < best && d <= SNAP_DISTANCE_PX) {
                best = d;
                bestPoint = p;
            }
        }
        return bestPoint;
    }

    private MapPoint findCloserThanCurrent(Point center, double currentDist) {
        for (MapPoint p : visiblePoints) {
            if (snappedPoint != null && snappedPoint.getUuid().equals(p.getUuid())) continue;
            double d = distancePx(p, center);
            if (d < currentDist) return p;
        }
        return null;
    }

    private double distancePx(MapPoint p, Point center) {
        Point tmp = new Point();
        GeoPoint gp = new GeoPoint(p.getLatitude(), p.getLongitude());
        map.getProjection().toPixels(gp, tmp);

        double dx = tmp.x + iconOffset.x - center.x;
        double dy = tmp.y + iconOffset.y - center.y;
        return Math.hypot(dx, dy);
    }

    @SuppressLint("ResourceAsColor")
    private void executeSnap(MapPoint target, boolean animate, boolean notify){
        if (target == null) return;

        GeoPoint gp = new GeoPoint(target.getLatitude(), target.getLongitude());
        if (animate) {
            isAnimating = true;
            map.getController().animateTo(gp);
            snapHandler.postDelayed(() -> isAnimating = false, ANIMATE_DURATION_MS);
        } else {
            map.getController().setCenter(gp);
        }

        if (notify && callback != null) {
            callback.onSnapped(target);
            point.setImageTintList(ColorStateList.valueOf(Color.parseColor("#B10E0E")));
        }
    }

    @Override
    public void draw(Canvas c, MapView osmv, boolean shadow) {

    }
}
