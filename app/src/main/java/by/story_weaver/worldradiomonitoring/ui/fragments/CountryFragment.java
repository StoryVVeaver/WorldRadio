package by.story_weaver.worldradiomonitoring.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import by.story_weaver.worldradiomonitoring.R;
import by.story_weaver.worldradiomonitoring.logic.LocationUtil;
import by.story_weaver.worldradiomonitoring.logic.adapters.CountryAdapter;
import by.story_weaver.worldradiomonitoring.logic.models.CodesModel;
import by.story_weaver.worldradiomonitoring.logic.models.FilterStation;
import by.story_weaver.worldradiomonitoring.logic.view_models.ViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CountryFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button saveButton;
    private FrameLayout progressBarSaveFilter;
    private EditText searchInput;
    private CountryAdapter adapter;
    private ViewModel viewModel;

    private List<CodesModel> allCountries = new ArrayList<>();
    private List<FilterStation> alreadySelectedIso = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_country, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findById(view);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        initRecycler();
        initSearch();

        observeCountries();

        viewModel.loadCountryCodes();
        viewModel.loadStationFilter();

        saveButton.setOnClickListener(v -> {
            List<FilterStation> selected = adapter.getSelectedCountriesIso();
            for (FilterStation i: selected){
                Log.v("CountryFrag", i.getCode());
            }
            viewModel.saveStationFilter(selected);
        });
    }

    private void findById(View view) {
        progressBarSaveFilter = view.findViewById(R.id.progress_overlay);
        recyclerView = view.findViewById(R.id.country_recycler);
        saveButton = view.findViewById(R.id.save_button);
        searchInput = view.findViewById(R.id.search_input);
    }

    private void initRecycler() {
        adapter = new CountryAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void initSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String query) {
        List<CodesModel> filtered = new ArrayList<>();

        for (CodesModel model : allCountries) {
            String name = LocationUtil.getCountryNameFromIso(model.getCountryCode());
            if (name.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(model);
            }
        }

        adapter.updateList(filtered);
    }

    private void observeCountries() {
        viewModel.setStationFilter().observe(getViewLifecycleOwner(), state -> {
            switch (state.status){
                case LOADING:
                    saveButton.setText("");
                    progressBarSaveFilter.setVisibility(VISIBLE);
                    break;
                case SUCCESS:
                    Toast.makeText(requireActivity(), getResources().getText(R.string.save), Toast.LENGTH_SHORT).show();
                    progressBarSaveFilter.setVisibility(GONE);
                    saveButton.setText(getResources().getText(R.string.save));
                    Log.v("CountryFrag", " " + state.data);
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), state.message, Toast.LENGTH_SHORT).show();
                    Log.e("CountryFrag", "set station filter: " + state.message);
                    progressBarSaveFilter.setVisibility(GONE);
                    saveButton.setText(getResources().getText(R.string.save));
                    break;
            }
        });
        viewModel.getCountryCodes().observe(getViewLifecycleOwner(), state -> {
            switch (state.status){
                case LOADING:
                    break;
                case SUCCESS:
                    allCountries = state.data;
                    adapter.updateList(allCountries);
                    markAlreadySelected();
                    break;
                case ERROR:
                    Log.e("CountryFrag", "country codes: " + state.message);
                    break;
            }
        });
        viewModel.getStationFilter().observe(getViewLifecycleOwner(), state -> {
            switch (state.status){
                case LOADING:
                    break;
                case SUCCESS:
                    alreadySelectedIso.clear();
                    alreadySelectedIso.addAll(state.data);
                    markAlreadySelected();
                    break;
                case ERROR:
                    Log.e("CountryFrag", "get station filter: " + state.message);
                    break;
            }
        });
    }

    private void markAlreadySelected() {
        if (adapter != null && alreadySelectedIso != null) {
            adapter.setSelectedCountries(alreadySelectedIso);
        }
    }
}
