package by.story_weaver.worldradiomonitoring.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import by.story_weaver.worldradiomonitoring.R;
import by.story_weaver.worldradiomonitoring.logic.adapters.RadioAdapter;
import by.story_weaver.worldradiomonitoring.logic.models.Station;
import by.story_weaver.worldradiomonitoring.logic.view_models.ViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewModel viewModel;
    private RadioAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findById(view);

        adapter = new RadioAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        viewModel.getTopClick().observe(getViewLifecycleOwner(), uiState -> {
            switch (uiState.status){
                case LOADING:
                    break;
                case SUCCESS:
                    adapter.setStations(uiState.data);
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), uiState.message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadTopClick(10);
    }

    private void findById(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
    }

}