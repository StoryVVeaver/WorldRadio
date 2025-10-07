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

/**

 * CenterSnapOverlay — рисует центральный указатель (drawable) и умеет:
 * * искать ближайшую видимую точку (feedVisiblePoints)
 * * отложенно (debounced) примагничиваться к ней через scheduleDelayedSnap()
 * * snapTo(p, animate[, iconOffset]) — принудительно примагнитить
 *
 * Поддерживает включение/выключение магнита через setSnapEnabled(boolean).
 */
public class CenterSnapOverlay extends Overlay {
    public interface OnSnapListener {
        void onSnap(@Nullable MapPoint snapped);
    }

    private final MapView map;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // экранная позиция курсора (если null — рисуем в центре экрана)
    private Point circleScreenPos = null;

    // текущая привязка
    private MapPoint snappedPoint = null;

    // порог в пикселях (экранных) для автопримагничивания (используется для поиска кандидата)
    private int snapThresholdPx;

    // видимые точки, которые передаёт фрагмент/кластерер
    private List<MapPoint> visiblePoints;
    private final OnSnapListener listener;

    // drawables для нормального и snapped состояния
    private final Drawable centerDrawable;
    private final Drawable centerSnappedDrawable;

    // анимация перемещения курсора
    private ValueAnimator snapAnimator = null;

    // параметры анимации / таймингов
    private long snapAnimationMs = 280;       // длительность анимации курсора
    private long snapProjectionDelayMs = 200; // задержка перед взятием projection после animateTo

    // задержка перед запуском checkSnap (debounce)
    private long snapDelayMs = 700;
    private final Runnable delayedSnapRunnable = this::checkSnap;

    // флаг включения магнита (по умолчанию выключен — включается вручную из MapFragment при первом касании)
    private boolean enableSnap = false;

    // default icon offset (если нужно центрировать курсор по центру иконки маркера)
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

    // ---------------- public API ----------------

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

    /**

     * Установить смещение иконки (экранные px), которое нужно применить к projection.toPixels(point).
     * Например: (0, -iconHeight/2) для anchor=(center,bottom).
     */
    public void setDefaultIconOffset(@Nullable Point offset) {
        if (offset == null) defaultIconOffset = new Point(0, 0);
        else defaultIconOffset = new Point(offset.x, offset.y);
    }

    /**

     * Передать видимые точки (только те, которые реально видимы на экране).
     */
    public void feedVisiblePoints(List<MapPoint> points) {
        this.visiblePoints = points;
    }

    /**

     * Вызывать при скролле/зуме с дебаунсом: ставит отложенный запуск checkSnap().
     */
    public void scheduleDelayedSnap() {
        if (!enableSnap) {
            mainHandler.removeCallbacks(delayedSnapRunnable);
            return;
        }
        mainHandler.removeCallbacks(delayedSnapRunnable);
        mainHandler.postDelayed(delayedSnapRunnable, snapDelayMs);
    }

    /**

     * Немедленная проверка ближайшей видимой точки и snap при соответствии порогу.
     * Может вызываться из UI-потока или через Handler.
     */
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
                    // уже привязаны к той же точке
                    return;
                }
                // анимированно центрируем карту и курсор (используем defaultIconOffset)
                snapTo(nearest, true, defaultIconOffset);
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

    /**

     * Прокси-метод — без offset, использует defaultIconOffset.
     */
    public void snapTo(MapPoint p, boolean animate) {
        snapTo(p, animate, defaultIconOffset);
    }

    /**

     * Принудительно примагнитить центр к точке с учётом пиксельного смещения (iconOffset).
     * iconOffset может быть null.
     */
    public void snapTo(MapPoint p, boolean animate, @Nullable Point iconOffset) {
        if (p == null) return;
        final Point offset = (iconOffset != null) ? iconOffset : defaultIconOffset;

        mainHandler.post(() -> {
            // если магнит отключен — просто центрируем карту и не меняем состояние snappedPoint
            if (!enableSnap) {
                GeoPoint target = new GeoPoint(p.getLatitude(), p.getLongitude());
                if (animate) {
                    try { map.getController().animateTo(target); } catch (Exception ignored) { map.getController().setCenter(target); }
                } else {
                    try { map.getController().setCenter(target); } catch (Exception ignored) {}
                }
                return;
            }

            if (p.equals(snappedPoint)) return; // уже привязаны

            GeoPoint target = new GeoPoint(p.getLatitude(), p.getLongitude());

            if (animate) {
                try { map.getController().animateTo(target); } catch (Exception ignored) { map.getController().setCenter(target); }

                // подождём немного, чтобы projection обновился (adjustable delay)
                mainHandler.postDelayed(() -> {
                    // целевая позиция в пикселях
                    Point targetPx = new Point();
                    map.getProjection().toPixels(target, targetPx);
                    targetPx.offset(offset.x, offset.y);

                    // стартовая позиция для анимации курсора (если уже есть, иначе центр экрана)
                    Point startPx = (circleScreenPos != null) ? new Point(circleScreenPos.x, circleScreenPos.y)
                            : new Point(map.getWidth() / 2, map.getHeight() / 2);

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
                            // финализируем точную позицию
                            map.getProjection().toPixels(target, targetPx);
                            targetPx.offset(offset.x, offset.y);
                            circleScreenPos = new Point(targetPx.x, targetPx.y);
                            snappedPoint = p;
                            if (listener != null) listener.onSnap(p);
                            map.invalidate();
                        }
                    });
                    snapAnimator.start();

                }, snapProjectionDelayMs);
            } else {
                // мгновенное центрирование
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
