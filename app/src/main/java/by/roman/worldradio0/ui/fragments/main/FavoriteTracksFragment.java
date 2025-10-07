package by.roman.worldradio0.ui.fragments.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

        adapter = new TrackAdapter(new java.util.ArrayList<>(), new TrackAdapter.OnTrackClickListener() {
            @Override
            public void onDeleteClick(int position) {
                viewModel.removeTrackFromFavorite(adapter.getTrack(position));
            }

            @Override
            public void onBrowseClick(int position) {
                searchInBrowser(requireActivity(), adapter.getTrack(position));
            }
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
    @SuppressLint("QueryPermissionsNeeded")
    public void searchInBrowser(Context context, String query) {
        try {
            String encodedQuery = Uri.encode(query);
            String url = "https://www.google.com/search?q=" + encodedQuery;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Нет доступного браузера", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка при открытии браузера", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}