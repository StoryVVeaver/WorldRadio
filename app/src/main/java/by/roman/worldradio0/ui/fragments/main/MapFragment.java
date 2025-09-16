package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {
    private MapView map;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewByID(view);
        initAll();




    }

    private void findViewByID(View view){
        map = view.findViewById(R.id.map);
    }
    private void initAll(){
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(55.7558, 37.6173);
        mapController.setCenter(startPoint);
        mapController.setZoom(12);
        addMarkers();
    }

    private void addMarkers(List<RadioStation> station_list) {


        Marker marker = new Marker(map);
        for (RadioStation i: station_list){

        }
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);

        marker.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.map_point));

        marker.setOnMarkerClickListener((marker1, mapView) -> {
            Toast.makeText(requireContext(), marker1.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        map.getOverlays().add(marker);
        map.invalidate();
    }

}