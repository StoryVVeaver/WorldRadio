package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListFragment extends Fragment {
    private PlayerViewModel playerViewModel;
    private FilterViewModel viewModel;
    private RecyclerView recyclerView;
    private RadioAdapter adapter;
    private boolean isLoadingNextPage = false;
    private boolean resume = false;
    private boolean activityCalled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume(){
        super.onResume();
        if(activityCalled){
            resume = true;
            viewModel.setPage(0);
            viewModel.loadStart();
            activityCalled = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findAllId(view);
        initAll();
        observeAndLoad();

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
    private void initAll(){
        adapter = new RadioAdapter(getContext(), new RadioAdapter.OnItemClickListener() {
            @Override
            public void onStationItemClick(int position) {
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
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(FilterViewModel.class);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
    }
    private void findAllId(View view){
        recyclerView = view.findViewById(R.id.list_recycler);
    }
    private void observeAndLoad() {
        viewModel.getFilteredStations().observe(getViewLifecycleOwner(), stations -> {
            switch (stations.status) {
                case LOADING:
                    if (adapter.getItemCount() > 0) {
                        adapter.showLoading();
                    }
                    break;
                case SUCCESS:
                    adapter.hideLoading();
                    List<RadioStation> Data = stations.data;
                    if(resume){
                        adapter.replaceAll(Data);
                        resume = false;
                        viewModel.setIsLastPage(false);
                    } else {
                        adapter.addStations(Data.subList(adapter.getItemCount(), Data.size()));
                    }
                    isLoadingNextPage = false;
                    break;
                case ERROR:
                    adapter.hideLoading();
                    if (!stations.message.isEmpty()){
                        Toast.makeText(getContext(), stations.message, Toast.LENGTH_SHORT).show();
                    }
                    isLoadingNextPage = false;
                    break;
            }
        });
    }
}