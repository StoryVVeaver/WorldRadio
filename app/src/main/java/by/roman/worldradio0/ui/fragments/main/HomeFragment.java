package by.roman.worldradio0.ui.fragments.main;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.HomeViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.ui.activities.TimerActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private RadioAdapter adapter;
    private ImageView timerButton;
    private RecyclerView recyclerView;
    private int position;
    private boolean isLoadingNextPage = false;

    @Override
    public void onResume(){
        super.onResume();
        timerButton.setEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("HomeFragment: performance", "onViewCreated started");
        findAllId(view);
        initAll();
        observeAndLoad();
        Log.v("HomeFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");

        timerButton.setOnClickListener(v -> {
            timerButton.setEnabled(false);
            //TODO
            //Intent intent = new Intent(getContext(), TimerActivity.class);
            //startActivity(intent);
        });

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
                        isLoadingNextPage = true;
                        adapter.showLoading();
                        viewModel.loadNextPage();
                    }
                }
            }
        });
    }
    private void observeAndLoad() {
        viewModel.getAllStations().observe(getViewLifecycleOwner(), stations -> {
            switch (stations.status) {
                case LOADING:
                    if (adapter.getItemCount() > 0) {
                        adapter.showLoading();
                    }
                    break;
                case SUCCESS:
                    adapter.hideLoading();
                    List<RadioStation> allData = stations.data;
                    adapter.addStations(allData.subList(adapter.getItemCount(), allData.size()));
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
    private void findAllId(View view){
        timerButton = view.findViewById(R.id.timerButtonView);
        recyclerView = view.findViewById(R.id.cardView);
    }
    private void initAll(){
        adapter = new RadioAdapter(getContext(), new RadioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playerViewModel.setPlaying(adapter.getUUID(position));
                playerViewModel.start();
                Toast.makeText(requireContext(),"id: " + position,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDeleteClick(int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
    }
}