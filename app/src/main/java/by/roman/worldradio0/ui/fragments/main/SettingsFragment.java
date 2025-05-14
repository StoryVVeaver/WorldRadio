package by.roman.worldradio0.ui.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import by.roman.worldradio0.business_logic.data.models.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.SettingsItem;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("SettingsFragment: performance", "onViewCreated started");
        findAllId(view);
        //TODO
        list(view);
        Log.v("SettingsFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAllId(View view){

    }
    private void list(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_Settings);

        List<SettingsGroup> groups = new ArrayList<>();

        List<SettingsItem> languageItems = new ArrayList<>();
        languageItems.add(new SettingsItem("Русский", true));
        languageItems.add(new SettingsItem("English", false));
        groups.add(new SettingsGroup("Язык", languageItems));

        List<SettingsItem> themeItems = new ArrayList<>();
        themeItems.add(new SettingsItem("Светлая", false));
        themeItems.add(new SettingsItem("Тёмная", true));
        themeItems.add(new SettingsItem("Системная", false));
        groups.add(new SettingsGroup("Тема", themeItems));

        List<SettingsItem> radioItems = new ArrayList<>();
        radioItems.add(new SettingsItem("Jazz FM", false));
        radioItems.add(new SettingsItem("Rock Radio", false));
        groups.add(new SettingsGroup("Избранные радиостанции", radioItems));


        SettingsAdapter adapter = new SettingsAdapter(groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }
}