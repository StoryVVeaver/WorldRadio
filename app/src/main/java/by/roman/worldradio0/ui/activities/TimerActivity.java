package by.roman.worldradio0.ui.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import by.roman.worldradio0.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        long startTime = System.nanoTime();
        Log.v("TimerActivity: performance", "onCreated started");
        Log.v("TimerActivity: performance", "onCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
}