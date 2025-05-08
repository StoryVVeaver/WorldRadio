package by.roman.worldradio0.ui.activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.view_models.FilterViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {
    private MaterialAutoCompleteTextView actvCountry;
    private MaterialAutoCompleteTextView actvTags;
    private MaterialAutoCompleteTextView actvLang;
    private ImageView backButton;
    private TextView deleteCountry;
    private TextView deleteTags;
    private TextView deleteLang;
    private TextView countText;
    private FilterViewModel viewModel;
    private Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findAllId();
        initAll();
        observeAndLoad();
        buttons();
        fillFields();
    }
    private void fillFields(){
        if(filter.getLang() != null){
            actvLang.setText(filter.getLang());
            deleteLang.setVisibility(VISIBLE);
        }
        if (filter.getCountry() != null){
            actvCountry.setText(filter.getCountry());
            deleteCountry.setVisibility(VISIBLE);
        }
        if (filter.getTag() != null){
            actvTags.setText(filter.getTag());
            deleteTags.setVisibility(VISIBLE);
        }
    }
    private void buttons(){
        backButton.setOnClickListener(v -> {
            finish();
        });
        deleteCountry.setOnClickListener(v -> {
            filter.setCountry(null);
            viewModel.setFilters(filter);
            viewModel.loadCount();
            actvCountry.setText("");
            deleteCountry.setVisibility(INVISIBLE);
        });
        deleteLang.setOnClickListener(v -> {
            filter.setLang(null);
            viewModel.setFilters(filter);
            viewModel.loadCount();
            actvLang.setText("");
            deleteLang.setVisibility(INVISIBLE);
        });
        deleteTags.setOnClickListener(v -> {
            filter.setTag(null);
            viewModel.setFilters(filter);
            viewModel.loadCount();
            actvTags.setText("");
            deleteTags.setVisibility(INVISIBLE);
        });
    }
    private void handleSelection(int type,  String selectedItem) {
        switch (type){
            case 1:
                filter.setCountry(selectedItem);
                deleteCountry.setVisibility(VISIBLE);
                break;
            case 2:
                filter.setTag(selectedItem);
                deleteTags.setVisibility(VISIBLE);
                break;
            case 3:
                filter.setLang(selectedItem);
                deleteLang.setVisibility(VISIBLE);
                break;
        }
        viewModel.setFilters(filter);
        viewModel.loadCount();
    }
    @SuppressLint("SetTextI18n")
    private void observeAndLoad() {
        viewModel.getCountFilteredStations().observe(this, count -> {
            if (count == null) return;
            switch (count.status) {
                case LOADING:
                case ERROR:
                    break;
                case SUCCESS:
                    if (count.data != null && countText != null) {
                        countText.setText("Подходит " + count.data);
                    }
                    break;
            }
        });
        viewModel.loadCount();

    }
    private void setupAutoComplete(@NonNull MaterialAutoCompleteTextView actv,List<String> list, int type){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(actv.getContext(), android.R.layout.simple_dropdown_item_1line, list);
        actv.setAdapter(adapter);

        actv.setThreshold(2);
        actv.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            handleSelection(type, selected);
        });
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(FilterViewModel.class);
        filter = viewModel.getFilters();
        setupAutoComplete(actvCountry,viewModel.getCountries(),1);
        setupAutoComplete(actvTags,viewModel.getTags(),2);
        setupAutoComplete(actvLang,viewModel.getLanguage(),3);
    }
    @SuppressLint("SetTextI18n")
    private void findAllId(){
        View filter_countryView = findViewById(R.id.filterCountry);
        View filter_tagsView = findViewById(R.id.filterTags);
        View filter_langView = findViewById(R.id.filterLanguage);
        actvCountry = filter_countryView.findViewById(R.id.autoComplete);
        actvTags = filter_tagsView.findViewById(R.id.autoComplete);
        actvLang = filter_langView.findViewById(R.id.autoComplete);
        backButton = findViewById(R.id.btnBack);
        countText = findViewById(R.id.StationCount);
        deleteCountry = filter_countryView.findViewById(R.id.delete);
        deleteCountry.setVisibility(INVISIBLE);
        deleteTags = filter_tagsView.findViewById(R.id.delete);
        deleteTags.setVisibility(INVISIBLE);
        deleteLang = filter_langView.findViewById(R.id.delete);
        deleteLang.setVisibility(INVISIBLE);
        TextView countryFilter = filter_countryView.findViewById(R.id.nameFilter);
        countryFilter.setText("Country");
        TextView tagFilter = filter_tagsView.findViewById(R.id.nameFilter);
        tagFilter.setText("Tags");
        TextView langFilter = filter_langView.findViewById(R.id.nameFilter);
        langFilter.setText("Lang");
    }
    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}