package by.roman.worldradio0.ui.fragments.filter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.view_models.FilterViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterFragment extends Fragment {

    private MaterialAutoCompleteTextView actvCountry;
    private MaterialAutoCompleteTextView actvTags;
    private MaterialAutoCompleteTextView actvLang;
    private MaterialAutoCompleteTextView actvName;
    private MaterialAutoCompleteTextView actvCodec;
    private MaterialToolbar toolbar;
    private TextView countText;
    private MaterialButton btnReset;
    private FilterViewModel viewModel;
    private StateViewModel stateViewModel;
    private Filter filter;
    private Chip chipAlphabet, chipRating, chipBitrate;
    private int currentSort = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findAllId(view);
        initAll();
        observeAndLoad();
        setupClickListeners();
        fillFields();
    }

    private void findAllId(View view){
        toolbar = view.findViewById(R.id.toolbar);
        countText = view.findViewById(R.id.StationCount);
        btnReset = view.findViewById(R.id.btnReset);

        chipAlphabet = view.findViewById(R.id.chipAlphabet);
        chipRating = view.findViewById(R.id.chipRating);
        chipBitrate = view.findViewById(R.id.chipBitrate);

        // Находим AutoCompleteTextView для каждого поля
        setupAutoCompleteFields(view);
    }

    private void setupAutoCompleteFields(View view) {
        // Страна
        TextInputLayout countryLayout = view.findViewById(R.id.filterCountry).findViewById(R.id.textInputLayout);
        actvCountry = (MaterialAutoCompleteTextView) countryLayout.getEditText();
        countryLayout.setHint("Страна");

        // Теги
        TextInputLayout tagsLayout = view.findViewById(R.id.filterTags).findViewById(R.id.textInputLayout);
        actvTags = (MaterialAutoCompleteTextView) tagsLayout.getEditText();
        tagsLayout.setHint("Теги");

        // Язык
        TextInputLayout langLayout = view.findViewById(R.id.filterLanguage).findViewById(R.id.textInputLayout);
        actvLang = (MaterialAutoCompleteTextView) langLayout.getEditText();
        langLayout.setHint("Язык");

        // Название
        TextInputLayout nameLayout = view.findViewById(R.id.filterName).findViewById(R.id.textInputLayout);
        actvName = (MaterialAutoCompleteTextView) nameLayout.getEditText();
        nameLayout.setHint("Название станции");

        // Кодек
        TextInputLayout codecLayout = view.findViewById(R.id.filterCodec).findViewById(R.id.textInputLayout);
        actvCodec = (MaterialAutoCompleteTextView) codecLayout.getEditText();
        codecLayout.setHint("Кодек");
    }

    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            stateViewModel.closeFullscreen();
        });

        btnReset.setOnClickListener(v -> {
            resetAllFilters();
        });

        chipAlphabet.setOnClickListener(v -> handleChipSelection(1));
        chipRating.setOnClickListener(v -> handleChipSelection(2));
        chipBitrate.setOnClickListener(v -> handleChipSelection(3));
    }

    private void handleChipSelection(int sortType) {
        if (currentSort == sortType) {
            currentSort = 0;
            chipAlphabet.setChecked(false);
            chipRating.setChecked(false);
            chipBitrate.setChecked(false);
        } else {
            currentSort = sortType;
            chipAlphabet.setChecked(sortType == 1);
            chipRating.setChecked(sortType == 2);
            chipBitrate.setChecked(sortType == 3);
        }
        filter.setSort(currentSort);
        viewModel.setFilters(filter);
        viewModel.loadCount();
    }

    private void resetAllFilters() {
        actvName.setText("");
        actvCountry.setText("");
        actvTags.setText("");
        actvLang.setText("");
        actvCodec.setText("");

        currentSort = 0;
        chipAlphabet.setChecked(false);
        chipRating.setChecked(false);
        chipBitrate.setChecked(false);
        filter.setCodec(null);
        filter.setName(null);
        filter.setTag(null);
        filter.setCountry(null);
        filter.setLang(null);
        viewModel.setFilters(filter);
        viewModel.loadCount();
    }

    private void handleSelection(String selectedItem, MaterialAutoCompleteTextView actv, String fieldType) {
        switch (fieldType) {
            case "country":
                filter.setCountry(selectedItem);
                break;
            case "tags":
                filter.setTag(selectedItem);
                break;
            case "language":
                filter.setLang(selectedItem);
                break;
            case "name":
                filter.setName(selectedItem);
                break;
            case "codec":
                filter.setCodec(selectedItem);
                break;
        }
        viewModel.setFilters(filter);
        viewModel.loadCount();
    }

    @SuppressLint("SetTextI18n")
    private void observeAndLoad() {
        viewModel.getCountFilteredStations().observe(getViewLifecycleOwner(), count -> {
            if (count == null) return;
            switch (count.status) {
                case SUCCESS:
                    if (count.data != null) {
                        String stationText = count.data + " станций найдено";
                        countText.setText(stationText);
                    }
                    break;
                case LOADING:
                    countText.setText("Загрузка...");
                    break;
                case ERROR:
                    countText.setText("Ошибка");
                    break;
            }
        });

        viewModel.loadCount();

        // Настройка автодополнения
        viewModel.getCountriesLive().observe(getViewLifecycleOwner(), list ->
                setupAutoComplete(actvCountry, list, "country"));
        viewModel.getTagsLive().observe(getViewLifecycleOwner(), list ->
                setupAutoComplete(actvTags, list, "tags"));
        viewModel.getLanguagesLive().observe(getViewLifecycleOwner(), list ->
                setupAutoComplete(actvLang, list, "language"));
        viewModel.getNamesLive().observe(getViewLifecycleOwner(), list ->
                setupAutoComplete(actvName, list, "name"));
        viewModel.getCodecsLive().observe(getViewLifecycleOwner(), list ->
                setupAutoComplete(actvCodec, list, "codec"));

        viewModel.loadAutocompleteData();
    }

    private void setupAutoComplete(MaterialAutoCompleteTextView actv, List<String> list, String fieldType) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                list
        );
        actv.setAdapter(adapter);
        actv.setThreshold(1);

        actv.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            handleSelection(selected, actv, fieldType);
        });
    }

    private void initAll() {
        viewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        filter = viewModel.getFilters();

        // Восстанавливаем состояние сортировки
        currentSort = filter.getSort();
        switch (currentSort) {
            case 1: chipAlphabet.setChecked(true); break;
            case 2: chipRating.setChecked(true); break;
            case 3: chipBitrate.setChecked(true); break;
        }
    }

    private void fillFields() {
        try {
            if (filter.getLang() != null) actvLang.setText(filter.getLang());
            if (filter.getCountry() != null) actvCountry.setText(filter.getCountry());
            if (filter.getTag() != null) actvTags.setText(filter.getTag());
            if (filter.getName() != null) actvName.setText(filter.getName());
            if (filter.getCodec() != null) actvCodec.setText(filter.getCodec());
        } catch (Exception e) {
            Log.e("FilterFragment", "Error filling fields", e);
        }
    }
}