package by.roman.worldradio0.ui.fragments.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.SettingsList;
import by.roman.worldradio0.business_logic.adapters.SettingsAdapter;
import by.roman.worldradio0.business_logic.settings.SettingsChangeListener;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.activities.AccountActivity;
import by.roman.worldradio0.ui.fragments.history.HistoryFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {
    private ImageButton history_button;
    private ImageButton log_out_button;
    private ImageButton edit_user_button;
    private RecyclerView recyclerView;
    private TextView textView;
    private TextView text_status;
    private ProgressBar loading;
    private SettingsViewModel viewModel;
    private StateViewModel stateViewModel;
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("SettingsFragment: performance", "onViewCreated started");
        findAllId(view);
        initAll();
        observeStatus();
        log_out_button.setOnClickListener(v -> {
            viewModel.logOut();
        });
        edit_user_button.setOnClickListener(v -> {

        });
        history_button.setOnClickListener(v -> {
            stateViewModel.openFullscreen(new HistoryFragment());
        });
        Log.v("SettingsFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAllId(@NonNull View view){
        recyclerView = view.findViewById(R.id.recyclerView_Settings);
        textView = view.findViewById(R.id.nameAccountView);
        loading = view.findViewById(R.id.progressBar_Settings);
        text_status = view.findViewById(R.id.text_loading_Settings);
        edit_user_button = view.findViewById(R.id.edit_user_settings);
        log_out_button = view.findViewById(R.id.log_out_settings);
        history_button = view.findViewById(R.id.history_button);
    }
    @SuppressLint("SetTextI18n")
    private void initAll(){
        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        SettingsAdapter adapter = new SettingsAdapter(SettingsList.getSettingsList(viewModel.getSettingsModel()), new SettingsChangeListener() {
            @Override
            public void onToggleChanged(@NonNull String key, boolean isChecked) {
                viewModel.toggleChange(key,isChecked);
            }

            @Override
            public void onSwitchChanged(@NonNull String key, int pos){
                viewModel.switchChange(key,pos);
            }

            @Override
            public void onClickChanged(@NonNull String key) {
                viewModel.clickChange(key);
            }

            @Override
            public void onSliderChanged(@NonNull String key, int value) {
                viewModel.sliderChange(key,value);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            text_status.setVisibility(INVISIBLE);
            text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(),R.color.white));
        };
        textView.setText("Hi, " +viewModel.getUserData().getLogin());
    }
    @SuppressLint("SetTextI18n")
    private void observeStatus(){
        viewModel.getStationsCount().observe(getViewLifecycleOwner(),count -> {
            switch (count.status){
                case LOADING:
                    if(count.data == 0){
                        text_status.setVisibility(VISIBLE);
                        loading.setVisibility(VISIBLE);
                        text_status.setText("Загрузка данных...");
                        loading.setIndeterminate(true);
                        handler.removeCallbacks(runnable);
                    } else {
                        text_status.setText("Сохранение данных...");
                        loading.setIndeterminate(false);
                        text_status.setText(count.data + "%");
                        loading.setProgress(count.data);
                    }
                    break;

                case SUCCESS:
                    loading.setVisibility(INVISIBLE);
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(),R.color.green));
                    text_status.setText("Данных сохранены");
                    handler.postDelayed(runnable,5000);
                    break;

                case ERROR:
                    loading.setVisibility(INVISIBLE);
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(),R.color.red));
                    text_status.setText("Ошибка обновления");
                    handler.postDelayed(runnable,5000);
                    break;
            }
        });
        viewModel.getGettingStatus().observe(getViewLifecycleOwner(), i -> {
            switch (i.status){
                case LOADING:
                    handler.removeCallbacks(runnable);
                    loading.setVisibility(INVISIBLE);
                    text_status.setVisibility(VISIBLE);
                    text_status.setText("Загрузка данных");
                    break;

                case SUCCESS:
                    text_status.setText("Данные загружены");
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.green));
                    handler.postDelayed(runnable,5000);
                    break;

                case ERROR:
                    text_status.setText("Ошибка загрузки");
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.red));
                    handler.postDelayed(runnable,5000);
                    break;
            }
        });
        viewModel.getSendingStatus().observe(getViewLifecycleOwner(), i -> {
            switch (i.status){
                case LOADING:
                    loading.setVisibility(INVISIBLE);
                    text_status.setVisibility(VISIBLE);
                    text_status.setText("Отправка данных");
                    handler.removeCallbacks(runnable);
                    break;

                case SUCCESS:
                    text_status.setText("Данные отправлены");
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.green));
                    handler.postDelayed(runnable,5000);
                    break;

                case ERROR:
                    text_status.setText("Ошибка отправки");
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.red));
                    handler.postDelayed(runnable,5000);
                    break;
            }
        });
        viewModel.getTimeToLeave().observe(getViewLifecycleOwner(), timeToLeave -> {
            requireActivity().startActivity(new Intent(requireContext(), AccountActivity.class));
            requireActivity().finish();
        });
    }
}