package by.roman.worldradio0.ui.fragments.history;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.HistoryViewModel;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.fragments.timer.AlarmFragment;
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
            new AlertDialog.Builder(requireContext())
                    .setTitle(getResources().getString(R.string.clear_history))
                    .setMessage(getResources().getString(R.string.clear_history_text))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        adapter.clear();
                        viewModel.deleteAllHistory();
                        viewModel.resetState();
                        viewModel.loadStart();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), null)
                    .show();
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
                        playerViewModel.start(adapter.getStation(position));
                    } else {
                        Toast.makeText(requireActivity(), getResources().getString(R.string.not_correct_internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(int position) {
            }

            @Override
            public void onStationLongClick(int position) {
                showMenu(position);
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }
    private void showMenu(int position) {
        String[] options = {getResources().getString(R.string.schedule_playback), getResources().getString(R.string.delete_record)};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(adapter.getStation(position).getName());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    AlarmFragment fragment = AlarmFragment.newInstance(adapter.getStation(position).getStationUuid());
                    stateViewModel.openFullscreen(fragment);
                    break;
                case 1:
                    viewModel.deleteOneFromHistory(adapter.getStation(position).getStationUuid());
                    viewModel.resetState();
                    viewModel.loadStart();
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
    private void observeAndLoad(){
        playerViewModel.getSelectedCard().observe(getViewLifecycleOwner(), uuid -> {
            if(uuid == null) adapter.clearSelectedStation();
            adapter.setSelectedStationUuid(uuid);
        });
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
                            back.setEnabled(true);
                        }
                    });
                    break;
                case ERROR:
                    handler.post(() -> {
                        if (isVisibleToUser && adapter != null) {
                            adapter.hideLoading();
                            if (!list.message.isEmpty()) {
                                if(list.message.equals("Лист пуст")){
                                    Toast.makeText(requireActivity(), getResources().getString(R.string.no_history), Toast.LENGTH_SHORT).show();
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