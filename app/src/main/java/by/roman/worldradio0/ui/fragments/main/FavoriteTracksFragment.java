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

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.EndlessRecyclerViewScrollListener;
import by.roman.worldradio0.business_logic.adapters.TrackAdapter;
import by.roman.worldradio0.business_logic.view_models.FavoriteViewModel;
import by.roman.worldradio0.business_logic.view_models.ListViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteTracksFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavoriteViewModel viewModel;
    private TrackAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_tracks, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        findAll(view);
        initAll();
        observeData();

    }
    private void findAll(@NonNull View view){
        recyclerView = view.findViewById(R.id.recyclerView_FavoriteTracks);
    }
    private void initAll(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TrackAdapter(new java.util.ArrayList<>(), position -> {
            viewModel.removeTrackFromFavorite(adapter.getTrack(position));
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                viewModel.loadTrackNextPage();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }
    private void observeData(){
        viewModel.getFavoriteTracks().observe(getViewLifecycleOwner(), items -> {
            switch (items.status){
                case SUCCESS:
                    adapter.setData(items.data);
                    break;

                case ERROR:
                    Log.e("FavoriteTrackFragment", items.message);
                    break;

                case LOADING:
                    break;
            }
        });

        viewModel.loadTrackNextPage();
    }
}