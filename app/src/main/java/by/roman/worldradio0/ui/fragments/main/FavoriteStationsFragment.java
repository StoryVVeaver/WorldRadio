package by.roman.worldradio0.ui.fragments.main;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.EndlessRecyclerViewScrollListener;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.view_models.FavoriteViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.fragments.timer.AlarmFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteStationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RadioAdapter adapter;
    private FavoriteViewModel viewModel;
    private StateViewModel stateViewModel;
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
                        playerViewModel.start(adapter.getStation(position));
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.not_correct_internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDeleteClick(int position) {
                viewModel.removeStationFromFavorite(adapter.getStation(position).getStationUuid());
            }

            @Override
            public void onStationLongClick(int position) {
                showMenu(position);
            }
        });
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
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
    private void showMenu(int position) {
        String[] options = {getResources().getString(R.string.schedule_playback), getResources().getString(R.string.remove_favorite)};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(adapter.getStation(position).getName());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    AlarmFragment fragment = AlarmFragment.newInstance(adapter.getStation(position).getStationUuid());
                    stateViewModel.openFullscreen(fragment);
                    break;
                case 1:
                    viewModel.removeStationFromFavorite(adapter.getStation(position).getStationUuid());
                    break;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
        dialog.show();
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