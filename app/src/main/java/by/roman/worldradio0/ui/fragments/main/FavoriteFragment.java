package by.roman.worldradio0.ui.fragments.main;

import static androidx.core.content.ContextCompat.getColor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.LocationUtil;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.FavoriteViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.ui.fragments.auth.EntranceFragment;
import by.roman.worldradio0.ui.fragments.auth.RegistrationFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteFragment extends Fragment {
    private ConstraintLayout stations;
    private ConstraintLayout tracks;
    private TextView stationsText;
    private TextView tracksText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("FavoriteFragment: performance", "onViewCreated started");
        findAllId(view);
        stations.setOnClickListener(v1 -> mode(new FavoriteStationsFragment(),0));
        tracks.setOnClickListener(v1 -> mode(new FavoriteTracksFragment(),1));
        Log.v("FavoriteFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAllId(View view){
        stations = view.findViewById(R.id.stationMode);
        tracks = view.findViewById(R.id.trackMode);
        stationsText = view.findViewById(R.id.stationMode_Text);
        tracksText = view.findViewById(R.id.trackMode_Text);
    }
    private void mode(Fragment f, int num){
        change(f);
        switch (num){
            case 0:
                stations.setBackgroundColor(getColor(requireContext(), R.color.selectedMode));
                stationsText.setTextColor(getColor(requireContext(), R.color.white));
                tracks.setBackgroundColor(getColor(requireContext(), R.color.unselectedMode));
                tracksText.setTextColor(getColor(requireContext(), R.color.unselectedText));
                break;
            case 1:
                stations.setBackgroundColor(getColor(requireContext(), R.color.unselectedMode));
                stationsText.setTextColor(getColor(requireContext(), R.color.unselectedText));
                tracks.setBackgroundColor(getColor(requireContext(), R.color.selectedMode));
                tracksText.setTextColor(getColor(requireContext(), R.color.white));
                break;
        }
    }
    private void change(Fragment f){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        ft.replace(R.id.fragmentContainerView_Favorite,f);
        ft.commit();
    }
}