package by.story_weaver.worldradiomonitoring.ui.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import by.story_weaver.worldradiomonitoring.R;
import by.story_weaver.worldradiomonitoring.logic.adapters.ViewPagerAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        findById();
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewPager.setUserInputEnabled(true);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_stations) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.nav_dashboard) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.nav_countries) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(1, false);
        }
    }
    private void findById(){
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
}