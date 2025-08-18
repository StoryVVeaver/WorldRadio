package by.roman.worldradio0.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.ViewPagerAdapter;
import by.roman.worldradio0.business_logic.view_models.StatePlayerViewModel;
import by.roman.worldradio0.ui.fragments.player.PlayerFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private StatePlayerViewModel viewModel;

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
                startBottomPlayer();
            } else {
                removeBottomPlayer();
            }
        });
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);
        viewPager.setOffscreenPageLimit(4);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (Objects.requireNonNull(item.getTitle()).toString()) {
                case "Globe":
                    viewPager.setCurrentItem(0);
                    return true;
                case "Favorite":
                    viewPager.setCurrentItem(1);
                    return true;
                case "Home":
                    viewPager.setCurrentItem(2);
                    return true;
                case "Filter":
                    viewPager.setCurrentItem(3);
                    return true;
                case "Settings":
                    viewPager.setCurrentItem(4);
                    return true;
            }
            return false;
        });
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(2, false);
        }
    }
    private void findAllId(){
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    private void startBottomPlayer(){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_bottom,R.anim.slide_in_from_bottom)
                .replace(R.id.bottom_player_container,new PlayerFragment())
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
}