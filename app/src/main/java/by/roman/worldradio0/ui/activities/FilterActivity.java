package by.roman.worldradio0.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import by.roman.worldradio0.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {
    private MaterialAutoCompleteTextView actvCountry;
    private MaterialAutoCompleteTextView actvTags;
    private MaterialAutoCompleteTextView actvLang;
    private Spinner spinnerSortBy;
    private ImageView backButton;
    private ImageView deleteCountry;
    private ImageView deleteTags;
    private ImageView deleteLang;
    private TextView count;
    private int savedSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            findAllId();
            return insets;
        });
    }
    private void findAllId(){
//        actvCountry = findViewById(R.id.actvCountry);
//        actvTags = findViewById(R.id.actvStyle);
//        actvLang = findViewById(R.id.actvLang);
//        backButton = findViewById(R.id.backButtonFilterView);
//        count = findViewById(R.id.countStation);
//        deleteCountry = findViewById(R.id.deleteCountryFilter);
//        deleteTags = findViewById(R.id.deleteStyleFilter);
//        deleteLang = findViewById(R.id.deleteLangFilter);
//        spinnerSortBy = findViewById(R.id.sortType);
    }
}