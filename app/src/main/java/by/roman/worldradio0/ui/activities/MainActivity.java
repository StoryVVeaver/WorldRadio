package by.roman.worldradio0.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.StatePlayerViewModel;
import by.roman.worldradio0.ui.fragments.main.FavoriteFragment;
import by.roman.worldradio0.ui.fragments.main.FilterFragment;
import by.roman.worldradio0.ui.fragments.main.GlobeFragment;
import by.roman.worldradio0.ui.fragments.main.HomeFragment;
import by.roman.worldradio0.ui.fragments.main.SettingsFragment;
import by.roman.worldradio0.ui.fragments.player.CollapsedPlayerFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private StatePlayerViewModel viewModel;
    private int currentSelectedItemId = -1;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findAllId();
        viewModel = new ViewModelProvider(this).get(StatePlayerViewModel.class);
        viewModel.shouldShowPanel().observe(this, show -> {
            if (Boolean.TRUE.equals(show)) {
                showPlayer();
            } else {
                hidePlayer();
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentSelectedItemId) {
                return false;
            }
            currentSelectedItemId = itemId;
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
                    Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent);
                    //loadFragment(new SettingsFragment());
                    return true;
            }
            return false;
        });
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            loadFragment(new HomeFragment());
        }
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
    private void startBottomPlayer(){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_bottom,R.anim.slide_in_from_bottom)
                .replace(R.id.bottom_player_container,new CollapsedPlayerFragment())
                .commit();
    }

    private void removeBottomPlayer() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_player_container);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom)
                    .remove(fragment)
                    .commit();
        }
    }
    private void repairBottomPlayer(){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_top,R.anim.slide_in_from_top)
                .replace(R.id.bottom_player_container,new CollapsedPlayerFragment())
                .commit();
    }
    private void hideBottomPlayer(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_player_container);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_out_to_top, R.anim.slide_out_to_top)
                    .remove(fragment)
                    .commit();
        }
    }
    private void startPlayer(){
        hideBottomPlayer();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_bottom,R.anim.slide_in_from_bottom)
                .replace(R.id.bottom_player_container,new CollapsedPlayerFragment())
                .commit();
    }
    private void removePlayer(){
        repairBottomPlayer();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_player_container);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom)
                    .remove(fragment)
                    .commit();
        }
    }
    public void showPlayer() {
        if(viewModel.isExpanded()){
            startPlayer();
        } else {
            startBottomPlayer();
        }
    }
    public void hidePlayer(){
        if(viewModel.isExpanded()){
            removePlayer();
        } else {
            removeBottomPlayer();
        }
    }
}