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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.view_models.MapViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.ui.elements.view.CenterSnapOverlay;
import by.roman.worldradio0.ui.elements.view.OptimizedGridClusterer;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment implements MapEventsReceiver {
    private CenterSnapOverlay centerSnap;
    private String currentSnappedUuid = null;
    private MapView map;
    private MapViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private OptimizedGridClusterer clusterer;
    private final Handler clusterHandler = new Handler(Looper.getMainLooper());
    private final Runnable clusterRunnable = () -> clusterer.clusterAsync();
    private final Runnable updateVisibleRunnable = this::updateCenterSnapVisiblePointsImmediate;
    private Drawable centerDrawable;
    private Drawable centerSnappedDrawable;
    private Drawable highlightMarkerDrawable;

    private List<MapPoint> allPoints;

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

        initializeMap();

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        viewModel.loadPoints();
        getPoints();
    }

    private void findViewByID(@NonNull View view){
        map = view.findViewById(R.id.map);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMap() {
        Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(10.0);
        mapController.setCenter(new GeoPoint(55.7558, 37.6173));

        clusterer = new OptimizedGridClusterer(requireContext(), map);
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

        // create overlay (initially DISABLED so start won't snap)
        centerSnap = new CenterSnapOverlay(map, 256, centerDrawable, centerSnappedDrawable, snapped -> {
            if (snapped != null) {
                if (playerViewModel.isInternetConnected()) {
                    if ("ok".equals(playerViewModel.checkTypeInternet())) {
                        playerViewModel.setPlaying(snapped.getUuid());
                        playerViewModel.start();
                    } else {
                        Toast.makeText(getContext(), "Not correct internet type!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Check internet connection!", Toast.LENGTH_SHORT).show();
                }

                clusterer.highlightMarkerByUuid(snapped.getUuid(), highlightMarkerDrawable);

                if (currentSnappedUuid != null && !currentSnappedUuid.equals(snapped.getUuid())) {
                    clusterer.clearHighlightByUuid(currentSnappedUuid);
                }
                currentSnappedUuid = snapped.getUuid();
            } else {
                if (currentSnappedUuid != null) {
                    clusterer.clearHighlightByUuid(currentSnappedUuid);
                    currentSnappedUuid = null;
                }
            }
        });

        centerSnap.setSnapEnabled(false);

        Drawable markerDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.map_point);
        int iconH = (markerDrawable != null && markerDrawable.getIntrinsicHeight() > 0)
                ? markerDrawable.getIntrinsicHeight()
                : map.getWidth() / 12;
        Point defaultIconOffset = new Point(0, - (iconH / 2));
        centerSnap.setDefaultIconOffset(defaultIconOffset);

        // cluster marker click -> enable snap (if not) and snap to point
        clusterer.setOnClusterMarkerClickListener(mp -> {
            if (!centerSnap.isSnapEnabled()) centerSnap.setSnapEnabled(true);
            centerSnap.snapTo(mp, true);
        });
        // enable snap after first user touch on map (prevents startup sticky behavior)
        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!centerSnap.isSnapEnabled()) centerSnap.setSnapEnabled(true);
            }
            return false;
        });

        map.getOverlays().add(centerSnap);
    }

    private void getPoints(){
        viewModel.getListPoints().observe(getViewLifecycleOwner(), points -> {
            switch (points.status){
                case SUCCESS:
                    allPoints = points.data;
                    clusterer.setItems(allPoints);
                    scheduleCluster();
                    // do not call scheduleDelayedSnap here so startup won't snap
                    updateCenterSnapVisiblePointsImmediate();
                    break;
                case LOADING:
                case ERROR:
                    break;
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
        // scheduleDelayedSnap will no-op if snap disabled
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
        // we intentionally do not call centerSnap.scheduleDelayedSnap() here to avoid startup snap
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) { return false; }

    @Override
    public boolean longPressHelper(GeoPoint p) { return false; }

}
