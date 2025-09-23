package by.roman.worldradio0.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryActivity extends AppCompatActivity {
    private ImageView back;
    private RadioAdapter adapter;
    private RecyclerView recyclerView;
    private SettingsViewModel viewModel;
    private PlayerViewModel playerViewModel;
    private boolean isVisibleToUser = false;
    private boolean isLoadingNextPage = false;
    private ImageView deleteAll;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onResume() {
        super.onResume();
        isVisibleToUser = true;
        viewModel.resetState();
        viewModel.loadStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleToUser = false;
        handler.removeCallbacksAndMessages(null);
        viewModel.cancelPendingOperations();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findAll();
        initAll();
        observeAndLoad();
        back.setOnClickListener(v1 -> {
            finish();
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

    private void findAll(){
        recyclerView = findViewById(R.id.recycler_history);
        back = findViewById(R.id.back_history);
        deleteAll = findViewById(R.id.deleteAll_history);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        adapter = new RadioAdapter(this, new RadioAdapter.OnItemClickListener() {
            @Override
            public void onStationItemClick(int position) {
                if (!isVisibleToUser) return;

                if (playerViewModel.isInternetConnected()) {
                    if (playerViewModel.checkTypeInternet().equals("ok")) {
                        playerViewModel.setPlaying(adapter.getUUID(position));
                        playerViewModel.start();
                    } else {
                        Toast.makeText(getBaseContext(), "Not correct internet type!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Check internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void observeAndLoad(){
        viewModel.getHistoryList().observe(this, list -> {
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
                                    Toast.makeText(this, "В истории ничего нет", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, list.message, Toast.LENGTH_SHORT).show();
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
    public void onDestroy() { //onDestroyView
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        recyclerView.setAdapter(null);
    }
}