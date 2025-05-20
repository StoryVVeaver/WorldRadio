package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.SettingsAdapter;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckWIthSliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextButtonItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextItem;
import by.roman.worldradio0.business_logic.settings.SettingsChangeListener;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SettingsViewModel viewModel;

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
        Log.v("SettingsFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAllId(@NonNull View view){
        recyclerView = view.findViewById(R.id.recyclerView_Settings);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        SettingsAdapter adapter = new SettingsAdapter(viewModel.getSettingsList(), new SettingsChangeListener() {
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
    }
}