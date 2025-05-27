package by.roman.worldradio0.ui.fragments.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GlobeFragment extends Fragment {
    private PlayerViewModel viewModel;
    private WebView webView;
    private TextView text;
    private RadioStation model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_globe, container, false);
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("GlobeFragment: performance", "onViewCreated started");
        findAllId(view);
        initAll();
        observePlaying();


        Log.v("GlobeFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAllId(View view){
        webView = view.findViewById(R.id.mapWebView);
        text = view.findViewById(R.id.errorText_Globe);
        text.setVisibility(VISIBLE);
        webView.setVisibility(INVISIBLE);
        text.setText("Воспроизведение не запущено");
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
    }
    private void observePlaying(){
        viewModel.getIsPlaying().observe(getViewLifecycleOwner(),status -> {
            if(status){
                model = viewModel.getCurrentStation();
                if(model.getGeoLong() != 0 && model.getGeoLat() != 0){
                    text.setVisibility(INVISIBLE);
                    webView.setVisibility(VISIBLE);
                    webView.setWebViewClient(new WebViewClient());
                    String mapUrl = getMapPosition(model.getGeoLat(), model.getGeoLong());
                    webView.loadUrl(mapUrl);
                } else {
                    text.setVisibility(VISIBLE);
                    webView.setVisibility(INVISIBLE);
                    text.setText("Станция не имеет коoрдинаты");
                }
            } else {
                text.setVisibility(VISIBLE);
                webView.setVisibility(INVISIBLE);
                text.setText("Воспроизведение не запущено");
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @NonNull
    private String getMapPosition(double lat, double lon) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        double delta = 0.01;

        double latMin = lat - delta;
        double latMax = lat + delta;
        double lonMin = lon - delta;
        double lonMax = lon + delta;

        String mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox="
                + lonMin + "%2C" + latMin + "%2C" + lonMax + "%2C" + latMax
                + "&layer=mapnik";
        return mapUrl;
    }
}