package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import by.roman.worldradio0.business_logic.view_models.MainViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private MainViewModel viewModel;
    private RadioAdapter adapter;
    private ImageView timerButton;
    private RecyclerView recyclerView;
    private int position;

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
        findAllId(view);
        adapter = new RadioAdapter(getContext(), position -> {
            Toast.makeText(getContext(), "Нажат элемент " + position, Toast.LENGTH_SHORT).show();
            this.position = position;
        });
        timerButton.setOnClickListener(v -> {
            Log.d("HomeFragment","Start loading");
            //viewModel.loadFromAPI();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getAllStations().observe(getViewLifecycleOwner(),stations ->{
            switch (stations.status) {
                case LOADING:
                    Log.d("HomeFragment","Loading");
                    //showLoadingSpinner();
                    break;
                case SUCCESS:
                    Log.d("HomeFragment","Success");
                    adapter.setStations(stations.data);
                    break;
                case ERROR:
                    Log.d("HomeFragment","Error");
                    //showError(stations.message);
                    break;
            }
        });
    }
    private void findAllId(View view){
        timerButton = view.findViewById(R.id.timerButtonView);
        recyclerView = view.findViewById(R.id.cardView);
    }
}