package by.roman.worldradio0.business_logic.adapters;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_CHECK_WITH_SLIDER_CHILD;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_GROUP;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_SLIDER_CHILD;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_CHECK_CHILD;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_SWITCH_CHILD;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_TEXT_BUTTON_CHILD;
import static by.roman.worldradio0.business_logic.settings.ListItem.TYPE_TEXT_CHILD;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckWIthSliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SwitchItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextButtonItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextItem;
import by.roman.worldradio0.business_logic.settings.ListItem;
import by.roman.worldradio0.business_logic.settings.SettingsChangeListener;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ListItem> items;
    private final SettingsChangeListener listener;

    public SettingsAdapter(@NonNull List<SettingsGroup> groups, @NonNull SettingsChangeListener listener) {
        items = new ArrayList<>();
        items.addAll(groups);
        this.listener = listener;
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

        switch (viewType) {
            case TYPE_GROUP:
                View groupView = inflater.inflate(R.layout.item_group, parent, false);
                return new GroupViewHolder(groupView);

            case TYPE_TEXT_CHILD:
                View textView = inflater.inflate(R.layout.text_child, parent, false);
                return new TextViewHolder(textView);

            case TYPE_SLIDER_CHILD:
                View sliderView = inflater.inflate(R.layout.slider_child, parent, false);
                return new SliderViewHolder(sliderView);

            case TYPE_CHECK_CHILD:
                View checkView = inflater.inflate(R.layout.check_child, parent, false);
                return new CheckViewHolder(checkView);

            case TYPE_CHECK_WITH_SLIDER_CHILD:
                View checkWithSliderView = inflater.inflate(R.layout.check_with_slider_child, parent, false);
                return new CheckWithSliderViewHolder(checkWithSliderView);

            case TYPE_TEXT_BUTTON_CHILD:
                View textButtonView = inflater.inflate(R.layout.text_button_child, parent, false);
                return new TextButtonViewHolder(textButtonView);

            case TYPE_SWITCH_CHILD:
                View switchView = inflater.inflate(R.layout.switch_child, parent, false);
                return new SwitchViewHolder(switchView);

            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        Log.d("SettingsAdapter", "onBindViewHolder: position = " + position + ", type = " + item.getType());
        if (holder instanceof GroupViewHolder) {
            ((GroupViewHolder) holder).bind((SettingsGroup) item);
        } else if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind((TextItem) item);
        } else if (holder instanceof SliderViewHolder) {
            ((SliderViewHolder) holder).bind((SliderItem) item);
        } else if (holder instanceof CheckViewHolder) {
            ((CheckViewHolder) holder).bind((CheckItem) item);
        } else if (holder instanceof CheckWithSliderViewHolder) {
            ((CheckWithSliderViewHolder) holder).bind((CheckWIthSliderItem) item);
        } else if (holder instanceof TextButtonViewHolder) {
            ((TextButtonViewHolder) holder).bind((TextButtonItem) item);
        } else if (holder instanceof SwitchViewHolder) {
            ((SwitchViewHolder) holder).bind((SwitchItem) item);
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

        void bind(@NonNull SettingsGroup group) {
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
        void expandGroup(int position, @NonNull SettingsGroup group) {
            Log.d("SA",position + " expand");
            group.setExpanded(true);
            arrow.setRotation(180);
            int insertPosition = position + 1;
            items.addAll(insertPosition, group.getChildren());
            notifyItemRangeInserted(insertPosition, group.getChildren().size());
        }

        void collapseGroup(int position, @NonNull SettingsGroup group) {
            Log.d("SA",position + " collapse");
            group.setExpanded(false);
            arrow.setRotation(0);
            int removePosition = position + 1;
            int count = group.getChildren().size();
            for (int i = 0; i < count; i++) items.remove(removePosition);
            notifyItemRangeRemoved(removePosition, count);
        }
    }
    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        TextViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.child_title);
        }

        void bind(@NonNull SettingsItem item) {
            text.setText(item.getTitle());
        }
    }
    class SliderViewHolder extends RecyclerView.ViewHolder {
        SeekBar seekBar;
        TextView textView;
        TextView left;
        TextView right;
        TextView value;
        SliderViewHolder(View itemView) {
            super(itemView);
            seekBar = itemView.findViewById(R.id.seekBar_child);
            textView = itemView.findViewById(R.id.text_slider);
            right = itemView.findViewById(R.id.right_slider);
            left = itemView.findViewById(R.id.left_slider);
            value = itemView.findViewById(R.id.value_slider);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        void bind(@NonNull SliderItem item) {
            textView.setText(item.getTitle());
            seekBar.setMin(item.getMin());
            seekBar.setMax(item.getMax());
            seekBar.setProgress(item.getValue());
            right.setText(item.getRightLabel());
            left.setText(item.getLeftLabel());
            if(item.getIsDouble()){
                if(item.getIsPercent()){
                    value.setText(String.format("%.1f", seekBar.getProgress() * 0.1) + "%");
                } else {
                    value.setText(String.format("%.1f", seekBar.getProgress() * 0.1));
                }
            } else {
                if(item.getIsPercent()){
                    value.setText(seekBar.getProgress() + "%");
                } else {
                    value.setText("" + seekBar.getProgress());
                }
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(item.getIsDouble()){
                        if(item.getIsPercent()){
                            value.setText(String.format("%.1f", progress * 0.1) + "%");
                        } else {
                            value.setText(String.format("%.1f", progress * 0.1));
                        }
                    } else {
                        if(item.getIsPercent()){
                            value.setText(progress + "%");
                        } else {
                            value.setText(progress + "");
                        }
                    }
                    listener.onSliderChanged(item.getKey(),progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Дополнительные действия при начале перетаскивания
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Дополнительные действия после отпускания
                }
            });
        }
    }
    class CheckViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        CheckBox check;
        TextView textView;

        CheckViewHolder(View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.checkBox_check);
            textView = itemView.findViewById(R.id.text_check);
        }

        void bind(@NonNull CheckItem item) {
            check.setChecked(item.isEnabled());
            textView.setText(item.getTitle());
            check.setOnCheckedChangeListener((btn, isChecked) -> {
                item.setEnabled(isChecked);
                listener.onToggleChanged(item.getKey(), isChecked);
            });
        }
    }
    class CheckWithSliderViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        CheckBox check;
        SeekBar seekBar;
        TextView textView;
        TextView value;

        CheckWithSliderViewHolder(View itemView) {
            super(itemView);

            check = itemView.findViewById(R.id.checkBox_check_with_slider);
            textView = itemView.findViewById(R.id.text_check_with_slider);
            value = itemView.findViewById(R.id.value_check_with_slider);
            seekBar = itemView.findViewById(R.id.seekBar_check_with_slider);
        }

        @SuppressLint("SetTextI18n")
        void bind(@NonNull CheckWIthSliderItem item) {
            check.setChecked(item.isChecked());
            seekBar.setMin(item.getMin());
            seekBar.setMax(item.getMax());
            seekBar.setProgress(item.getValue());
            textView.setText(item.getTitle());
            value.setText(item.getValue() * 250 + "");
            check.setOnCheckedChangeListener((btn, isChecked) -> {
                item.setChecked(isChecked);
                int vis = isChecked ? VISIBLE : INVISIBLE;
                seekBar.setVisibility(vis);
                value.setVisibility(vis);
                listener.onToggleChanged(item.getCheckKey(), isChecked);
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    value.setText(progress * 250 + "");
                    listener.onSliderChanged(item.getSliderKey(),progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Дополнительные действия при начале перетаскивания
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Дополнительные действия после отпускания
                }
            });
        }
    }
    class TextButtonViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        TextView text;

        TextButtonViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_text_button);
            text1 = itemView.findViewById(R.id.textView1_text_button);
            text2 = itemView.findViewById(R.id.textView2_text_button);
        }

        @SuppressLint("SetTextI18n")
        void bind(@NonNull TextButtonItem item) {
            text.setText(item.getTitle());
            text1.setText(item.getText1());
            text2.setText(item.getText2());
            text1.setOnClickListener(v -> {
                listener.onClickChanged(item.getText1Key());
            });
            text2.setOnClickListener(v -> {
                listener.onClickChanged(item.getText2Key());
            });
        }
    }
    class SwitchViewHolder extends RecyclerView.ViewHolder {
        List<String> types;
        View view;
        int pos;
        TextView text;
        TextView value;

        SwitchViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_switch);
            value = itemView.findViewById(R.id.textView1_switch);
            view = itemView;
        }

        @SuppressLint("SetTextI18n")
        void bind(@NonNull SwitchItem item) {
            text.setText(item.getTitle());
            types = item.getTypes();
            pos = item.getPos();
            view.setOnClickListener(v -> {
                if(pos < types.size()-1){
                    pos++;
                } else pos = 0;
                value.setText(types.get(pos));
                listener.onSwitchChanged(item.getKey(), pos);
            });
        }
    }
}

