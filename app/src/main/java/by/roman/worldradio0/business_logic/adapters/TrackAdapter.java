package by.roman.worldradio0.business_logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    public interface OnTrackClickListener {
        void onDeleteClick(int position);
        void onBrowseClick(int position);
    }
    private final OnTrackClickListener listener;
    private List<FavoriteTrack> data;

    public TrackAdapter(List<FavoriteTrack> data, OnTrackClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(List<FavoriteTrack> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    public String getTrack(int position){
        return data.get(position).getTrack();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteTrack track = data.get(position);
        holder.bind(track);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView trackName;
        ImageView delButton;
        ImageView browseButton;
        ViewHolder(View v) {
            super(v);
            trackName = v.findViewById(R.id.trackView);
            delButton = v.findViewById(R.id.delButton_track);
            browseButton = v.findViewById(R.id.browseButtonTrack);
        }
        void bind(FavoriteTrack track){
            trackName.setText(track.getTrack());
            delButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(pos);
                }
            });
            browseButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onBrowseClick(pos);
                }
            });
        }
    }
}