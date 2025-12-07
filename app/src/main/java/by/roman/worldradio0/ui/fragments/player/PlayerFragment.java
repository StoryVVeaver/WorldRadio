package by.roman.worldradio0.ui.fragments.player;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.PlayerViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.fragments.filter.FilterFragment;
import by.roman.worldradio0.ui.fragments.timer.TimerFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerFragment extends Fragment {
    private ImageButton timerButton;
    private ImageButton filterButton;
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
    private final int MIN_SWIPE_DISTANCE = 10;
    private final int MAX_TAP_MOVEMENT = 5;

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
        timerButton = view.findViewById(R.id.timerButtonView);
        filterButton = view.findViewById(R.id.filterButtonView);
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

                        if (Math.abs(diffX) <= MAX_TAP_MOVEMENT && Math.abs(diffY) <= MAX_TAP_MOVEMENT) {
                            motionLayout.transitionToEnd();
                            return true;
                        }

                        if (Math.abs(diffY) > Math.abs(diffX)) {
                            if (diffY < -MIN_SWIPE_DISTANCE) {
                                motionLayout.transitionToEnd();
                                return true;
                            } else if (diffY > MIN_SWIPE_DISTANCE) {
                                viewModel.stop();
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
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        return true;
                }
                return false;
            }
        });
    }

    private int darkenColor(int color, float factor) {
        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        hsl[2] = Math.max(0.1f, hsl[2] * (1f - factor));
        return ColorUtils.HSLToColor(hsl);
    }

    private int getCenterColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.background);
        int extractedColor = defaultColor;

        if (palette.getDominantSwatch() != null) {
            extractedColor = palette.getDominantColor(defaultColor);
        } else if (palette.getMutedSwatch() != null) {
            extractedColor = palette.getMutedColor(defaultColor);
        } else if (palette.getDarkMutedSwatch() != null) {
            extractedColor = palette.getDarkMutedColor(defaultColor);
        } else if (palette.getLightMutedSwatch() != null) {
            extractedColor = palette.getLightMutedColor(defaultColor);
        }

        if (ColorUtils.calculateLuminance(extractedColor) > 0.8) {
            int darkMuted = palette.getDarkMutedColor(defaultColor);
            if (darkMuted != defaultColor) {
                extractedColor = darkMuted;
            } else {
                extractedColor = darkenColor(extractedColor, 0.45f);
            }
        }

        float initialDarkenFactor = 0.8f;
        int finalColor = ColorUtils.blendARGB(extractedColor, getResources().getColor(R.color.background), 0.6f);
        return darkenColor(finalColor, initialDarkenFactor);
    }

    private void applyLargePlayerBackground(int centerColor) {
        float edgeDarkenFactor = 0.7f;
        int edgeColor = darkenColor(centerColor, edgeDarkenFactor);

        largePlayer.setBackgroundColor(darkenColor(edgeColor, 0.8f));
    }

    private void applyBottomPlayerBackground(int color) {
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.bottom_player);

        int finalColor = ColorUtils.blendARGB(color, defaultColor, 0.3f);

        bottomPlayer.setBackgroundColor(finalColor);
    }


    @SuppressLint("SetTextI18n")
    private void putData(@NonNull View view){
        station.setText(viewModel.getCurrentStation().getName());
        large_station.setText(viewModel.getCurrentStation().getName());

        Glide.with(view.getContext())
                .load(viewModel.getCurrentStation().getFavicon())
                .error(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon))
                .into(logo);

        Glide.with(view.getContext())
                .asBitmap()
                .load(viewModel.getCurrentStation().getFavicon())
                .error(AppCompatResources.getDrawable(requireContext(), R.drawable.no_icon))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        large_logo.setImageBitmap(resource);
                        int centerColor = getCenterColor(resource);

                        applyLargePlayerBackground(centerColor);
                        applyBottomPlayerBackground(centerColor);
                    }

                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                        largePlayer.setBackgroundResource(R.color.background);
                        bottomPlayer.setBackgroundResource(R.color.bottom_player);
                        large_logo.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon));
                    }
                });

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
                    .asBitmap()
                    .load(currentStation.getFavicon())
                    .error(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            large_logo.setImageBitmap(resource);
                            int centerColor = getCenterColor(resource);

                            applyLargePlayerBackground(centerColor);
                            applyBottomPlayerBackground(centerColor);
                        }
                        @Override
                        public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                            largePlayer.setBackgroundResource(R.color.background);
                            bottomPlayer.setBackgroundResource(R.color.bottom_player);
                            large_logo.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.no_icon));
                        }
                    });
        });
        if(!viewModel.getCurrentStation().getHomepage().isEmpty()){
            large_internet.setVisibility(VISIBLE);
        } else large_internet.setVisibility(INVISIBLE);
        fav_icons();
        favTrack_icons();
    }

    private void buttons(){
        icons();
        timerButton.setOnClickListener(v -> {
            stateViewModel.openFullscreen(new TimerFragment());
        });
        filterButton.setOnClickListener(v -> {
            stateViewModel.openFullscreen(new FilterFragment());
        });
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
            large_saveTrack.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.selected_saved_track));
        } else {
            large_saveTrack.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.unselected_saved_track));
        }
    }
    private void openUrlInBrowser(String homepage) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), getResources().getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
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