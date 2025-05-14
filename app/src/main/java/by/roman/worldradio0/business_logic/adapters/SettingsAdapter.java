package by.roman.worldradio0.business_logic.adapters;

import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_GROUP;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.SettingsItem;
import by.roman.worldradio0.business_logic.settings.ListItem;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ListItem> items;

    public SettingsAdapter(List<SettingsGroup> groups) {
        items = new ArrayList<>();
        for (SettingsGroup group : groups) {
            items.add(group);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_GROUP) {
            View view = inflater.inflate(R.layout.item_group, parent, false);
            return new GroupViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_child, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        Log.d("SettingsAdapter", "onBindViewHolder: position = " + position + ", type = " + item.getType());
        if (holder instanceof GroupViewHolder) {
            ((GroupViewHolder) holder).bind((SettingsGroup) item);
        } else if (holder instanceof ChildViewHolder) {
            ((ChildViewHolder) holder).bind((SettingsItem) item);
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView arrow;

        GroupViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.group_title);
            arrow = itemView.findViewById(R.id.group_arrow);
        }

        void bind(SettingsGroup group) {
            title.setText(group.getTitle());
            arrow.setRotation(group.isExpanded() ? 180 : 0);
            itemView.setOnClickListener(v -> {
                int currentPosition = getBindingAdapterPosition();
                Log.d("SettingsAdapter",currentPosition + " " + items.size());
                int pos = collapseAll(currentPosition);
                if(pos != -1){
                    notifyItemChanged(pos);
                }
                currentPosition = getBindingAdapterPosition();
                if (group.isExpanded()) {
                    collapseGroup(currentPosition, group);
                } else {
                    expandGroup(currentPosition, group);
                }
            });
        }

        private int collapseAll(int position) {
            for(int i = 0;i<items.size()-1;i++){
                if(i == position) continue;
                ListItem item = items.get(i);
                if(item.getType() == TYPE_GROUP){
                    SettingsGroup group = (SettingsGroup) item;
                    if(group.isExpanded()){
                        collapseGroup(i,group);
                        return i;
                    }
                }
            }
            return -1;
        }
        void expandGroup(int position, SettingsGroup group) {
            Log.d("SA",position + " expand");
            group.setExpanded(true);
            arrow.setRotation(180);
            int insertPosition = position + 1;
            items.addAll(insertPosition, group.getChildren());
            notifyItemRangeInserted(insertPosition, group.getChildren().size());
        }

        void collapseGroup(int position, SettingsGroup group) {
            Log.d("SA",position + " collapse");
            group.setExpanded(false);
            arrow.setRotation(0);
            int removePosition = position + 1;
            int count = group.getChildren().size();
            for (int i = 0; i < count; i++) items.remove(removePosition);
            notifyItemRangeRemoved(removePosition, count);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ChildViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.child_title);
        }

        void bind(SettingsItem item) {
            title.setText(item.getTitle());
        }
    }
}

