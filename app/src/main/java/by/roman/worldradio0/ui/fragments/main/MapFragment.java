package by.roman.worldradio0.ui.fragments.main;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import by.roman.worldradio0.business_logic.UiState;
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

    private MapView map;
    private MapViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private HistoryViewModel historyViewModel;
    private SettingsViewModel settingsViewModel;

    private CenterSnapOverlay centerSnap;
    private GridClusterer clusterer;

    private final Handler clusterHandler = new Handler(Looper.getMainLooper());
    private final Runnable clusterRunnable = () -> clusterer.clusterAsync();
    private final Runnable updateVisibleRunnable = this::updateCenterSnapVisiblePointsImmediate;
    private final Runnable apiLoadRunnable = this::loadStationsForVisibleRegion;

    private List<MapPoint> allPoints;
    private IMapController mapController;
    private ImageButton snapOn, GPS;
    private ImageView point;
    private Settings settings;

    private double lat, lon;
    private GeoPoint lastLoadCenter = new GeoPoint(0.0, 0.0);
    private double lastZoom = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewByID(view);

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        initializeMap();
        observeData();

        snapOn.setOnClickListener(v -> {
            centerSnap.setSnapEnabled(!centerSnap.isSnapEnabled());
            updateSnapButtonUI();
        });

        GPS.setOnClickListener(v -> {
            mapController.animateTo(new GeoPoint(lat, lon));
            if (map.getZoomLevelDouble() < 12) mapController.setZoom(12.0);
        });

        centerMap(playerViewModel.getCurrentStation());
    }

    private void findViewByID(@NonNull View view) {
        GPS = view.findViewById(R.id.GPSButtonView);
        map = view.findViewById(R.id.map);
        snapOn = view.findViewById(R.id.snapButtonView);
        point = view.findViewById(R.id.center_point);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMap() {
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();

        clusterer = new GridClusterer(requireContext(), map);
        clusterer.setCellSizePx(80);

        map.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                scheduleCluster();
                scheduleUpdateVisibleForCenterSnap();
                checkAndScheduleApiLoad();
                return false;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                scheduleCluster();
                scheduleUpdateVisibleForCenterSnap();
                checkAndScheduleApiLoad();
                return false;
            }
        });

        centerSnap = new CenterSnapOverlay(map, 256, point, snapped -> {
            if (snapped == null) {
                playerViewModel.stop();
                return;
            }
            if(playerViewModel.isInternetConnected()){
                if(playerViewModel.checkTypeInternet().equals("ok")){
                    RadioStation stationToStart = new RadioStation(snapped.getUuid(), snapped.getName(),snapped.getUrl(), snapped.getFavicon(), snapped.getHomepage());
                    playerViewModel.start(stationToStart);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.not_correct_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        });

        centerSnap.setRequireFirstTouch(true);

        Drawable markerDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.map_point);
        int iconH = (markerDrawable != null) ? markerDrawable.getIntrinsicHeight() : 100;
        centerSnap.setDefaultIconOffset(new Point(0, -(iconH / 2)));

        clusterer.setOnClusterMarkerClickListener(mp -> centerSnap.snapTo(mp, true, true));
        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) centerSnap.notifyUserInteraction();
            return false;
        });

        map.getOverlays().add(centerSnap);

        settings = settingsViewModel.getSettingsModel();
        centerSnap.setSnapEnabled(settings.getSnapEnabled() == 1);
        updateSnapButtonUI();
    }

    private void observeData() {
        viewModel.getListPoints().observe(getViewLifecycleOwner(), state -> {
            if (state.status == UiState.Status.SUCCESS && state.data != null) {
                allPoints = state.data;
                clusterer.setItems(allPoints);
                scheduleCluster();
                updateCenterSnapVisiblePointsImmediate();
            }
        });

        playerViewModel.getSnapNearestEvent().observe(getViewLifecycleOwner(), event -> snapToNearest());
        playerViewModel.getSnapPrevious().observe(getViewLifecycleOwner(), event -> snapToPrevious());

        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            if (!isPlaying) centerSnap.clearSnapped();
        });

        playerViewModel.getIsPlayingChanged().observe(getViewLifecycleOwner(), this::centerMap);
    }

    private void checkAndScheduleApiLoad() {
        double currentZoom = map.getZoomLevelDouble();
        GeoPoint currentCenter = (GeoPoint) map.getMapCenter();

        double threshold = map.getBoundingBox().getDiagonalLengthInMeters() / 4;
        if (Math.abs(currentZoom - lastZoom) > 0.5 || lastLoadCenter.distanceToAsDouble(currentCenter) > threshold) {
            clusterHandler.removeCallbacks(apiLoadRunnable);
            clusterHandler.postDelayed(apiLoadRunnable, 800);
        }
    }

    private void loadStationsForVisibleRegion() {
        if (map == null) return;
        GeoPoint center = (GeoPoint) map.getMapCenter();
        BoundingBox box = map.getBoundingBox();

        // Радиус до угла экрана
        GeoPoint northEast = new GeoPoint(box.getLatNorth(), box.getLonEast());
        double radius = center.distanceToAsDouble(northEast);

        lastLoadCenter = center;
        lastZoom = map.getZoomLevelDouble();

        viewModel.loadPointsByLocation(center.getLatitude(), center.getLongitude(), radius);
    }

    private void updateCenterSnapVisiblePointsImmediate() {
        if (allPoints == null || map == null) return;
        BoundingBox bbox = map.getBoundingBox();
        List<MapPoint> visible = new ArrayList<>();
        for (MapPoint p : allPoints) {
            if (bbox.contains(p.getLatitude(), p.getLongitude())) {
                visible.add(p);
            }
        }
        centerSnap.feedVisiblePoints(visible);
    }

    private void centerMap(@Nullable RadioStation station) {
        if (station != null && isValidLocation(station.getGeoLat(), station.getGeoLong())) {
            mapController.animateTo(new GeoPoint(station.getGeoLat(), station.getGeoLong()));
            ensureZoom();
        } else {
            requestUserLocation();
        }
    }

    private void requestUserLocation() {
        LocationUtil.requestLocation(requireActivity(), new LocationUtil.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude, String country, String code) {
                lat = latitude; lon = longitude;
                mapController.animateTo(new GeoPoint(lat, lon));
                ensureZoom();
                // После того как нашли пользователя, грузим станции вокруг него
                loadStationsForVisibleRegion();
            }
            @Override
            public void onError(String error) { Log.e("MapFragment", error); }
        });
    }

    private void snapToNearest() {
        if (allPoints == null || allPoints.isEmpty()) return;
        BoundingBox bbox = map.getBoundingBox();
        MapPoint nearest = null;
        double minSourceDist = Double.MAX_VALUE;

        GeoPoint mapCenter = (GeoPoint) map.getMapCenter();
        String currentUuid = playerViewModel.getCurrentStation() != null ? playerViewModel.getCurrentStation().getStationUuid() : "";

        for (MapPoint p : allPoints) {
            if (bbox.contains(p.getLatitude(), p.getLongitude()) && !p.getUuid().equals(currentUuid)) {
                double dist = mapCenter.distanceToAsDouble(new GeoPoint(p.getLatitude(), p.getLongitude()));
                if (dist < minSourceDist) {
                    minSourceDist = dist;
                    nearest = p;
                }
            }
        }
        if (nearest != null) centerSnap.snapTo(nearest, true, true);
    }

    private void snapToPrevious() {
        String lastUuid = historyViewModel.getLastHistory().getUuid();
        MapPoint prev = viewModel.getMapPointByUUID(lastUuid);
        if (prev != null) centerSnap.snapTo(prev, true, true);
    }

    private void updateSnapButtonUI() {
        int iconRes = centerSnap.isSnapEnabled() ? R.drawable.snap : R.drawable.snap_crossed;
        snapOn.setImageDrawable(AppCompatResources.getDrawable(requireContext(), iconRes));
        settings.setSnapEnabled(centerSnap.isSnapEnabled() ? 1 : 0);
        settingsViewModel.setSettings(settings);
    }

    private void ensureZoom() { if (map.getZoomLevelDouble() < 12) mapController.setZoom(12.0); }
    private boolean isValidLocation(double lt, double ln) { return lt != 0.0 && ln != 0.0; }
    private void scheduleCluster() { clusterHandler.removeCallbacks(clusterRunnable); clusterHandler.postDelayed(clusterRunnable, 275); }
    private void scheduleUpdateVisibleForCenterSnap() { clusterHandler.removeCallbacks(updateVisibleRunnable); clusterHandler.postDelayed(updateVisibleRunnable, 250); centerSnap.scheduleDelayedSnap(); }

    @Override
    public void onResume() { super.onResume(); map.onResume(); loadStationsForVisibleRegion(); }
    @Override
    public void onPause() { super.onPause(); map.onPause(); }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clusterHandler.removeCallbacksAndMessages(null);
        if (map != null) map.onDetach();
        if (clusterer != null) clusterer.shutdown();
    }

    @Override public boolean singleTapConfirmedHelper(GeoPoint p) { return false; }
    @Override public boolean longPressHelper(GeoPoint p) { return false; }
}