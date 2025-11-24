package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.EndlessRecyclerViewScrollListener;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.FavoriteViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteStationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RadioAdapter adapter;
    private FavoriteViewModel viewModel;
    private PlayerViewModel playerViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_stations, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("FavoriteFragment: performance", "onViewCreated started");
        findAllId(view);
        initAll();
        observeAndLoad();
        //TODO drag and drop
        Log.v("FavoriteFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAllId(@NonNull View view){
        recyclerView = view.findViewById(R.id.recyclerView_FavoriteStations);
    }
    private void initAll(){
        adapter = new RadioAdapter(getContext(), new RadioAdapter.OnItemClickListener() {
            @Override
            public void onStationItemClick(int position) {
                if(playerViewModel.isInternetConnected()){
                    if(playerViewModel.checkTypeInternet().equals("ok")){
                        playerViewModel.start(adapter.getUUID(position));
                    } else {
                        Toast.makeText(getContext(), "Not correct internet type!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Check internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDeleteClick(int position) {
                viewModel.removeStationFromFavorite(adapter.getUUID(position));
            }
        });
        adapter.setMode(1);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(FavoriteViewModel.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                viewModel.loadStationNextPage();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }
    private void observeAndLoad() {
        viewModel.getFavoriteStations().observe(getViewLifecycleOwner(), stations -> {
            switch (stations.status) {
                case LOADING:
                    if (adapter.getItemCount() > 0) {
                        adapter.showLoading();
                    }
                    break;
                case SUCCESS:
                    adapter.hideLoading();
                    adapter.replaceAll(stations.data);
                    break;
                case ERROR:
                    adapter.hideLoading();
                    Toast.makeText(getContext(), stations.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        viewModel.loadStationNextPage();
    }
}