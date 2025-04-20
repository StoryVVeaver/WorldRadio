package by.roman.worldradio0.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import by.roman.worldradio0.R;
import by.roman.worldradio0.ui.fragments.main.FavoriteFragment;
import by.roman.worldradio0.ui.fragments.main.FilterFragment;
import by.roman.worldradio0.ui.fragments.main.GlobeFragment;
import by.roman.worldradio0.ui.fragments.main.HomeFragment;
import by.roman.worldradio0.ui.fragments.main.SettingsFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            findAllId();

            bottomNavigationView.setOnItemSelectedListener(item -> {
                switch (Objects.requireNonNull(item.getTitle()).toString()) {
                    case "Globe":
                        loadFragment(new GlobeFragment());
                        return true;
                    case "Favorite":
                        loadFragment(new FavoriteFragment());
                        return true;
                    case "Home":
                        loadFragment(new HomeFragment());
                        return true;
                    case "Filter":
                        loadFragment(new FilterFragment());
                        return true;
                    case "Settings":
                        loadFragment(new SettingsFragment());
                        return true;
                }
                return false;
            });

            return insets;
        });
    }
    private void findAllId(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
}