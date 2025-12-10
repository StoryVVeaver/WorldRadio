package by.story_weaver.worldradiomonitoring.logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.worldradiomonitoring.R;
import by.story_weaver.worldradiomonitoring.logic.models.Station;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.RadioViewHolder> {

    private final List<Station> stations = new ArrayList<>();

    public void setStations(List<Station> newStations){
        stations.clear();
        stations.addAll(newStations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RadioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_card, parent, false);
        return new RadioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RadioViewHolder holder, int position) {
        Station station = stations.get(position);
        holder.name.setText(station.getName());
        holder.country.setText(station.getCountry());
        holder.clickCount.setText(String.valueOf(station.getClickCount()));

        Glide.with(holder.itemView.getContext())
                .load(station.getFavicon())
                .placeholder(R.drawable.unselected_flag)
                .into(holder.logo);

        String flagUrl = "https://flagcdn.com/48x36/" + station.getCountryCode().toLowerCase() + ".png";
        Glide.with(holder.itemView.getContext())
                .load(flagUrl)
                .placeholder(R.drawable.unselected_flag)
                .into(holder.flag);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    static class RadioViewHolder extends RecyclerView.ViewHolder {
        ImageView logo, flag;
        TextView name, country, clickCount;

        public RadioViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.logoStationView);
            flag = itemView.findViewById(R.id.flagStation);
            name = itemView.findViewById(R.id.nameStationView);
            country = itemView.findViewById(R.id.countryName);
            clickCount = itemView.findViewById(R.id.clickCountView);
        }
    }
}
