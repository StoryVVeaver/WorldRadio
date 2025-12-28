package by.roman.worldradio0.ui.fragments.main;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.LocationUtil;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.view_models.HistoryViewModel;
import by.roman.worldradio0.business_logic.view_models.MapViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import by.roman.worldradio0.ui.elements.view.CenterSnapOverlay;
import by.roman.worldradio0.ui.elements.view.GridClusterer;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment implements MapEventsReceiver {
    private CenterSnapOverlay centerSnap;
    private String currentSnappedUuid = null;
    private MapView map;
    private MapViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private HistoryViewModel historyViewModel;
    private SettingsViewModel settingsViewModel;
    private GridClusterer clusterer;
    private final Handler clusterHandler = new Handler(Looper.getMainLooper());
    private final Runnable clusterRunnable = () -> clusterer.clusterAsync();
    private final Runnable updateVisibleRunnable = this::updateCenterSnapVisiblePointsImmediate;
    private Drawable centerDrawable;
    private Drawable centerSnappedDrawable;
    private Drawable highlightMarkerDrawable;
    private String previousSnappedUuid = null;
    private List<MapPoint> allPoints;
    private IMapController mapController;
    private ImageButton snapOn, GPS;
    private Settings settings;
    private double lat, lon;

    @Override
    public void onResume() {
        super.onResume();
        settings = settingsViewModel.getSettingsModel();
        centerSnap.setSnapEnabled(settings.getSnapEnabled() == 1);
        fav_snap();
        if (playerViewModel.getCurrentStation() != null) {
            RadioStation station = playerViewModel.getCurrentStation();
            Log.v("MapFragment", "station ok, checking " + station.getGeoLat() + " " + station.getGeoLong() + " " + station.getStationUuid());
            if (station.getGeoLat() != 0 && station.getGeoLong() != 0 && !station.getStationUuid().isEmpty()) {
                mapController.setCenter(new GeoPoint(station.getGeoLat(), station.getGeoLong()));
                if (map.getZoomLevel() < 12) {
                    mapController.setZoom(12.0);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("SAVED_LAT", lat);
        outState.putDouble("SAVED_LON", lon);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewByID(view);

        centerDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.delete);
        centerSnappedDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.fi_rs_filter);
        highlightMarkerDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.history);

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        initializeMap();


        viewModel.loadPoints();
        observeData();

        snapOn.setOnClickListener(v -> {
            Log.v("map", centerSnap.isSnapEnabled() + " now - set " + !centerSnap.isSnapEnabled());
            centerSnap.setSnapEnabled(!centerSnap.isSnapEnabled());
            fav_snap();
        });
        GPS.setOnClickListener(v -> {
            mapController.setCenter(new GeoPoint(lat, lon));
            if(map.getZoomLevel() < 12){
                mapController.setZoom(12.0);
            }
        });
        if (savedInstanceState != null
                && savedInstanceState.containsKey("SAVED_LAT")
                && savedInstanceState.containsKey("SAVED_LON")) {

            lat = savedInstanceState.getDouble("SAVED_LAT");
            lon = savedInstanceState.getDouble("SAVED_LON");
            mapController.setCenter(new GeoPoint(lat, lon));
            if (map.getZoomLevel() < 12) {
                mapController.setZoom(12.0);
            }
        } else {
            LocationUtil.requestLocation(requireActivity(), new LocationUtil.LocationCallback() {
                @Override
                public void onLocationReceived(double latitude, double longitude, String countryName, String countryCode) {
                    mapController.setCenter(new GeoPoint(latitude, longitude));
                    lat = latitude;
                    lon = longitude;
                }

                @Override
                public void onError(String error) {
                    Log.e("LOCATION_ERROR", error);
                }
            });
        }
    }

    private void findViewByID(@NonNull View view){
        GPS = view.findViewById(R.id.GPSButtonView);
        map = view.findViewById(R.id.map);
        snapOn = view.findViewById(R.id.snapButtonView);
    }
    private void fav_snap(){
        if (centerSnap.isSnapEnabled()){
            snapOn.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.snap));
            settings.setSnapEnabled(1);
        } else {
            snapOn.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.snap_crossed));
            settings.setSnapEnabled(0);
        }
        settingsViewModel.setSettings(settings);
    }
    //TODO синхронизация автоматически листов
    //TODO настройки пользователя
    //TODO настройки аудио
    //TODO настройки вида навигации

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMap() {
        Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(new GeoPoint(0, 0));

        clusterer = new GridClusterer(requireContext(), map);
        clusterer.setCellSizePx(80);

        map.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                scheduleCluster();
                scheduleUpdateVisibleForCenterSnap();
                return false;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                scheduleCluster();
                scheduleUpdateVisibleForCenterSnap();
                return false;
            }
        });

        centerSnap = new CenterSnapOverlay(map, 256, centerDrawable, centerSnappedDrawable, snapped -> {
            if (snapped != null) {

                if (playerViewModel.isInternetConnected()) {
                    if ("ok".equals(playerViewModel.checkTypeInternet())) {
                        playerViewModel.start(snapped.getUuid());
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.not_correct_internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                clusterer.highlightMarkerByUuid(snapped.getUuid(), highlightMarkerDrawable);

                if (currentSnappedUuid != null && !currentSnappedUuid.equals(snapped.getUuid())) {
                    clusterer.clearHighlightByUuid(currentSnappedUuid);
                }
                currentSnappedUuid = snapped.getUuid();
            } else {
                if (currentSnappedUuid != null) {
                    previousSnappedUuid = currentSnappedUuid;
                    clusterer.clearHighlightByUuid(currentSnappedUuid);
                    currentSnappedUuid = null;
                }
            }
        });

        centerSnap.setRequireFirstTouch(true);
        centerSnap.setSnapEnabled(true);

        Drawable markerDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.map_point);
        int iconH = (markerDrawable != null && markerDrawable.getIntrinsicHeight() > 0)
                ? markerDrawable.getIntrinsicHeight()
                : map.getWidth() / 12;
        Point defaultIconOffset = new Point(0, - (iconH / 2));
        centerSnap.setDefaultIconOffset(defaultIconOffset);

        clusterer.setOnClusterMarkerClickListener(mp -> {
            centerSnap.snapTo(mp, true, true);
        });
        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                centerSnap.notifyUserInteraction();
            }
            return false;
        });

        map.getOverlays().add(centerSnap);
        settings = settingsViewModel.getSettingsModel();
        centerSnap.setSnapEnabled(settings.getSnapEnabled() == 1);
        fav_snap();
    }
    private void observeData(){
        viewModel.getListPoints().observe(getViewLifecycleOwner(), points -> {
            switch (points.status){
                case SUCCESS:
                    allPoints = points.data;
                    clusterer.setItems(allPoints);
                    scheduleCluster();
                    updateCenterSnapVisiblePointsImmediate();
                    break;
                case LOADING:
                case ERROR:
                    break;
            }
        });
        playerViewModel.getSnapNearestEvent().observe(getViewLifecycleOwner(), event -> {
            snapToNearest();
        });
        playerViewModel.getSnapPrevious().observe(getViewLifecycleOwner(), event -> {
            snapToPrevious();
        });
        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), status -> {
            if(status == false){
                clusterer.clearAllHighlights();
                centerSnap.clearSnapped();
                currentSnappedUuid = null;
                previousSnappedUuid = null;
            }
        });
        playerViewModel.getIsPlayingChanged().observe(getViewLifecycleOwner(), station -> {
            if(map.getZoomLevel() < 10){
                map.setZoomLevel(10);
            }
            if(station.getGeoLat() != 0 && station.getGeoLong() != 0){
                mapController.setCenter(new GeoPoint(station.getGeoLat(), station.getGeoLong()));
            }
        });
    }

    private void scheduleCluster() {
        clusterHandler.removeCallbacks(clusterRunnable);
        clusterHandler.postDelayed(clusterRunnable, 275);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clusterHandler.removeCallbacks(clusterRunnable);
        if (map != null) {
            map.onDetach();
        }
        if (clusterer != null) {
            clusterer.shutdown();
        }
    }

    private void scheduleUpdateVisibleForCenterSnap() {
        clusterHandler.removeCallbacks(updateVisibleRunnable);
        clusterHandler.postDelayed(updateVisibleRunnable, 250);
        centerSnap.scheduleDelayedSnap();
    }

    private void updateCenterSnapVisiblePointsImmediate() {
        if (allPoints == null) return;
        BoundingBox bbox = map.getBoundingBox();
        List<MapPoint> visible = new java.util.ArrayList<>(128);
        for (MapPoint p : allPoints) {
            double lat = p.getLatitude();
            double lon = p.getLongitude();
            if (lat <= bbox.getLatNorth() && lat >= bbox.getLatSouth()
                    && lon >= bbox.getLonWest() && lon <= bbox.getLonEast()) {
                visible.add(p);
            }
        }
        centerSnap.feedVisiblePoints(visible);
    }
    private void snapToNearest() {
        if (allPoints == null || allPoints.isEmpty() || map == null) return;

        BoundingBox bbox = map.getBoundingBox();
        List<MapPoint> visible = new ArrayList<>(128);
        for (MapPoint p : allPoints) {
            double lat = p.getLatitude();
            double lon = p.getLongitude();
            if (lat <= bbox.getLatNorth() && lat >= bbox.getLatSouth()
                    && lon >= bbox.getLonWest() && lon <= bbox.getLonEast()) {
                visible.add(p);
            }
        }

        if (visible.isEmpty()) {
            //Toast.makeText(requireContext(), "Рядом нет радиостанций", Toast.LENGTH_SHORT).show();
            return;
        }

        final int cx = map.getWidth() / 2;
        final int cy = map.getHeight() / 2;

        MapPoint nearest = null;
        double bestDist2 = Double.MAX_VALUE;
        android.graphics.Point tmp = new android.graphics.Point();

        for (MapPoint p : visible) {
            String uuid = p.getUuid();
            if (uuid == null) continue;
            if(uuid.equals(playerViewModel.getCurrentStation().getStationUuid())){
                Log.v("MapFragment", "Skip " + uuid + ", " +playerViewModel.getCurrentStation().getName());
                continue;
            }
            if(uuid.equals(historyViewModel.getLastHistory().getUuid())){
                Log.v("MapFragment", "Skip " + uuid + ", history");
                continue;
            }
            Log.v("MapFragment", uuid + " != " + historyViewModel.getLastHistory().getUuid());
            org.osmdroid.util.GeoPoint gp = new org.osmdroid.util.GeoPoint(p.getLatitude(), p.getLongitude());
            map.getProjection().toPixels(gp, tmp);
            double dx = tmp.x - cx;
            double dy = tmp.y - cy;
            double d2 = dx * dx + dy * dy;
            if (d2 < bestDist2) {
                bestDist2 = d2;
                nearest = p;
            }
        }

        if (nearest == null) {
            Toast.makeText(requireContext(), getResources().getString(R.string.no_stations), Toast.LENGTH_SHORT).show();
            return;
        }

        final MapPoint target = nearest;
        String snappedUuid = (centerSnap.getSnappedPoint() != null) ? centerSnap.getSnappedPoint().getUuid() : "null";
        Log.v("MapFragment", "from " + snappedUuid + " to " + nearest.getUuid());
        centerSnap.snapTo(target, true, true);
    }
    private void snapToPrevious(){
        if(historyViewModel.getLastHistory().getUuid() != null){
            MapPoint point = viewModel.getMapPointByUUID(historyViewModel.getLastHistory().getUuid());
            currentSnappedUuid = previousSnappedUuid;
            previousSnappedUuid = null;
            centerSnap.snapTo(point, true, true);
        } else {
            Toast.makeText(requireActivity(), getResources().getString(R.string.no_last), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) { return false; }
    @Override
    public boolean longPressHelper(GeoPoint p) { return false; }

}
