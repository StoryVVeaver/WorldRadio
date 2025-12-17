package by.story_weaver.worldradiomonitoring.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import by.story_weaver.worldradiomonitoring.R;
import by.story_weaver.worldradiomonitoring.logic.adapters.RadioAdapter;
import by.story_weaver.worldradiomonitoring.logic.models.ClickModel;
import by.story_weaver.worldradiomonitoring.logic.models.Station;
import by.story_weaver.worldradiomonitoring.logic.view_models.ViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";

    private ViewModel viewModel;
    private RadioAdapter adapter;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;

    private TextView tvUsers, tvCountries, tvLanguages, tvStates, tvTags, tvFilters;
    private ProgressBar pbUsers, pbCountries, pbLanguages, pbStates, pbTags, pbFilters;
    private ProgressBar pbStations;


    private final LinkedHashMap<String, Station> stationMap = new LinkedHashMap<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        findViews(view);
        initRecycler(view);
        observeData();
        initLoad();

        refreshRunnable = () -> {
            Log.d(TAG, "Запуск периодической подгрузки кликов");
            viewModel.loadClicks();
            handler.postDelayed(refreshRunnable, 60000);
        };
        handler.post(refreshRunnable);
    }

    private void findViews(View v) {
        tvUsers = v.findViewById(R.id.tvUsers);
        tvCountries = v.findViewById(R.id.tvCountries);
        tvLanguages = v.findViewById(R.id.tvLanguages);
        tvStates = v.findViewById(R.id.tvStates);
        tvTags = v.findViewById(R.id.tvTags);
        tvFilters = v.findViewById(R.id.tvFilters);
        pbUsers = v.findViewById(R.id.pbUsers);
        pbCountries = v.findViewById(R.id.pbCountries);
        pbLanguages = v.findViewById(R.id.pbLanguages);
        pbStates = v.findViewById(R.id.pbStates);
        pbTags = v.findViewById(R.id.pbTags);
        pbFilters = v.findViewById(R.id.pbFilters);
        pbStations = v.findViewById(R.id.pbStations);
    }

    private void initRecycler(View v) {
        adapter = new RadioAdapter();
        RecyclerView rv = v.findViewById(R.id.recyclerStations);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
    }

    private void initLoad() {
        viewModel.loadCountUsers();
        viewModel.loadCountryCodes();
        viewModel.loadLanguages();
        viewModel.loadStates();
        viewModel.loadTags();
        viewModel.loadStationFilter();
        viewModel.loadClicks();
    }

    private void observeData() {
        viewModel.getCountUsers().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbUsers.setVisibility(View.VISIBLE);
                    tvUsers.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    pbUsers.setVisibility(View.GONE);
                    tvUsers.setVisibility(View.VISIBLE);
                    tvUsers.setText(String.valueOf(state.data));
                    break;
                case ERROR:
                    pbUsers.setVisibility(View.GONE);
                    tvUsers.setVisibility(View.VISIBLE);
                    tvUsers.setText("-");
                    break;
            }
        });
        viewModel.getCountryCodes().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbCountries.setVisibility(View.VISIBLE);
                    tvCountries.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    pbCountries.setVisibility(View.GONE);
                    tvCountries.setVisibility(View.VISIBLE);
                    tvCountries.setText(String.valueOf(state.data.size()));
                    break;
                case ERROR:
                    pbCountries.setVisibility(View.GONE);
                    tvCountries.setVisibility(View.VISIBLE);
                    tvCountries.setText("-");
                    break;
            }
        });
        viewModel.getLang().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbLanguages.setVisibility(View.VISIBLE);
                    tvLanguages.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    pbLanguages.setVisibility(View.GONE);
                    tvLanguages.setVisibility(View.VISIBLE);
                    tvLanguages.setText(String.valueOf(state.data.size()));
                    break;
                case ERROR:
                    pbLanguages.setVisibility(View.GONE);
                    tvLanguages.setVisibility(View.VISIBLE);
                    tvLanguages.setText("-");
                    break;
            }
        });
        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbStates.setVisibility(View.VISIBLE);
                    tvStates.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    pbStates.setVisibility(View.GONE);
                    tvStates.setVisibility(View.VISIBLE);
                    tvStates.setText(String.valueOf(state.data.size()));
                    break;
                case ERROR:
                    pbStates.setVisibility(View.GONE);
                    tvStates.setVisibility(View.VISIBLE);
                    tvStates.setText("-");
                    break;
            }
        });
        viewModel.getTag().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbTags.setVisibility(View.VISIBLE);
                    tvTags.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    pbTags.setVisibility(View.GONE);
                    tvTags.setVisibility(View.VISIBLE);
                    tvTags.setText(String.valueOf(state.data.size()));
                    break;
                case ERROR:
                    pbTags.setVisibility(View.GONE);
                    tvTags.setVisibility(View.VISIBLE);
                    tvTags.setText("-");
                    break;
            }
        });
        viewModel.getStationFilter().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbFilters.setVisibility(View.VISIBLE);
                    tvFilters.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    pbFilters.setVisibility(View.GONE);
                    tvFilters.setVisibility(View.VISIBLE);
                    tvFilters.setText(String.valueOf(state.data.size()));
                    break;
                case ERROR:
                    pbFilters.setVisibility(View.GONE);
                    tvFilters.setVisibility(View.VISIBLE);
                    tvFilters.setText("-");
                    break;
            }
        });
        viewModel.getClick().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    pbStations.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    pbStations.setVisibility(View.GONE);
                    handleClicks(state.data);
                    break;
                case ERROR:
                    pbStations.setVisibility(View.GONE);
                    break;
            }
        });
        viewModel.getStationByUUID().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case SUCCESS:
                    Station station = Objects.requireNonNull(state.data).get(0);
                    stationMap.put(station.getStationUuid(), station);
                    List<Station> result = new ArrayList<>(stationMap.values());
                    Collections.reverse(result);
                    adapter.setStations(result);
                    break;
                case ERROR:
                case LOADING:
                    break;
            }
        });
    }
    private void handleClicks(List<ClickModel> clicks) {
        if (clicks == null || clicks.isEmpty()) {
            return;
        }

        clicks.sort(Comparator.comparingLong(ClickModel::getTimeMillis));

        List<ClickModel> lastClicks = clicks.size() > 20
                ? clicks.subList(clicks.size() - 20, clicks.size())
                : clicks;

        for (ClickModel click : lastClicks) {
            if (stationMap.containsKey(click.getUUID())) {
                continue;
            }

            viewModel.loadStation(click.getUUID());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshRunnable);
    }
}
