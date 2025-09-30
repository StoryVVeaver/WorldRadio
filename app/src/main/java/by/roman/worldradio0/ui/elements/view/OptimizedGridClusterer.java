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
 * OptimizedGridClusterer — гридовая кластеризация, хранит все точки локально.
 */
public class OptimizedGridClusterer {
    public interface OnClusterMarkerClickListener {
        void onClusterMarkerClicked(MapPoint point);
    }

    private final Context context;
    private final MapView map;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> runningTask;

    private final Map<String, Marker> markerByUuid = new HashMap<>();
    private final Map<String, Drawable> originalIconByUuid = new HashMap<>();

    // отложенная подсветка (если подсветили до создания маркера)
    private String pendingHighlightUuid = null;
    private Drawable pendingHighlightDrawable = null;

    // все точки (весь мир) — загружаются один раз
    private List<MapPoint> items = new ArrayList<>();

    private final List<Marker> clusterMarkers = new ArrayList<>();

    private int cellSizePx = 100;
    private final LruCache<Integer, Drawable> iconCache = new LruCache<>(256);

    private OnClusterMarkerClickListener clickListener;

    public OptimizedGridClusterer(Context context, MapView map) {
        this.context = context.getApplicationContext();
        this.map = map;
    }

    public void setOnClusterMarkerClickListener(OnClusterMarkerClickListener l) {
        this.clickListener = l;
    }

    public void setCellSizePx(int px) {
        this.cellSizePx = Math.max(8, px);
    }

    public synchronized void setItems(List<MapPoint> newItems) {
        if (newItems == null) this.items = new ArrayList<>();
        else this.items = new ArrayList<>(newItems);
    }

    public synchronized void addItem(MapPoint p) {
        items.add(p);
    }

    public synchronized void clear() {
        items.clear();
        removeClusterMarkersFromMap();
    }

    private void removeClusterMarkersFromMap() {
        mainHandler.post(() -> {
            for (Marker m : clusterMarkers) {
                try { map.getOverlays().remove(m); } catch (Exception ignored) {}
            }
            clusterMarkers.clear();
            markerByUuid.clear();
            originalIconByUuid.clear();
            map.invalidate();
        });
    }

    public void clusterAsync() {
        if (runningTask != null && !runningTask.isDone()) runningTask.cancel(true);

        final BoundingBox bbox = map.getBoundingBox();
        final double zoom = map.getZoomLevelDouble();

        final List<MapPoint> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(this.items);
        }

        runningTask = executor.submit(() -> {
            try {
                double worldSize = 256.0 * Math.pow(2.0, zoom);
                Map<Long, ClusterAccumulator> grid = new HashMap<>((int) (snapshot.size() / 3 + 16));

                for (MapPoint p : snapshot) {
                    if (Thread.currentThread().isInterrupted()) return;

                    double lat = p.getLatitude();
                    double lon = p.getLongitude();

                    if (!isPointInBoundingBox(lat, lon, bbox)) continue;

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

                List<Cluster> clusters = new ArrayList<>(grid.size());
                for (ClusterAccumulator acc : grid.values()) {
                    if (acc == null) continue;
                    int count = acc.count;
                    double avgLat = acc.sumLat / count;
                    double avgLon = acc.sumLon / count;
                    clusters.add(new Cluster(avgLat, avgLon, count, acc.sample));
                }

                mainHandler.post(() -> {
                    // remove old
                    for (Marker m : clusterMarkers) {
                        try { map.getOverlays().remove(m); } catch (Exception ignored) {}
                    }
                    clusterMarkers.clear();
                    markerByUuid.clear();
                    originalIconByUuid.clear();

                    double currentZoom = map.getZoomLevelDouble();

                    for (Cluster c : clusters) {
                        GeoPoint pos = new GeoPoint(c.lat, c.lon);

                        if (c.count == 1) {
                            // одиночная точка — ставим маркер, привязываем MapPoint и клики будут примагничивать + запускать playback
                            Marker marker = new Marker(map);
                            marker.setPosition(pos);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

                            Drawable singleDrawable = getSinglePointDrawable();
                            if (singleDrawable != null) marker.setIcon(singleDrawable);

                            MapPoint sample = c.sample;
                            if (sample != null) {
                                marker.setRelatedObject(sample); // ONLY for single points
                                String uuid = sample.getUuid();
                                if (uuid != null) {
                                    markerByUuid.put(uuid, marker);
                                    originalIconByUuid.put(uuid, marker.getIcon());
                                }
                            }

                            // click => snap/play
                            marker.setOnMarkerClickListener((m, mapView) -> {
                                Object ro = m.getRelatedObject();
                                if (ro instanceof MapPoint) {
                                    MapPoint mp = (MapPoint) ro;
                                    // делегируем наружу: MapFragment через clickListener сделает centerSnap.snapTo(mp)
                                    if (clickListener != null) clickListener.onClusterMarkerClicked(mp);
                                }
                                return true; // consume — не показывать info window
                            });

                            clusterMarkers.add(marker);
                            map.getOverlays().add(marker);
                            continue;
                        }

                        // АГРЕГИРОВАННЫЙ КЛАСТЕР (count > 1)
                        Marker marker = new Marker(map);
                        marker.setPosition(pos);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        Drawable clusterIcon = getClusterDrawableForCount(c.count);
                        if (clusterIcon != null) marker.setIcon(clusterIcon);

                        // НЕ устанавливем marker.setRelatedObject(sample) — клики по кластеру не должны запускать воспроизведение
                        // но можно сохранить mapping uuid -> marker для highlight (по representative sample)
                        MapPoint sample = c.sample;
                        if (sample != null) {
                            String uuid = sample.getUuid();
                            if (uuid != null) {
                                markerByUuid.put(uuid, marker);
                                originalIconByUuid.put(uuid, marker.getIcon());
                            }
                        }

                        // click => zoom into cluster center (не snap/play)
                        marker.setOnMarkerClickListener((m, mapView) -> {
                            mapControllerZoomToPoint(pos, 2.0);
                            return true;
                        });

                        clusterMarkers.add(marker);
                        map.getOverlays().add(marker);
                    }


                    // apply pending highlight if present
                    if (pendingHighlightUuid != null && pendingHighlightDrawable != null) {
                        Marker pending = markerByUuid.get(pendingHighlightUuid);
                        if (pending != null) {
                            // save original if missing
                            if (!originalIconByUuid.containsKey(pendingHighlightUuid)) {
                                originalIconByUuid.put(pendingHighlightUuid, pending.getIcon());
                            }
                            pending.setIcon(pendingHighlightDrawable);
                        }
                    }

                    map.invalidate();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /** Поставить подсветку (highlightDrawable) на маркер по uuid. Выполняется на UI-потоке. */
    public void highlightMarkerByUuid(final String uuid, final Drawable highlightDrawable) {
        if (uuid == null) return;
        // Сохраним pending — чтобы при пересоздании маркеров highlight не терялся
        pendingHighlightUuid = uuid;
        pendingHighlightDrawable = highlightDrawable;

        mainHandler.post(() -> {
            Marker m = markerByUuid.get(uuid);
            if (m != null) {
                // если оригинал ещё не сохранён — сохраним
                if (!originalIconByUuid.containsKey(uuid)) {
                    originalIconByUuid.put(uuid, m.getIcon());
                }
                if (highlightDrawable != null) m.setIcon(highlightDrawable);
                map.invalidate();
            }
            // если маркера сейчас нет, pending будет применён в конце clusterAsync()
        });
    }

    /** Убрать подсветку и вернуть оригинальную иконку. */
    public void clearHighlightByUuid(final String uuid) {
        if (uuid == null) return;
        mainHandler.post(() -> {
            // убрать pending если он совпадает
            if (uuid.equals(pendingHighlightUuid)) {
                pendingHighlightUuid = null;
                pendingHighlightDrawable = null;
            }
            Marker m = markerByUuid.get(uuid);
            if (m != null) {
                Drawable orig = originalIconByUuid.get(uuid);
                m.setIcon(orig);
                map.invalidate();
            }
        });
    }

    public void clearAllHighlights() {
        mainHandler.post(() -> {
            for (Map.Entry<String, Marker> e : markerByUuid.entrySet()) {
                String uuid = e.getKey();
                Marker m = e.getValue();
                Drawable orig = originalIconByUuid.get(uuid);
                if (m != null) m.setIcon(orig);
            }
            pendingHighlightUuid = null;
            pendingHighlightDrawable = null;
            map.invalidate();
        });
    }

    private void mapControllerZoomToPoint(GeoPoint p, double zoomDelta) {
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
        if (count < 100) return (count / 10) * 10;
        if (count < 1000) return (count / 100) * 100;
        return 1000;
    }

    private Drawable createClusterDrawable(int count) {
        int base = 40;
        int size = base + (int) (Math.min(1.0, Math.log10(Math.max(1, count))) * 48);
        Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        Paint circle = new Paint(Paint.ANTI_ALIAS_FLAG);
        circle.setStyle(Paint.Style.FILL);
        circle.setARGB(255, 58, 123, 213);
        float cx = size / 2f;
        float cy = size / 2f;
        canvas.drawCircle(cx, cy, size / 2f, circle);

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

    private Drawable getSinglePointDrawable() {
        try {
            return AppCompatResources.getDrawable(context, R.drawable.map_point);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isPointInBoundingBox(double lat, double lon, BoundingBox bbox) {
        return lat <= bbox.getLatNorth() && lat >= bbox.getLatSouth()
                && lon >= bbox.getLonWest() && lon <= bbox.getLonEast();
    }

    private static long packIntsToLong(int a, int b) {
        return (((long) a) << 32) | (b & 0xffffffffL);
    }

    private static double lonToWorldX(double lon, double worldSize) {
        return (lon + 180.0) / 360.0 * worldSize;
    }

    private static double latToWorldY(double lat, double worldSize) {
        double sinLat = Math.sin(lat * Math.PI / 180.0);
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
        return y * worldSize;
    }

    public void shutdown() {
        try { runningTask = null; executor.shutdownNow(); } catch (Exception ignored) {}
        removeClusterMarkersFromMap();
    }

    private static class ClusterAccumulator {
        double sumLat;
        double sumLon;
        int count;
        MapPoint sample;
    }

    private static class Cluster {
        double lat;
        double lon;
        int count;
        MapPoint sample;
        Cluster(double lat, double lon, int count, MapPoint sample) {
            this.lat = lat; this.lon = lon; this.count = count; this.sample = sample;
        }
    }
}
