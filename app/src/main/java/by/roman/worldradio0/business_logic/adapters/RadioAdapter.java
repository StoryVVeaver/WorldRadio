package by.roman.worldradio0.business_logic.adapters;

import static android.app.AppOpsManager.MODE_DEFAULT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public class    RadioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_STATION = 0;
    private static final int TYPE_LOADING = 1;
    private int mode = 0;
    private final Context context;
    private List<RadioStation> stations = new ArrayList<>();
    private final OnItemClickListener listener;
    private boolean isLoading = false;

    public interface OnItemClickListener {
        void onStationItemClick(int position);
        void onDeleteClick(int position);
    }

    public RadioAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.radio_card, parent, false);
            return new StationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StationViewHolder) {
            RadioStation station = stations.get(position);
            boolean isLast = false;
            if(position + 1 == stations.size()){
                isLast = true;
            }
            StationViewHolder stationHolder = (StationViewHolder) holder;
            stationHolder.bind(station, isLast);

            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < stations.size()) {
                    listener.onStationItemClick(pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return stations.size() + (isLoading ? 1 : 0);
    }
    public String getUUID(int position){
        if (position >= 0 && position < stations.size()) {
            return stations.get(position).getStationUuid();
        }
        Log.e("RadioAdapter","getUUID: invalid position " + position + " size=" + stations.size());
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        return (position == stations.size() && isLoading) ? TYPE_LOADING : TYPE_STATION;
    }

    public int findCurrentStation(String uuid){
        int result = -1;
        for(int i = 0; i < stations.size(); i++){
            if(stations.get(i).getStationUuid().equals(uuid)){
                result = i;
            }
        }
        return result;
    }

    public void addStations(List<RadioStation> newStations) {
        if (newStations == null || newStations.isEmpty()) return;
        int start;
        synchronized (this) {
            start = stations.size();
            stations.addAll(new ArrayList<>(newStations));
        }
        final int s = start;
        final int cnt = newStations.size();
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> notifyItemRangeInserted(s, cnt));
    }

    public void replaceAll(List<RadioStation> newStations) {
        if (newStations == null) newStations = new ArrayList<>();
        this.stations = new ArrayList<>(newStations);
        new android.os.Handler(android.os.Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        stations.clear();
        notifyDataSetChanged();
    }
    public void showLoading() {
        if (isLoading) return;
        isLoading = true;
        new android.os.Handler(android.os.Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    public void hideLoading() {
        if (!isLoading) return;
        isLoading = false;
        new android.os.Handler(android.os.Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }


    class StationViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameStation, country, quality;
        private final ImageView logoStation, flag, button;
        private final FrameLayout card;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameStation = itemView.findViewById(R.id.nameStationView_Home);
            logoStation = itemView.findViewById(R.id.logoStationView_Home);
            flag = itemView.findViewById(R.id.flagStation_CardHome);
            country = itemView.findViewById(R.id.countryName_CardHome);
            button = itemView.findViewById(R.id.delButton_card);
            quality = itemView.findViewById(R.id.quality_collapsed_player);
            card = itemView.findViewById(R.id.radio_card);
        }

        @SuppressLint("SetTextI18n")
        void bind(@NonNull RadioStation station, boolean isLast) {
            nameStation.setText(station.getName());
            nameStation.setSelected(true);
            country.setText(station.getCountry());
            int bitrate = station.getBitrate();
            card.setPadding(0, 0, 0, 0);

            if(isLast){
                int bottomPaddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        250,
                        itemView.getResources().getDisplayMetrics()
                );
                card.setPadding(0, 0, 0, bottomPaddingPx);
            }
            if(bitrate > 400){
                if(bitrate > 5000){
                    bitrate = bitrate / 100;
                    if(bitrate < 129){
                        quality.setText("LQ");
                    } else if (bitrate < 320 ) {
                    } else quality.setText("HQ");
                } else quality.setText("LQ");
            } else {
                if(bitrate < 129){
                    quality.setText("LQ");
                } else if (bitrate > 319 ) {
                    quality.setText("HQ");
                }
            }
            if (mode == 1) {
                button.setVisibility(VISIBLE);
                quality.setVisibility(GONE);
                button.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(pos);
                    }
                });
            } else {
                quality.setVisibility(VISIBLE);
                button.setVisibility(GONE);
            }

            Glide.with(itemView.getContext())
                    .load("https://flagsapi.com/" + station.getCountryCode() + "/flat/64.png")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(AppCompatResources.getDrawable(context,R.drawable.no_icon))
                    .into(flag);

            Glide.with(itemView.getContext())
                    .load(station.getFavicon())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(AppCompatResources.getDrawable(context,R.drawable.no_icon))
                    .into(logoStation);
        }
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        View loadingContainer;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            loadingContainer = itemView.findViewById(R.id.loading_container);

            loadingContainer.setAlpha(0f);
            loadingContainer.animate().alpha(1f).setDuration(300).start();
        }
    }
}