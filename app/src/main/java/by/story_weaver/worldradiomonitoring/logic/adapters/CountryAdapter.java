package by.story_weaver.worldradiomonitoring.logic.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import by.story_weaver.worldradiomonitoring.R;
import by.story_weaver.worldradiomonitoring.logic.LocationUtil;
import by.story_weaver.worldradiomonitoring.logic.models.CodesModel;
import by.story_weaver.worldradiomonitoring.logic.models.FilterStation;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private List<CodesModel> countries;
    private final Set<FilterStation> selectedIso = new HashSet<>();

    public CountryAdapter(List<CodesModel> countries) {
        this.countries = countries;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<CodesModel> newList) {
        this.countries = newList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedCountries(List<FilterStation> isoList) {
        selectedIso.clear();
        selectedIso.addAll(isoList);
        notifyDataSetChanged();
    }

    public List<FilterStation> getSelectedCountriesIso() {
        return new ArrayList<>(selectedIso);
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.ViewHolder holder, int position) {
        CodesModel model = countries.get(position);
        String iso = model.getCountryCode();
        String name = LocationUtil.getCountryNameFromIso(iso);

        holder.name.setText(name);

        String url = "https://flagsapi.com/" + iso + "/flat/64.png";

        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.unselected_flag)
                .error(R.drawable.unselected_flag)
                .into(holder.flag);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedIso.contains(new FilterStation(iso)));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedIso.add(new FilterStation(iso));
            else selectedIso.remove(new FilterStation(iso));
        });
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView flag;
        TextView name;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flag = itemView.findViewById(R.id.country_flag);
            name = itemView.findViewById(R.id.country_name);
            checkBox = itemView.findViewById(R.id.country_checkbox);
        }
    }
}
