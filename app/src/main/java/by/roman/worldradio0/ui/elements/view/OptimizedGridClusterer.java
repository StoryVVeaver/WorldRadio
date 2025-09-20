package by.roman.worldradio0.ui.elements.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.MapPoint;

/**
 * OptimizedGridClusterer — высокопроизводительный кластеризатор для osmdroid (без "взрыва").
 *
 * Подход:
 * 1) Фильтрация точек по видимому BoundingBox (быстро, O(n)).
 * 2) Преобразование lat/lon -> мировые пиксели (WebMercator) на заданном zoom.
 * 3) Группировка в хэш-клетки (grid) по пиксельным координатам — O(n).
 * 4) Создание только итоговых Marker'ов (один на кластер/точку) на UI-потоке.
 * 5) Кэширование и повторное использование иконок кластеров.
 * 6) Кластеризация выполняется асинхронно (Executor), результаты применяются на UI-потоке.
 *
 * Как использовать: в вашем MapFragment создайте экземпляр OptimizedGridClusterer(
 *     requireContext(), mapView), затем при изменении данных вызовите setItems(list);
 * и при перемещении/зуме карты вызывайте clusterAsync(). Не забудьте вызвать shutdown()
 * в onDestroyView().
 */
public class OptimizedGridClusterer {
    private final Context context;
    private final MapView map;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> runningTask;

    // Исходные точки (легковесно хранить только lat/lon и мета)
    private List<MapPoint> items = new ArrayList<>();

    // Результирующие маркеры, которые добавлены на карту (чтобы удалять при обновлении)
    private final List<Marker> clusterMarkers = new ArrayList<>();

    // pixel cell size для агрегации (можно варьировать в зависимости от желаемой плотности)
    private int cellSizePx = 100;

    // Кеш иконок для кластеров (ограниченный по памяти)
    private final LruCache<Integer, Drawable> iconCache = new LruCache<>(256);

    public OptimizedGridClusterer(Context context, MapView map) {
        this.context = context.getApplicationContext();
        this.map = map;
    }

    public void setCellSizePx(int px) {
        this.cellSizePx = Math.max(8, px);
    }

    /**
     * Заменить набор точек (без немедленной кластеризации).
     */
    public synchronized void setItems(List<MapPoint> newItems) {
        if (newItems == null) {
            items = new ArrayList<>();
        } else {
            items = new ArrayList<>(newItems);
        }
    }

    /**
     * Быстрое добавление одной точки (не потокобезопасно для массового добавления).
     */
    public synchronized void addItem(MapPoint p) {
        items.add(p);
    }

    /**
     * Удалить все исходные точки и маркеры с карты.
     */
    public synchronized void clear() {
        items.clear();
        removeClusterMarkersFromMap();
    }

    private void removeClusterMarkersFromMap() {
        if (clusterMarkers.isEmpty()) return;
        mainHandler.post(() -> {
            for (Marker m : clusterMarkers) {
                map.getOverlays().remove(m);
            }
            clusterMarkers.clear();
            map.invalidate();
        });
    }

    /**
     * Асинхронно запускает кластеризацию для текущего состояния карты (видимая область и zoom)
     * Результат применяется на UI-потоке.
     */
    public void clusterAsync() {
        // отменяем предыдущую задачу, если есть
        if (runningTask != null && !runningTask.isDone()) {
            runningTask.cancel(true);
        }

        // Забираем текущий bbox и zoom на UI-потоке (они нужны для корректных вычислений)
        BoundingBox bbox = map.getBoundingBox();
        double zoom = map.getZoomLevelDouble();

        // Копируем ссылку на список точек под синхро-блоком
        final List<MapPoint> pointsSnapshot;
        synchronized (this) {
            pointsSnapshot = new ArrayList<>(this.items);
        }

        runningTask = executor.submit(() -> {
            try {
                // Подготовка: вычисляем мировые размеры
                double worldSize = 256.0 * Math.pow(2.0, zoom);

                // Хеш-таблица для группировки клеток
                Map<Long, ClusterAccumulator> grid = new HashMap<>((int) (pointsSnapshot.size() / 3 + 16));

                // 1) Фильтрация по bbox + преобразование координат в мировые пиксели
                for (MapPoint p : pointsSnapshot) {
                    if (Thread.currentThread().isInterrupted()) return; // graceful cancel

                    double lat = p.getLatitude();
                    double lon = p.getLongitude();

                    // Обрезаем по видимому bbox — это очень быстрый фильтр
                    if (!isPointInBoundingBox(lat, lon, bbox)) continue;

                    // Преобразуем lat/lon -> мировые пиксели (WebMercator projection)
                    // x: [0..worldSize), y: [0..worldSize)
                    double x = lonToWorldX(lon, worldSize);
                    double y = latToWorldY(lat, worldSize);

                    int cellX = (int) Math.floor(x / cellSizePx);
                    int cellY = (int) Math.floor(y / cellSizePx);

                    long key = packIntsToLong(cellX, cellY);
                    ClusterAccumulator acc = grid.get(key);
                    if (acc == null) {
                        acc = new ClusterAccumulator();
                        acc.sumLat = lat;
                        acc.sumLon = lon;
                        acc.count = 1;
                        acc.sample = p;
                        grid.put(key, acc);
                    } else {
                        acc.sumLat += lat;
                        acc.sumLon += lon;
                        acc.count++;
                    }

                }

                if (Thread.currentThread().isInterrupted()) return;

                // 2) Создадим результат — список кластеров
                List<Cluster> clusters = new ArrayList<>(grid.size());
                for (ClusterAccumulator acc : grid.values()) {
                    if (acc == null) continue;
                    int count = acc.count;
                    double avgLat = acc.sumLat / count;
                    double avgLon = acc.sumLon / count;
                    clusters.add(new Cluster(avgLat, avgLon, count, acc.sample));
                }

                // 3) Передаём результат на UI-поток — создаём Marker'ы и добавляем их на карту
                mainHandler.post(() -> {
                    // удаляем старые
                    for (Marker m : clusterMarkers) {
                        map.getOverlays().remove(m);
                    }
                    clusterMarkers.clear();

                    double currentZoom = map.getZoomLevelDouble();

                    for (Cluster c : clusters) {
                        GeoPoint pos = new GeoPoint(c.lat, c.lon);

                        // Если кластер маленький (count==1) — обычный маркер
                        if (c.count == 1) {
                            Marker marker = new Marker(map);
                            marker.setPosition(pos);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setIcon(AppCompatResources.getDrawable(context, R.drawable.map_point));
                            marker.setOnMarkerClickListener((m, mapView) -> { m.showInfoWindow(); return true; });
                            clusterMarkers.add(marker);
                            map.getOverlays().add(marker);
                            continue;
                        }

                        // обычный агрегированный маркер
                        Marker marker = new Marker(map);
                        marker.setPosition(pos);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        Drawable clusterIcon = getClusterDrawableForCount(c.count);
                        marker.setIcon(clusterIcon);
                        marker.setOnMarkerClickListener((m, mapView) -> {
                            mapControllerZoomToPoint(pos, 2.0);
                            return true;
                        });
                        clusterMarkers.add(marker);
                        map.getOverlays().add(marker);
                    }

                    map.invalidate();
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mapControllerZoomToPoint(GeoPoint p, double zoomDelta) {
        // Выполняем плавное центрирование и увеличение
        // Используем UI-поток (вызывается из onMarkerClick, уже на UI)
        try {
            double newZoom = map.getZoomLevelDouble() + zoomDelta;
            map.getController().setZoom(newZoom);
            map.getController().setCenter(p);
        } catch (Exception ignored) {}
    }

    private Drawable getClusterDrawableForCount(int count) {
        int key = getBucketKey(count);
        Drawable d = iconCache.get(key);
        if (d != null) return d;

        Drawable created = createClusterDrawable(count);
        iconCache.put(key, created);
        return created;
    }

    private int getBucketKey(int count) {
        if (count < 10) return count;
        if (count < 100) return (count / 10) * 10; // 10,20,...90
        if (count < 1000) return (count / 100) * 100; // 100,200,...900
        return 1000;
    }

    private Drawable createClusterDrawable(int count) {
        int base = 32;
        int size = base + (int) (Math.min(1.0, Math.log10(count)) * 48); // увеличиваем чуть-чуть
        Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        Paint circle = new Paint(Paint.ANTI_ALIAS_FLAG);
        circle.setStyle(Paint.Style.FILL);
        circle.setARGB(255, 58, 123, 213);

        float cx = size / 2f;
        float cy = size / 2f;
        float radius = size / 2f;
        canvas.drawCircle(cx, cy, radius, circle);

        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setColor(0xFFFFFFFF);
        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        text.setTextSize(size / 2.6f);

        String s = String.valueOf(count);
        Rect bounds = new Rect();
        text.getTextBounds(s, 0, s.length(), bounds);
        float tx = cx - bounds.width() / 2f - bounds.left;
        float ty = cy + bounds.height() / 2f - bounds.bottom / 2f;
        canvas.drawText(s, tx, ty, text);

        return new BitmapDrawable(context.getResources(), bm);
    }

    private static boolean isPointInBoundingBox(double lat, double lon, BoundingBox bbox) {
        // BoundingBox содержит верхнюю/нижнюю и левую/правую границы
        // Используем простую проверку (bbox уже в градусах)
        return lat <= bbox.getLatNorth() && lat >= bbox.getLatSouth()
                && lon >= bbox.getLonWest() && lon <= bbox.getLonEast();
    }

    private static long packIntsToLong(int a, int b) {
        return (((long) a) << 32) | (b & 0xffffffffL);
    }

    // ----- WebMercator helpers (lat/lon -> world pixel coords for a given "worldSize") -----
    private static double lonToWorldX(double lon, double worldSize) {
        return (lon + 180.0) / 360.0 * worldSize;
    }

    private static double latToWorldY(double lat, double worldSize) {
        double sinLat = Math.sin(lat * Math.PI / 180.0);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
        return y * worldSize;
    }

    /**
     * Полное завершение воркера, удалить маркеры — вызывать в onDestroyView().
     */
    public void shutdown() {
        try {
            runningTask = null;
            executor.shutdownNow();
        } catch (Exception ignored) {}
        removeClusterMarkersFromMap();
    }

    // Вспомогательные классы
    private static class ClusterAccumulator {
        double sumLat;
        double sumLon;
        int count;
        MapPoint sample; // пример точки
    }

    private static class Cluster {
        double lat;
        double lon;
        int count;
        MapPoint sample;

        Cluster(double lat, double lon, int count, MapPoint sample) {
            this.lat = lat;
            this.lon = lon;
            this.count = count;
            this.sample = sample;
        }
    }
}
