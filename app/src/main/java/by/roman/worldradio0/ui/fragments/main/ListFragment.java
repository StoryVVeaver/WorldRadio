package by.roman.worldradio0.ui.fragments.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.FilterViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.fragments.timer.AlarmFragment;
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
                play(adapter.getStation(position));
            }

            @Override
            public void onDeleteClick(int position) {
            }

            @Override
            public void onStationLongClick(int position) {
                showMenu(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        viewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
    }

    private void findAllId(View view) {
        recyclerView = view.findViewById(R.id.list_recycler);
    }
    private void showMenu(int position) {
        String[] options = {getResources().getString(R.string.schedule_playback)};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(adapter.getStation(position).getName());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    AlarmFragment fragment = AlarmFragment.newInstance(adapter.getStation(position).getStationUuid());
                    stateViewModel.openFullscreen(fragment);
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
    private void play(RadioStation station) {
        if (!playerViewModel.isInternetConnected()) {
            Toast.makeText(getContext(), getResources().getString(R.string.not_correct_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!"ok".equals(playerViewModel.checkTypeInternet())) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }
        playerViewModel.start(station);
    }

    private void observeAndLoad() {
        playerViewModel.getSelectedCard().observe(getViewLifecycleOwner(), uuid -> {
            adapter.setSelectedStationUuid(uuid);
        });
        viewModel.getFilteredStations().observe(getViewLifecycleOwner(), stations -> {
            if (!isVisibleToUser) return;

            switch (stations.status) {
                case LOADING:
                    if (adapter.getItemCount() == 0) {
                        adapter.showLoading();
                    }
                    break;
                case SUCCESS:
                    adapter.hideLoading();
                    if (stations.data != null) {
                        adapter.replaceAll(stations.data);
                    }
                    isLoadingNextPage = false;
                    break;
                case ERROR:
                    adapter.hideLoading();
                    if (stations.message != null) {
                        Toast.makeText(getContext(), stations.message, Toast.LENGTH_SHORT).show();
                    }
                    isLoadingNextPage = false;
                    break;
            }
        });

        playerViewModel.getPlayPrevious().observe(getViewLifecycleOwner(), flag -> {
            if (flag != null && flag) {
                String currentUuid = playerViewModel.getCurrentStation().getStationUuid();
                int pos = adapter.findCurrentStation(currentUuid);
                if(pos > 0){
                    play(adapter.getStation(pos - 1));
                }
            }
        });

        playerViewModel.getPlayNext().observe(getViewLifecycleOwner(), flag -> {
            if (flag != null && flag) {
                String currentUuid = playerViewModel.getCurrentStation().getStationUuid();
                int pos = adapter.findCurrentStation(currentUuid);
                if(pos != -1 && pos < adapter.getItemCount() - 1){
                    play(adapter.getStation(pos + 1));
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