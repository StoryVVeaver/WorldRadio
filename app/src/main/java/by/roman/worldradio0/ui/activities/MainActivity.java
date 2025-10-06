package by.roman.worldradio0.ui.activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.ViewPagerAdapter;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.fragments.player.PlayerFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private StateViewModel viewModel;
    private boolean isMap = true;

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
        initAll();
        observeChanges();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (Objects.requireNonNull(item.getTitle()).toString()) {
                case "Favorite":
                    viewPager.setCurrentItem(0);
                    return true;
                case "Home":
                    viewPager.setCurrentItem(1);
                    return true;
                case "Settings":
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        });
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(1, false);
        }
    }
    private void findAllId(){
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(StateViewModel.class);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);
        viewPager.setOffscreenPageLimit(2);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    viewPager.setUserInputEnabled(!isMap);
                } else {
                    viewPager.setUserInputEnabled(true);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }
    private void observeChanges(){
        viewModel.shouldShowPanel().observe(this, show -> {
            if (Boolean.TRUE.equals(show)) {
                startBottomPlayer();
            } else {
                removeBottomPlayer();
            }
        });
        viewModel.isMapOpen().observe(this, state -> {
            boolean mapOpen = Boolean.TRUE.equals(state);
            isMap = mapOpen;

            int current = viewPager.getCurrentItem();
            if (current == 1) {
                viewPager.setUserInputEnabled(!mapOpen);
            }
        });
        viewModel.openRequest().observe(this, fragment -> {
            showFullscreenFragment(fragment, fragment.getClass().getSimpleName(), true);
        });
        viewModel.closeRequest().observe(this, flag -> {
            closeFullscreenFragment();
        });
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
    public void showFullscreenFragment(Fragment fragment, String tag, boolean addToBackStack) {
        viewPager.setUserInputEnabled(false);
        findViewById(R.id.fullscreen_container).setVisibility(VISIBLE);

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fullscreen_container, fragment, tag);
        if (addToBackStack) ft.addToBackStack(tag);
        ft.commit();
        bottomNavigationView.setVisibility(INVISIBLE);
    }
    public void closeFullscreenFragment() {
        final FragmentManager fm = getSupportFragmentManager();

        Runnable restoreUi = () -> {
            findViewById(R.id.fullscreen_container).setVisibility(GONE);
            bottomNavigationView.setVisibility(VISIBLE);
            if (viewPager.getCurrentItem() == 1) {
                viewPager.setUserInputEnabled(!isMap);
            } else {
                viewPager.setUserInputEnabled(true);
            }
        };

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (fm.getBackStackEntryCount() == 0) {
                        restoreUi.run();
                        fm.removeOnBackStackChangedListener(this);
                    }
                }
            });
        } else {
            Fragment fragment = fm.findFragmentById(R.id.fullscreen_container);
            if (fragment != null) {
                fm.beginTransaction()
                        .remove(fragment)
                        .commitNow();
            }
            restoreUi.run();
        }
    }
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (fm.getBackStackEntryCount() == 0) {
                        findViewById(R.id.fullscreen_container).setVisibility(GONE);
                        bottomNavigationView.setVisibility(VISIBLE);
                        if (viewPager.getCurrentItem() == 1) {
                            viewPager.setUserInputEnabled(!isMap);
                        } else {
                            viewPager.setUserInputEnabled(true);
                        }
                        fm.removeOnBackStackChangedListener(this);
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}