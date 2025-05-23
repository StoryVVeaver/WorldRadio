package by.roman.worldradio0.ui.activities;

import static android.view.View.INVISIBLE;
import static android.view.View.NO_ID;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.List;
import java.util.Objects;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.view_models.FilterViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {
    private MaterialAutoCompleteTextView actvCountry;
    private MaterialAutoCompleteTextView actvTags;
    private MaterialAutoCompleteTextView actvLang;
    private MaterialAutoCompleteTextView actvName;
    private MaterialAutoCompleteTextView actvCodec;
    private ImageView backButton;
    private TextView deleteCountry;
    private TextView deleteTags;
    private TextView deleteLang;
    private TextView deleteCodec;
    private TextView deleteName;
    private TextView countText;
    private FilterViewModel viewModel;
    private Filter filter;
    private Chip chip1;
    private Chip chip2;
    private Chip chip3;
    private int pos;

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
        long startTime = System.nanoTime();
        Log.v("FilterActivity: performance", "onCreated started");
        findAllId();
        initAll();
        observeAndLoad();
        buttons();
        fillFields();
        Log.v("FilterActivity: performance", "onCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
        //TODO asynch loading
    }
    private void fillFields(){
        try{
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
            if(filter.getName() != null){
                actvName.setText(filter.getName());
                deleteName.setVisibility(VISIBLE);
            }
            if(filter.getCodec() != null){
                actvCodec.setText(filter.getCodec());
                deleteCodec.setVisibility(VISIBLE);
            }
        } catch (Exception e){
            Log.e("FilterActivity", Objects.requireNonNull(e.getMessage()));
        }
    }
    private void buttons(){
        try {
            chip1.setOnClickListener(v -> {
                if(pos == 1){
                    pos = 0;
                } else pos = 1;
                chip2.setChecked(false);
                chip3.setChecked(false);
                filter.setSort(pos);
                viewModel.setFilters(filter);
            });
            chip2.setOnClickListener(v -> {
                if(pos == 2){
                    pos = 0;
                } else pos = 2;
                chip1.setChecked(false);
                chip3.setChecked(false);
                filter.setSort(pos);
                viewModel.setFilters(filter);
            });
            chip3.setOnClickListener(v -> {
                if(pos == 3){
                    pos = 0;
                } else pos = 3;
                chip1.setChecked(false);
                chip2.setChecked(false);
                filter.setSort(pos);
                viewModel.setFilters(filter);
            });
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
            deleteName.setOnClickListener(v -> {
                filter.setName(null);
                viewModel.setFilters(filter);
                viewModel.loadCount();
                actvName.setText("");
                deleteName.setVisibility(INVISIBLE);
            });
            deleteCodec.setOnClickListener(v -> {
                filter.setCodec(null);
                viewModel.setFilters(filter);
                viewModel.loadCount();
                actvCodec.setText("");
                deleteCodec.setVisibility(INVISIBLE);
            });
        } catch (Exception e) {
            Log.e("FilterActivity", Objects.requireNonNull(e.getMessage()));
        }
    }
    private void handleSelection(int type,  String selectedItem) {
        try {
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
                case 4:
                    filter.setName(selectedItem);
                    deleteName.setVisibility(VISIBLE);
                    break;
                case 5:
                    filter.setCodec(selectedItem);
                    deleteCodec.setVisibility(VISIBLE);
                    break;
            }
            viewModel.setFilters(filter);
            viewModel.loadCount();
        } catch (Exception e) {
            Log.e("FilterActivity", Objects.requireNonNull(e.getMessage()));
        }
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
        try {
            viewModel = new ViewModelProvider(this).get(FilterViewModel.class);
            filter = viewModel.getFilters();
            setupAutoComplete(actvCountry,viewModel.getCountries(),1);
            setupAutoComplete(actvTags,viewModel.getTags(),2);
            setupAutoComplete(actvLang,viewModel.getLanguage(),3);
            setupAutoComplete(actvName,viewModel.getNames(),4);
            setupAutoComplete(actvCodec,viewModel.getCodecs(),5);
            switch (filter.getSort()){
                case 1:
                    chip1.setChecked(true);
                    break;

                case 2:
                    chip2.setChecked(true);
                    break;

                case 3:
                    chip3.setChecked(true);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("FilterActivity", Objects.requireNonNull(e.getMessage()));
        }
    }
    @SuppressLint("SetTextI18n")
    private void findAllId(){
        chip1 = findViewById(R.id.chipAlphabet);
        chip2 = findViewById(R.id.chipRating);
        chip3 = findViewById(R.id.chipBitrate);
        View filter_countryView = findViewById(R.id.filterCountry);
        View filter_tagsView = findViewById(R.id.filterTags);
        View filter_langView = findViewById(R.id.filterLanguage);
        View filter_nameView = findViewById(R.id.filterName);
        View filter_codecView = findViewById(R.id.filterCodec);
        actvCountry = filter_countryView.findViewById(R.id.autoComplete);
        actvTags = filter_tagsView.findViewById(R.id.autoComplete);
        actvLang = filter_langView.findViewById(R.id.autoComplete);
        actvName = filter_nameView.findViewById(R.id.autoComplete);
        actvCodec = filter_codecView.findViewById(R.id.autoComplete);
        backButton = findViewById(R.id.btnBack);
        countText = findViewById(R.id.StationCount);
        deleteCountry = filter_countryView.findViewById(R.id.delete);
        deleteCountry.setVisibility(INVISIBLE);
        deleteTags = filter_tagsView.findViewById(R.id.delete);
        deleteTags.setVisibility(INVISIBLE);
        deleteLang = filter_langView.findViewById(R.id.delete);
        deleteLang.setVisibility(INVISIBLE);
        deleteName = filter_nameView.findViewById(R.id.delete);
        deleteName.setVisibility(INVISIBLE);
        deleteCodec = filter_codecView.findViewById(R.id.delete);
        deleteCodec.setVisibility(INVISIBLE);
        TextView countryFilter = filter_countryView.findViewById(R.id.nameFilter);
        countryFilter.setText("Country");
        TextView tagFilter = filter_tagsView.findViewById(R.id.nameFilter);
        tagFilter.setText("Tags");
        TextView langFilter = filter_langView.findViewById(R.id.nameFilter);
        langFilter.setText("Lang");
        TextView nameFilter = filter_nameView.findViewById(R.id.nameFilter);
        nameFilter.setText("Name");
        TextView codecFilter = filter_codecView.findViewById(R.id.nameFilter);
        codecFilter.setText("Codec");
    }
}