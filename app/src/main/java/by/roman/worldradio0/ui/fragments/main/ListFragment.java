package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import by.roman.worldradio0.business_logic.view_models.FilterViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListFragment extends Fragment {
    private PlayerViewModel playerViewModel;
    private StateViewModel stateViewModel;
    private FilterViewModel viewModel;
    private RecyclerView recyclerView;
    private RadioAdapter adapter;
    private boolean isLoadingNextPage = false;
    private boolean isVisibleToUser = false;
    private boolean isFirstLoad = true;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onResume() {
        super.onResume();
        isVisibleToUser = true;

        if (isFirstLoad) {
            viewModel.resetState();
            viewModel.loadStart();
            isFirstLoad = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleToUser = false;
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        long startTime = System.nanoTime();
        Log.v("HomeFragment: performance", "onViewCreated started");

        findAllId(view);
        initAll();
        observeAndLoad();
        setupScrollListener();

        Log.v("HomeFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isVisibleToUser || isLoadingNextPage || viewModel.getIsLastPage()) return;

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Более агрессивная загрузка - начинаем раньше
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                        isLoadingNextPage = true;
                        adapter.showLoading();
                        viewModel.loadNextPage();
                    }
                }
            }
        });
    }

    private void initAll() {
        adapter = new RadioAdapter(getContext(), new RadioAdapter.OnItemClickListener() {
            @Override
            public void onStationItemClick(int position) {
                if (!isVisibleToUser) return;
                play(adapter.getUUID(position));
            }

            @Override
            public void onDeleteClick(int position) {
                // Обработка удаления
            }
        });

        // Оптимизация RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true); // Если все элементы одинаковой высоты
        recyclerView.setItemViewCacheSize(20); // Кэшируем больше элементов

        viewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
    }

    private void findAllId(View view) {
        recyclerView = view.findViewById(R.id.list_recycler);
    }

    private void play(String uuid) {
        if (!playerViewModel.isInternetConnected()) {
            Toast.makeText(getContext(), "Check internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!"ok".equals(playerViewModel.checkTypeInternet())) {
            Toast.makeText(getContext(), "Not correct internet type!", Toast.LENGTH_SHORT).show();
            return;
        }
        playerViewModel.start(uuid);
    }

    private void observeAndLoad() {
        // Оптимизированный observer для станций
        viewModel.getFilteredStations().observe(getViewLifecycleOwner(), stations -> {
            if (!isVisibleToUser) return;

            switch (stations.status) {
                case LOADING:
                    // Показываем loading только если список пустой
                    if (adapter.getItemCount() == 0) {
                        adapter.showLoading();
                    }
                    break;
                case SUCCESS:
                    handler.post(() -> {
                        if (isVisibleToUser && adapter != null) {
                            adapter.hideLoading();
                            List<RadioStation> data = stations.data;
                            if (data != null) {
                                adapter.replaceAll(data);
                            }
                            isLoadingNextPage = false;
                        }
                    });
                    break;
                case ERROR:
                    handler.post(() -> {
                        if (isVisibleToUser && adapter != null) {
                            adapter.hideLoading();
                            if (stations.message != null && !stations.message.isEmpty()) {
                                Toast.makeText(getContext(), stations.message, Toast.LENGTH_SHORT).show();
                            }
                            isLoadingNextPage = false;
                        }
                    });
                    break;
            }
        });

        playerViewModel.getPlayPrevious().observe(getViewLifecycleOwner(), flag -> {
            if (flag != null && flag) {
                String currentUuid = playerViewModel.getCurrentStation().getStationUuid();
                int pos = adapter.findCurrentStation(currentUuid);
                if(pos > 0){
                    play(adapter.getUUID(pos - 1));
                }
            }
        });

        playerViewModel.getPlayNext().observe(getViewLifecycleOwner(), flag -> {
            if (flag != null && flag) {
                String currentUuid = playerViewModel.getCurrentStation().getStationUuid();
                int pos = adapter.findCurrentStation(currentUuid);
                if(pos != -1 && pos < adapter.getItemCount() - 1){
                    play(adapter.getUUID(pos + 1));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}