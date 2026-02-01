package by.roman.worldradio0.ui.fragments.filter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import by.roman.worldradio0.business_logic.LocationUtil;
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
    private MaterialToolbar toolbar;
    private TextView countText;
    private MaterialButton btnReset;
    private FilterViewModel viewModel;
    private StateViewModel stateViewModel;
    private Filter filter;
    private Chip chipAlphabet, chipRating, chipBitrate;
    private int currentSort = 0;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

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
        btnReset = view.findViewById(R.id.btnReset);

        chipAlphabet = view.findViewById(R.id.chipAlphabet);
        chipRating = view.findViewById(R.id.chipRating);
        chipBitrate = view.findViewById(R.id.chipBitrate);

        setupAutoCompleteFields(view);
    }

    private void setupAutoCompleteFields(View view) {
        TextInputLayout countryLayout = view.findViewById(R.id.filterCountry).findViewById(R.id.textInputLayout);
        actvCountry = (MaterialAutoCompleteTextView) countryLayout.getEditText();
        countryLayout.setHint(getResources().getString(R.string.country));

        TextInputLayout tagsLayout = view.findViewById(R.id.filterTags).findViewById(R.id.textInputLayout);
        actvTags = (MaterialAutoCompleteTextView) tagsLayout.getEditText();
        tagsLayout.setHint(getResources().getString(R.string.tag));

        TextInputLayout langLayout = view.findViewById(R.id.filterLanguage).findViewById(R.id.textInputLayout);
        actvLang = (MaterialAutoCompleteTextView) langLayout.getEditText();
        langLayout.setHint(getResources().getString(R.string.lang));

        TextInputLayout nameLayout = view.findViewById(R.id.filterName).findViewById(R.id.textInputLayout);
        actvName = (MaterialAutoCompleteTextView) nameLayout.getEditText();
        nameLayout.setHint(getResources().getString(R.string.name));
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
        if (currentSort != sortType) {
            currentSort = sortType;
            chipAlphabet.setChecked(sortType == 1);
            chipRating.setChecked(sortType == 2);
            chipBitrate.setChecked(sortType == 3);
            filter.setSort(currentSort);
            viewModel.setFilters(filter);
        }
    }

    private void resetAllFilters() {
        actvName.setText("");
        actvCountry.setText("");
        actvTags.setText("");
        actvLang.setText("");

        chipAlphabet.setChecked(false);
        chipRating.setChecked(false);
        chipBitrate.setChecked(false);
        filter.setCodec(null);
        filter.setName(null);
        filter.setTag(null);
        filter.setCountry(null);
        filter.setLang(null);
        viewModel.setFilters(filter);
    }

    private void handleSelection(String selectedItem, MaterialAutoCompleteTextView actv, String fieldType) {
        actv.clearFocus();
        switch (fieldType) {
            case "country":
                filter.setCountry(LocationUtil.getIsoFromCountryName(selectedItem));
                break;
            case "tags":
                filter.setTag(selectedItem.toLowerCase().trim());
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
        actv.dismissDropDown();
    }

    @SuppressLint("SetTextI18n")
    private void observeAndLoad() {
        viewModel.getCountriesLive().observe(getViewLifecycleOwner(), list -> setupAutoComplete(actvCountry, list, "country"));
        viewModel.getLanguagesLive().observe(getViewLifecycleOwner(), list -> setupAutoComplete(actvLang, list, "language"));

        viewModel.getTagsLive().observe(getViewLifecycleOwner(), list -> updateAdapter(actvTags, list));
        viewModel.getNamesLive().observe(getViewLifecycleOwner(), list -> updateAdapter(actvName, list));

        actvName.addTextChangedListener(new SimpleTextWatcher(s -> {
            filter.setName(s.isEmpty() ? null : s);
            viewModel.setFilters(filter);
            debounceSearch(() -> viewModel.loadNames(s));
        }));

        actvTags.addTextChangedListener(new SimpleTextWatcher(s -> {
            String lowerTag = s.toLowerCase().trim();
            filter.setTag(lowerTag.isEmpty() ? null : lowerTag);
            viewModel.setFilters(filter);
            debounceSearch(() -> viewModel.loadTags(s));
        }));

        viewModel.loadAutocompleteData();
    }

    private void debounceSearch(Runnable runnable) {
        searchHandler.removeCallbacks(searchRunnable);
        searchRunnable = runnable;
        searchHandler.postDelayed(searchRunnable, 500);
    }

    private void updateAdapter(MaterialAutoCompleteTextView actv, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, list);
        actv.setAdapter(adapter);
        if (actv.hasFocus() && !list.isEmpty()) {
            actv.showDropDown();
        }
    }

    private interface TextChangedListener {
        void onTextChanged(String s);
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final TextChangedListener listener;
        public SimpleTextWatcher(TextChangedListener listener) { this.listener = listener; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { listener.onTextChanged(s.toString()); }
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
            if (filter.getCountry() != null) actvCountry.setText(LocationUtil.getCountryNameFromIso(filter.getCountry()));
            if (filter.getTag() != null) actvTags.setText(filter.getTag());
            if (filter.getName() != null) actvName.setText(filter.getName());
        } catch (Exception e) {
            Log.e("FilterFragment", "Error filling fields", e);
        }
    }
}