package by.roman.worldradio0.ui.fragments.main;

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
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.MapViewModel;
import by.roman.worldradio0.ui.elements.view.OptimizedGridClusterer;
import dagger.hilt.android.AndroidEntryPoint;

//TODO после загрузки станций нужно пперепнуть лист в карте
@AndroidEntryPoint
public class MapFragment extends Fragment implements MapEventsReceiver {
    private MapView map;
    private MapViewModel viewModel;
    private OptimizedGridClusterer clusterer;
    private final Handler clusterHandler = new Handler(Looper.getMainLooper());
    private final Runnable clusterRunnable = () -> clusterer.clusterAsync();

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
        initializeMap();
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        viewModel.loadPoints();
        getPoints();
    }

    private void findViewByID(@NonNull View view){
        map = view.findViewById(R.id.map);
    }
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
                return false;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                scheduleCluster();
                return false;
            }
        });
    }
    private void getPoints(){
        viewModel.getListPoints().observe(getViewLifecycleOwner(), points -> {
            switch (points.status){
                case SUCCESS:
                    clusterer.setItems(points.data);
                    scheduleCluster();
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

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}