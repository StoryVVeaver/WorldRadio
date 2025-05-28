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
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.FavoriteViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private RadioAdapter adapter;
    private FavoriteViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private boolean isLoadingNextPage = false;

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
        initAll();
        observeAndLoad();
        //TODO drag and drop
        Log.v("FavoriteFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoadingNextPage && !viewModel.getIsLastPage()) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        //isLoadingNextPage = true;
                        //adapter.showLoading();
                        //viewModel.loadNextPage();
                        //TODO переделать хуйню
                    }
                }
            }
        });
    }
    private void findAllId(View view){
        recyclerView = view.findViewById(R.id.recyclerView_Favorite);
    }
    private void initAll(){
        adapter = new RadioAdapter(getContext(), new RadioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(playerViewModel.isInternetConnected()){
                    if(playerViewModel.checkTypeInternet().equals("ok")){
                        playerViewModel.setPlaying(adapter.getUUID(position));
                        playerViewModel.start();
                    } else {
                        Toast.makeText(getContext(), "Not correct internet type!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Check internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDeleteClick(int position) {
                viewModel.removeFromFavorite(adapter.getUUID(position));
            }
        });
        adapter.setMode(1);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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
                    List<RadioStation> allData = stations.data;
                    adapter.replaceAll(allData);
                    isLoadingNextPage = false;
                    break;
                case ERROR:
                    adapter.hideLoading();
                    Toast.makeText(getContext(), stations.message, Toast.LENGTH_SHORT).show();
                    isLoadingNextPage = false;
                    break;
            }
        });
    }
}