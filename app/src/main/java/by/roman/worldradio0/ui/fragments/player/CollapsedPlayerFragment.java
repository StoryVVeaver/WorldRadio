package by.roman.worldradio0.ui.fragments.player;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CollapsedPlayerFragment extends Fragment {
    private ImageView stop_btn;
    private ImageView play_pause;
    private ImageView logo;
    private TextView station;
    private TextView track;
    private CardView bottomPlayer;
    private PlayerViewModel viewModel;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collapsed_player, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        findAll(view);
        initAll();
        putData(view);
        buttons();
        Log.v("CollapsedPlayerFragment","Performance - onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAll(@NonNull View view){
        stop_btn = view.findViewById(R.id.bottom_player_stop);
        play_pause = view.findViewById(R.id.bottom_player_play_pause);
        logo = view.findViewById(R.id.bottom_player_logo);
        station = view.findViewById(R.id.bottom_player_station);
        station.setSelected(true);
        track = view.findViewById(R.id.bottom_player_track);
        track.setSelected(true);
        bottomPlayer = view.findViewById(R.id.bottomPlayer);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
    }
    @SuppressLint("SetTextI18n")
    private void putData(View view){
        station.setSelected(true);
        station.setText(viewModel.getCurrentStation().getName());
        track.setSelected(true);
        viewModel.getCurrentTrack().observe(getViewLifecycleOwner(), currentTrack -> {
            track.setText(currentTrack);
        });
        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying ->{
            flag = isPlaying;
            icons();
        });
        Glide.with(view.getContext())
                .load(viewModel.getCurrentStation().getUrl())
                .into(logo);
    }
    private void buttons(){
        //TODO fix icons
        icons();
        play_pause.setOnClickListener(v -> {
            if(flag){
                viewModel.pause();
            } else {
                viewModel.play();
            }
            icons();
        });
        bottomPlayer.setOnClickListener(v -> {
            //TODO expanded
        });
        stop_btn.setOnClickListener(v -> viewModel.stop());
    }
    private void icons(){
        if (!flag) {
            play_pause.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.play));
        } else {
            play_pause.setImageDrawable(AppCompatResources
                    .getDrawable(requireContext(), R.drawable.pause));
        }
    }
}