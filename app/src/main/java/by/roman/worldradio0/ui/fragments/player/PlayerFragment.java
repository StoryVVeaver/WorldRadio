package by.roman.worldradio0.ui.fragments.player;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerFragment extends Fragment {
    private MotionLayout motionLayout;
    private ImageView save_btn;
    private ImageView large_save_btn;
    private ImageView play_pause;
    private ImageView large_saveTrack;
    private ImageView large_play_pause;
    private ImageView logo;
    private ImageView large_logo;
    private ImageView close;
    private ImageView large_internet;
    private TextView station;
    private TextView large_station;
    private TextView track;
    private TextView large_track;
    private CardView bottomPlayer;
    private ConstraintLayout largePlayer;
    private PlayerViewModel viewModel;
    private StateViewModel stateViewModel;
    private boolean isPlaying;
    private boolean isFavorite;
    private boolean isFavoriteTrack;
    private boolean isMap = true;

    @Override
    public void onResume(){
        super.onResume();
        large_internet.setEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
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
        large_saveTrack = view.findViewById(R.id.large_save_unsave_Track);
        large_internet = view.findViewById(R.id.large_internet);
        close = view.findViewById(R.id.large_back);
        motionLayout = view.findViewById(R.id.motionLayout);
        save_btn = view.findViewById(R.id.bottom_player_save);
        large_save_btn = view.findViewById(R.id.large_save_unsave);
        play_pause = view.findViewById(R.id.bottom_player_play_pause);
        large_play_pause = view.findViewById(R.id.large_play_pause);
        large_logo = view.findViewById(R.id.large_station_logo);
        logo = view.findViewById(R.id.bottom_player_logo);
        station = view.findViewById(R.id.bottom_player_station);
        large_station = view.findViewById(R.id.large_station_name);
        large_station.setSelected(true);
        station.setSelected(true);
        track = view.findViewById(R.id.bottom_player_track);
        large_track = view.findViewById(R.id.large_track_name);
        track.setSelected(true);
        large_track.setSelected(true);
        bottomPlayer = view.findViewById(R.id.bottomPlayer);
        largePlayer = view.findViewById(R.id.large_player);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initAll(){
        viewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);

        final int MIN_SWIPE_DISTANCE = 60;
        final int MAX_TAP_MOVEMENT = 12;

        largePlayer.setOnTouchListener(new View.OnTouchListener() {
            private float startY;
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        startX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float endY = event.getY();
                        float endX = event.getX();
                        float diffY = endY - startY;
                        float diffX = endX - startX;

                        if (Math.abs(diffY) > Math.abs(diffX)) {
                            if (diffY > MIN_SWIPE_DISTANCE) {
                                motionLayout.transitionToStart();
                                return true;
                            }
                            return true;
                        } else {
                            if (Math.abs(diffX) > MIN_SWIPE_DISTANCE) {
                                if (diffX < 0) {
                                    onSwipeLeft();
                                } else {
                                    onSwipeRight();
                                }
                                return true;
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        bottomPlayer.setOnTouchListener(new View.OnTouchListener() {
            private float startY;
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        startX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float endY = event.getY();
                        float endX = event.getX();
                        float diffY = endY - startY;
                        float diffX = endX - startX;

                        if (Math.abs(diffY) > Math.abs(diffX)) {
                            if (diffY < MIN_SWIPE_DISTANCE) {
                                motionLayout.transitionToEnd();
                                return true;
                            } else if (diffY > MIN_SWIPE_DISTANCE) {
                                viewModel.stop();
                                return true;
                            } else {
                                motionLayout.transitionToEnd();
                                return true;
                            }
                        } else {
                            if (Math.abs(diffX) > MIN_SWIPE_DISTANCE) {
                                if (diffX < 0) {
                                    onSwipeLeft();
                                } else {
                                    onSwipeRight();
                                }
                                return true;
                            } else {
                                return true;
                            }
                        }
                    case MotionEvent.ACTION_MOVE:
                        return true;
                }
                return false;
            }
        });
    }
//TODO перекрасить фон в соответствии с аватаркой
    @SuppressLint("SetTextI18n")
    private void putData(@NonNull View view){
        station.setText(viewModel.getCurrentStation().getName());
        large_station.setText(viewModel.getCurrentStation().getName());
        Glide.with(view.getContext())
                .load(viewModel.getCurrentStation().getFavicon())
                .error(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon))
                .into(logo);
        Glide.with(view.getContext())
                .load(viewModel.getCurrentStation().getFavicon())
                .error(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon))
                .into(large_logo);
        viewModel.getCurrentTrack().observe(getViewLifecycleOwner(), currentTrack -> {
            track.setText(currentTrack);
            large_track.setText(currentTrack);
        });
        viewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            this.isPlaying = isPlaying;
            icons();
        });
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            this.isFavorite = isFavorite;
            fav_icons();
        });
        viewModel.getIsFavoriteTrack().observe(getViewLifecycleOwner(), isFavoriteTrack -> {
            this.isFavoriteTrack = isFavoriteTrack;
            favTrack_icons();
        });
        stateViewModel.isMapOpen().observe(getViewLifecycleOwner(), flag -> {
            this.isMap = flag;
        });
        viewModel.getIsPlayingChanged().observe(getViewLifecycleOwner(), currentStation -> {
            isFavorite = viewModel.isFavorite();
            isFavoriteTrack = viewModel.isFavoriteTrack();
            fav_icons();
            favTrack_icons();
            station.setText(currentStation.getName());
            large_station.setText(currentStation.getName());
            Glide.with(view.getContext())
                    .load(currentStation.getFavicon())
                    .error(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon))
                    .into(logo);
            Glide.with(view.getContext())
                    .load(currentStation.getFavicon())
                    .error(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon))
                    .into(large_logo);
        });
        if(!viewModel.getCurrentStation().getHomepage().isEmpty()){
            large_internet.setVisibility(VISIBLE);
        } else large_internet.setVisibility(INVISIBLE);
        fav_icons();
        favTrack_icons();
    }

    private void buttons(){
        icons();

        large_internet.setOnClickListener(v -> {
            openUrlInBrowser(viewModel.getCurrentStation().getHomepage());
            large_internet.setEnabled(false);
        });
        play_pause.setOnClickListener(v -> {
            if(isPlaying){
                viewModel.pause();
            } else {
                viewModel.play();
            }
            icons();
        });
        large_play_pause.setOnClickListener(v -> {
            if(isPlaying){
                viewModel.pause();
            } else {
                viewModel.play();
            }
            icons();
        });
        close.setOnClickListener(v -> {
            motionLayout.transitionToStart();
        });

        save_btn.setOnClickListener(v -> {
            if (isFavorite){
                viewModel.removeFromFavorite();
            } else {
                viewModel.addToFavorite();
            }
            fav_icons();
        });
        large_save_btn.setOnClickListener(v -> {
            if (isFavorite){
                viewModel.removeFromFavorite();
            } else {
                viewModel.addToFavorite();
            }
            fav_icons();
        });
        large_saveTrack.setOnClickListener(v -> {
            if(isFavoriteTrack){
                viewModel.removeTrackFromFavorite();
            } else {
                viewModel.addTrackToFavorite();
            }
            favTrack_icons();
        });
    }
    private void icons(){
        if (!isPlaying) {
            play_pause.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.play));
            large_play_pause.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.playbutton));
        } else {
            play_pause.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.pause));
            large_play_pause.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.pausebutton));
        }
    }
    private void fav_icons(){
        if (isFavorite){
            save_btn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.fi_ss_star__2_));
            large_save_btn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.fi_ss_star__2_));
        } else {
            save_btn.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.unsaved));
            large_save_btn.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.unsaved));
        }
    }
    private void favTrack_icons(){
        if (isFavoriteTrack){
            large_saveTrack.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.fi_ss_star__2_));
        } else {
            large_saveTrack.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.unsaved));
        }
    }
    private void openUrlInBrowser(String homepage) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Браузер не обнаружен", Toast.LENGTH_SHORT).show();
            Log.e("BottomPlayer", "Error opening URL: " + e.getMessage());
        }
    }
    private void onSwipeLeft() {
        Log.v("BottomPlayer", "Left, isMap: " + isMap);
        if(isMap){
            viewModel.requestSnapNearest();
        } else {
            viewModel.playNext();
        }
    }
    private void onSwipeRight() {
        Log.v("BottomPlayer", "Right");
        if(isMap){
            viewModel.requestSnapPrevious();
        } else {
            viewModel.playPrevious();
        }
    }
}
