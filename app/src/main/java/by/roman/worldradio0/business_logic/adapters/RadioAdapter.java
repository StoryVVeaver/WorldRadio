package by.roman.worldradio0.business_logic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.ViewHolder> {
    private Context context;
    private final List<RadioStation> stationList = new ArrayList<>();
    private OnItemClickListener listener;
    @SuppressLint("NotifyDataSetChanged")
    public void setStations(List<RadioStation> stations) {
        stationList.clear();
        stationList.addAll(stations);
        notifyDataSetChanged(); // можно заменить на DiffUtil позже
    }
    public RadioAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.radio_card,parent,false);
        return new ViewHolder(view);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RadioStation card = stationList.get(position);
        holder.nameStation.setText(card.getName());
        holder.nameStation.setSelected(true);
        Glide.with(context)
                .load("https://flagsapi.com/"+ card.getCountryCode() +"/flat/64.png")
                .into(holder.flag);
        holder.country.setText(card.getCountry());
        Glide.with(context)
                .load(card.getFavicon())
                .into(holder.logoStation);
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            RadioStation selectedStation = stationList.get(adapterPosition);
            notifyDataSetChanged();
            if(listener != null){
                listener.onItemClick(position);
            }
        });
    }


    public int getItemCount() {
        return stationList.size();
    }
    public void loadMoreData(List<RadioStation> newStations) {
        int startPosition = stationList.size();
        stationList.addAll(newStations);
        notifyItemRangeInserted(startPosition, newStations.size());
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameStation,country;
        ImageView logoStation,flag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameStation = itemView.findViewById(R.id.nameStationView_Home);
            logoStation = itemView.findViewById(R.id.logoStationView_Home);
            flag = itemView.findViewById(R.id.flagStation_CardHome);
            country = itemView.findViewById(R.id.countryName_CardHome);
        }
    }
}
