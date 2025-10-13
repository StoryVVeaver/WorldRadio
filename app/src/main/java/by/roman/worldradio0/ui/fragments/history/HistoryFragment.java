package by.roman.worldradio0.ui.fragments.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.HistoryViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFragment extends Fragment {
    private ImageButton back;
    private RadioAdapter adapter;
    private RecyclerView recyclerView;
    private HistoryViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private StateViewModel stateViewModel;
    private boolean isVisibleToUser = false;
    private boolean isLoadingNextPage = false;
    private ImageButton deleteAll;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleToUser = false;
        handler.removeCallbacksAndMessages(null);
        viewModel.cancelPendingOperations();
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisibleToUser = true;
        viewModel.resetState();
        viewModel.loadStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findAll(view);
        initAll();
        observeAndLoad();
        back.setOnClickListener(v1 -> {
            stateViewModel.closeFullscreen();
        });
        deleteAll.setOnClickListener(v1 -> {
            adapter.clear();
            viewModel.deleteAllHistory();
            viewModel.resetState();
            viewModel.loadStart();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isVisibleToUser) return;

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoadingNextPage && !viewModel.getIsLastPage()) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        isLoadingNextPage = true;
                        handler.post(() -> {
                            if (isVisibleToUser && adapter != null) {
                                adapter.showLoading();
                                viewModel.loadNextPage();
                            } else {
                                isLoadingNextPage = false;
                            }
                        });
                    }
                }
            }
        });
    }

    private void findAll(View view){
        recyclerView = view.findViewById(R.id.recycler_history);
        back = view.findViewById(R.id.back_history);
        deleteAll = view.findViewById(R.id.deleteAll_history);
        back.setEnabled(false);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        adapter = new RadioAdapter(requireActivity(), new RadioAdapter.OnItemClickListener() {
            @Override
            public void onStationItemClick(int position) {
                if (!isVisibleToUser) return;

                if (playerViewModel.isInternetConnected()) {
                    if (playerViewModel.checkTypeInternet().equals("ok")) {
                        playerViewModel.setPlaying(adapter.getUUID(position));
                        playerViewModel.start();
                    } else {
                        Toast.makeText(requireActivity(), "Not correct internet type!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "Check internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }
    private void observeAndLoad(){
        viewModel.getHistoryList().observe(getViewLifecycleOwner(), list -> {
            back.setEnabled(true);
            switch (list.status){
                case LOADING:
                    if (adapter.getItemCount() > 0) {
                        handler.post(() -> {
                            if (isVisibleToUser && adapter != null) {
                                adapter.showLoading();
                            }
                        });
                    }
                    break;
                case SUCCESS:
                    handler.post(() -> {
                        if (isVisibleToUser && adapter != null) {
                            adapter.hideLoading();
                            List<RadioStation> data = list.data;
                            adapter.replaceAll(data);
                            isLoadingNextPage = false;
                        }
                    });
                    break;
                case ERROR:
                    handler.post(() -> {
                        if (isVisibleToUser && adapter != null) {
                            adapter.hideLoading();
                            if (!list.message.isEmpty() && !list.message.equals("")) {
                                if(list.message.equals("Лист пуст")){
                                    Toast.makeText(requireActivity(), "В истории ничего нет", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireActivity(), list.message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            isLoadingNextPage = false;
                        }
                    });
                    break;
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        recyclerView.setAdapter(null);
    }
}