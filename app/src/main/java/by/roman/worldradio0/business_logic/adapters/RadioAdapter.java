package by.roman.worldradio0.business_logic.adapters;

import static android.app.AppOpsManager.MODE_DEFAULT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public class RadioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_STATION = 0;
    private static final int TYPE_LOADING = 1;
    private int mode = 0;
    private final Context context;
    private List<RadioStation> stations = new ArrayList<>();
    private final OnItemClickListener listener;
    private boolean isLoading = false;

    public interface OnItemClickListener {
        void onItemClick(int position);
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
            StationViewHolder stationHolder = (StationViewHolder) holder;
            stationHolder.bind(station);

            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return stations.size() + (isLoading ? 1 : 0);
    }
    public String getUUID(int position){
        if(stations != null){
            return stations.get(position).getStationUuid();
        }
        Log.e("RadioAdapter","null");
        return "db93a00f-9191-46ab-9e87-ec9b373b3eee";
    }

    @Override
    public int getItemViewType(int position) {
        return (position == stations.size() && isLoading) ? TYPE_LOADING : TYPE_STATION;
    }

    public void addStations(List<RadioStation> newStations) {
        int start = stations.size();
        stations.addAll(newStations);
        notifyItemRangeInserted(start, newStations.size());
    }
    @SuppressLint("NotifyDataSetChanged")
    public void replaceAll(List<RadioStation> newStations) {
        stations.clear();
        stations.addAll(newStations);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        stations.clear();
        notifyDataSetChanged();
    }
    public void showLoading() {
        isLoading = true;
        notifyItemInserted(stations.size());
    }

    public void hideLoading() {
        isLoading = false;
        notifyItemRemoved(stations.size());
    }

    class StationViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameStation, country, quality;
        private final ImageView logoStation, flag, button;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameStation = itemView.findViewById(R.id.nameStationView_Home);
            logoStation = itemView.findViewById(R.id.logoStationView_Home);
            flag = itemView.findViewById(R.id.flagStation_CardHome);
            country = itemView.findViewById(R.id.countryName_CardHome);
            button = itemView.findViewById(R.id.delButton_card);
            quality = itemView.findViewById(R.id.quality_collapsed_player);
        }

        @SuppressLint("SetTextI18n")
        void bind(@NonNull RadioStation station) {
            nameStation.setText(station.getName());
            nameStation.setSelected(true);
            country.setText(station.getCountry());
            int bitrate = station.getBitrate();
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
                button.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(pos);
                    }
                    quality.setVisibility(GONE);
                });
            } else {
                quality.setVisibility(VISIBLE);
                button.setVisibility(GONE);
            }

            Glide.with(itemView.getContext())
                    .load("https://flagsapi.com/" + station.getCountryCode() + "/flat/64.png")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(flag);

            Glide.with(itemView.getContext())
                    .load(station.getFavicon())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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