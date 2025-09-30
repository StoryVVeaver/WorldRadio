package by.roman.worldradio0.ui.elements.view;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.MapPoint;

/**
 * CenterSnapOverlay — рисует центральный указатель (drawable) и умеет:
 *  - искать ближайшую видимую точку (feedVisiblePoints)
 *  - если ближайшая точка ближе snapThresholdPx, "примагничиваться" к ней:
 *      - анимированно центрирует карту на точку (animateTo)
 *      - анимированно двигает курсор так, чтобы его центр оказался ровно над точкой
 *      - переключает drawable (centerSnappedDrawable)
 *      - вызывает listener.onSnap(MapPoint)
 */
public class CenterSnapOverlay extends Overlay {
    public interface OnSnapListener {
        void onSnap(@Nullable MapPoint snapped);
    }

    private final MapView map;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // если null — рисуем drawable ровно в центре экрана
    private Point circleScreenPos = null;

    // текущая привязка (если null — не привязаны)
    private MapPoint snappedPoint = null;

    // порог в пикселях (экранных) для автопримагничивания
    private int snapThresholdPx;

    private List<MapPoint> visiblePoints;
    private final OnSnapListener listener;

    private final Drawable centerDrawable;
    private final Drawable centerSnappedDrawable;

    // анимация перемещения курсора
    private ValueAnimator snapAnimator = null;

    // параметры анимации / таймингов
    private final Runnable delayedSnapRunnable = this::checkSnap;
    private long snapDelayMs = 300;
    private long snapAnimationMs = 150;       // длительность анимации курсора
    private long snapProjectionDelayMs = 600; // задержка перед взятием projection после animateTo

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

    /** Установить новый порог в пикселях. */
    public void setSnapThresholdPx(int px) {
        this.snapThresholdPx = Math.max(0, px);
    }

    /** Установить длительность анимации курсора (ms). */
    public void setSnapAnimationMs(long ms) {
        this.snapAnimationMs = Math.max(0, ms);
    }

    /** Установить задержку перед вычислением projection после animateTo (ms). */
    public void setSnapProjectionDelayMs(long ms) {
        this.snapProjectionDelayMs = Math.max(0, ms);
    }

    /** Передать видимые точки (лучше — только видимые из кластерера). */
    public void feedVisiblePoints(List<MapPoint> points) {
        this.visiblePoints = points;
    }
    public void scheduleDelayedSnap() {
        mainHandler.removeCallbacks(delayedSnapRunnable);
        mainHandler.postDelayed(delayedSnapRunnable, snapDelayMs);
    }

    /**
     * Проверяет ближайшую видимую точку. Если она ближе порога и это новая точка —
     * анимированно центрирует карту на ней и вызывает listener.onSnap().
     *
     * Вызывать по окончании скролла/завершении зума (debounced).
     */
    public void checkSnap() {
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
                    // уже привязаны к той же точке — ничего не делаем
                    return;
                }
                // анимированно центрируем карту и курсор
                snapTo(nearest, true);
            } else {
                // unsnap (вернуть центр-пиктограмму в центр)
                if (snappedPoint != null) {
                    snappedPoint = null;
                    // прервём анимацию если есть
                    if (snapAnimator != null && snapAnimator.isRunning()) snapAnimator.cancel();
                    circleScreenPos = null;
                    if (listener != null) listener.onSnap(null);
                    map.invalidate();
                }
            }
        });
    }

    /**
     * Принудительно примагнитить центр к точке.
     * @param p точка
     * @param animate если true — анимированно; иначе — сразу
     */
    public void snapTo(MapPoint p, boolean animate) {
        if (p == null) return;

        mainHandler.post(() -> {
            if (p.equals(snappedPoint)) return; // уже привязаны

            GeoPoint target = new GeoPoint(p.getLatitude(), p.getLongitude());

            if (animate) {
                // 1️⃣ Анимируем карту к точке
                try {
                    map.getController().animateTo(target);
                } catch (Exception ignored) {
                    map.getController().setCenter(target);
                }

                // 2️⃣ Через задержку берём координаты точки на экране после движения карты
                mainHandler.postDelayed(() -> {
                    Point targetPx = new Point();
                    map.getProjection().toPixels(target, targetPx); // точное положение станции на экране

                    // 3️⃣ Стартовая позиция курсора
                    Point startPx = (circleScreenPos != null) ? new Point(circleScreenPos.x, circleScreenPos.y)
                            : new Point(map.getWidth() / 2, map.getHeight() / 2);

                    // 4️⃣ Анимируем курсор к новой позиции
                    if (snapAnimator != null && snapAnimator.isRunning()) snapAnimator.cancel();
                    snapAnimator = ValueAnimator.ofFloat(0f, 1f);
                    snapAnimator.setDuration(snapAnimationMs);
                    snapAnimator.addUpdateListener(animation -> {
                        float f = (float) animation.getAnimatedValue();
                        int ix = Math.round(startPx.x + (targetPx.x - startPx.x) * f);
                        int iy = Math.round(startPx.y + (targetPx.y - startPx.y) * f);
                        circleScreenPos = new Point(ix, iy);
                        map.invalidate();
                    });
                    snapAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(android.animation.Animator animation) {
                            // 5️⃣ Финальная фиксация курсора точно на точке
                            circleScreenPos = new Point(targetPx.x, targetPx.y);
                            snappedPoint = p;
                            if (listener != null) listener.onSnap(p);
                            map.invalidate();
                        }
                    });
                    snapAnimator.start();

                }, snapProjectionDelayMs); // задержка, чтобы карта успела завершить анимацию

            } else {
                // Немедленно центрируем карту и ставим курсор
                try {
                    map.getController().setCenter(target);
                } catch (Exception ignored) {}

                Point targetPx = new Point();
                map.getProjection().toPixels(target, targetPx);
                circleScreenPos = new Point(targetPx.x, targetPx.y);
                snappedPoint = p;
                if (listener != null) listener.onSnap(p);
                map.invalidate();
            }
        });
    }



    /** Снять привязку и вернуть центр в исходное состояние. */
    public void clearSnap() {
        mainHandler.post(() -> {
            snappedPoint = null;
            if (snapAnimator != null && snapAnimator.isRunning()) snapAnimator.cancel();
            circleScreenPos = null;
            if (listener != null) listener.onSnap(null);
            map.invalidate();
        });
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
